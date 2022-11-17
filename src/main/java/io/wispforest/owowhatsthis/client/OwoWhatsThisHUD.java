package io.wispforest.owowhatsthis.client;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.container.VerticalFlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.hud.Hud;
import io.wispforest.owo.util.OwoFreezer;
import io.wispforest.owowhatsthis.OwoWhatsThis;
import io.wispforest.owowhatsthis.information.InformationProvider;
import io.wispforest.owowhatsthis.information.TargetType;
import io.wispforest.owowhatsthis.network.DataUpdatePacket;
import io.wispforest.owowhatsthis.network.OwoWhatsThisNetworking;
import io.wispforest.owowhatsthis.network.RequestDataPacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class OwoWhatsThisHUD {

    public static final Identifier COMPONENT_ID = OwoWhatsThis.id("tooltip");

    private static final Map<Identifier, TargetType<?>> SORTED_TARGET_TYPES = new LinkedHashMap<>();
    private static int currentHash = 0;

    private static final Map<InformationProvider<?, ?>, Object> PROVIDER_DATA = new HashMap<>();
    private static int lastUpdateHash = 0;

    @SuppressWarnings("unchecked")
    public static void initialize() {
        OwoFreezer.registerFreezeCallback(() -> {
            OwoWhatsThis.TARGET_TYPES.streamEntries()
                    .sorted(Comparator.comparingInt(entry -> -entry.value().priority()))
                    .forEach(entry -> SORTED_TARGET_TYPES.put(entry.getKey().map(RegistryKey::getValue).orElseThrow(), entry.value()));
        });

        Hud.add(COMPONENT_ID, () -> {
            return Containers.verticalFlow(Sizing.content(), Sizing.content())
                    .padding(Insets.of(3))
                    .positioning(Positioning.relative(50, 0))
                    .margins(Insets.top(5));
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null) return;

            var component = Hud.getComponent(COMPONENT_ID);
            if (!(component instanceof VerticalFlowLayout view)) return;

            final var target = OwoWhatsThis.raycast(client.player, 1);

            view.<FlowLayout>configure(layout -> {
                view.clearChildren();
                view.surface(Surface.BLANK);

                for (var type : SORTED_TARGET_TYPES.values()) {
                    var transformed = type.transformer().apply(client.world, target);
                    if (transformed == null) continue;

                    view.surface(Surface.flat(0x77000000).and(Surface.outline(0x77000000)));
                    view.child(
                            Containers.grid(Sizing.content(), Sizing.content(), 2, 2).<GridLayout>configure(grid -> {
                                grid.verticalAlignment(VerticalAlignment.CENTER);

                                final var preview = ((TargetType.DisplayAdapter<Object>) DisplayAdapters.get(type)).buildPreview(transformed);

                                grid.child(Containers.verticalFlow(Sizing.content(), Sizing.content()).child(preview.preview()).padding(Insets.right(5)), 0, 0);
                                grid.child(Components.label(preview.name()).shadow(true), 0, 1);

                                grid.child(
                                        Containers.verticalFlow(Sizing.content(), Sizing.content()).<FlowLayout>configure(infoView -> {
                                            infoView.gap(4).margins(Insets.top(5));

                                            boolean mustRefresh = false;
                                            for (var provider : OwoWhatsThis.INFORMATION_PROVIDERS) {
                                                if (provider.applicableTargetType() != type) continue;

                                                if (provider.client()) {
                                                    var infoTransformed = ((InformationProvider<Object, ?>) provider).transformer().apply(client.world, transformed);
                                                    if (infoTransformed == null) continue;

                                                    infoView.child(
                                                            ((InformationProvider.DisplayAdapter<Object>) DisplayAdapters.get(provider)).build(infoTransformed)
                                                    );
                                                } else {
                                                    if (!PROVIDER_DATA.containsKey(provider)) continue;
                                                    if (provider.live()) mustRefresh = true;
                                                    infoView.child(
                                                            ((InformationProvider.DisplayAdapter<Object>) DisplayAdapters.get(provider)).build(PROVIDER_DATA.get(provider))
                                                    );
                                                }
                                            }

                                            int newHash = transformed.hashCode();

                                            if (newHash != currentHash || mustRefresh) {
                                                var targetBuf = PacketByteBufs.create();
                                                targetBuf.writeRegistryValue(OwoWhatsThis.TARGET_TYPES, type);
                                                ((TargetType<Object>) type).serializer().accept(targetBuf, transformed);
                                                OwoWhatsThisNetworking.CHANNEL.clientHandle().send(new RequestDataPacket(newHash, newHash != currentHash, targetBuf));
                                            }

                                            currentHash = newHash;
                                        }),
                                        1,
                                        1
                                );
                            })
                    );
                }
            });
        });
    }

    @SuppressWarnings("ConstantConditions")
    public static void loadProviderData(DataUpdatePacket message) {
        if (message.nonce() != currentHash) return;

        if (lastUpdateHash != message.nonce()) {
            lastUpdateHash = message.nonce();
            PROVIDER_DATA.clear();
        }

        final var buffer = message.data();
        final var dataCount = buffer.readVarInt();

        for (int i = 0; i < dataCount; i++) {
            var provider = buffer.readRegistryValue(OwoWhatsThis.INFORMATION_PROVIDERS);
            var data = provider.serializer().deserializer().apply(buffer);
            PROVIDER_DATA.put(provider, data);
        }
    }

}

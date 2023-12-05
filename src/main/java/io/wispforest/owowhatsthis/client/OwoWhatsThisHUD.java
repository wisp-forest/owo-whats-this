package io.wispforest.owowhatsthis.client;

import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.hud.Hud;
import io.wispforest.owowhatsthis.OwoWhatsThis;
import io.wispforest.owowhatsthis.RateLimitTracker;
import io.wispforest.owowhatsthis.TooltipObjectManager;
import io.wispforest.owowhatsthis.information.InformationProvider;
import io.wispforest.owowhatsthis.information.TargetType;
import io.wispforest.owowhatsthis.mixin.PlayerListHudAccessor;
import io.wispforest.owowhatsthis.network.DataUpdatePacket;
import io.wispforest.owowhatsthis.network.OwoWhatsThisNetworking;
import io.wispforest.owowhatsthis.network.RequestDataPacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class OwoWhatsThisHUD {

    public static final Identifier COMPONENT_ID = OwoWhatsThis.id("tooltip");

    private static final Map<InformationProvider<?, ?>, Object> PROVIDER_DATA = new HashMap<>();
    private static final RateLimitTracker rateLimit = new RateLimitTracker();

    private static int currentHash = 0;

    @SuppressWarnings("unchecked")
    public static void initialize() {

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> rateLimit.reset());

        Hud.add(COMPONENT_ID, () -> {
            return Containers.verticalFlow(Sizing.content(), Sizing.content())
                    .padding(Insets.of(3))
                    .positioning(Positioning.relative(50, 0))
                    .margins(Insets.top(5));
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null) return;

            var component = Hud.getComponent(COMPONENT_ID);
            if (!(component instanceof FlowLayout view)) return;

            final var target = OwoWhatsThis.raycast(client.player, 1);

            view.<FlowLayout>configure(layout -> {
                view.clearChildren();
                view.surface(Surface.BLANK);

                if (!OwoWhatsThis.CONFIG.enableTooltip()) return;
                if (((PlayerListHudAccessor) MinecraftClient.getInstance().inGameHud.getPlayerListHud()).whatsThis$isVisible()) return;

                for (var type : TooltipObjectManager.sortedTargetTypes()) {
                    var transformed = type.transformer().apply(client.world, target);
                    if (transformed == null) continue;

                    view.surface(Surface.flat(OwoWhatsThis.CONFIG.tooltipColor().argb()).and(Surface.outline(OwoWhatsThis.CONFIG.tooltipBorderColor().argb())));

                    view.child(
                            Containers.grid(Sizing.content(), Sizing.content(), 2, 2).<GridLayout>configure(grid -> {
                                grid.verticalAlignment(VerticalAlignment.CENTER);

                                final var preview = ((TargetType.DisplayAdapter<Object>) DisplayAdapters.get(type)).buildPreview(transformed);

                                grid.child(Containers.verticalFlow(Sizing.content(), Sizing.content()).child(preview.preview()).padding(Insets.right(5)), 0, 0);
                                grid.child(preview.title(), 0, 1);

                                grid.child(
                                        Containers.verticalFlow(Sizing.content(), Sizing.content()).<FlowLayout>configure(infoView -> {
                                            infoView.gap(4).margins(Insets.top(5));

                                            int newHash = transformed.hashCode();
                                            final var targetChanged = newHash != currentHash;

                                            if (targetChanged) {
                                                PROVIDER_DATA.clear();
                                            }

                                            boolean mustRefresh = false;
                                            for (var provider : TooltipObjectManager.getProviders(type, client.player.isSneaking())) {
                                                if (provider.client()) {
                                                    var infoTransformed = ((InformationProvider<Object, ?>) provider).transformer().apply(client.player, client.world, transformed);
                                                    if (infoTransformed == null) continue;

                                                    infoView.child(
                                                            ((InformationProvider.DisplayAdapter<Object>) DisplayAdapters.get(provider)).build(infoTransformed)
                                                    );
                                                } else {
                                                    if (provider.live()) {
                                                        mustRefresh = true;
                                                    }
                                                    if (!PROVIDER_DATA.containsKey(provider)) continue;

                                                    infoView.child(
                                                            ((InformationProvider.DisplayAdapter<Object>) DisplayAdapters.get(provider)).build(PROVIDER_DATA.get(provider))
                                                    );
                                                }
                                            }

                                            if (targetChanged) rateLimit.setOverride(0);
                                            if ((targetChanged || mustRefresh) && rateLimit.update(client.world.getTime())) {
                                                var targetBuf = PacketByteBufs.create();
                                                targetBuf.writeRegistryValue(OwoWhatsThis.TARGET_TYPE, type);
                                                ((TargetType<Object>) type).serializer().accept(transformed, targetBuf);
                                                OwoWhatsThisNetworking.CHANNEL.clientHandle().send(new RequestDataPacket(newHash, targetBuf));
                                            }

                                            currentHash = newHash;
                                        }),
                                        1,
                                        1
                                );
                            })
                    );

                    return;
                }
            });
        });
    }

    @SuppressWarnings("ConstantConditions")
    public static void readProviderData(DataUpdatePacket message) {
        if (message.nonce() != currentHash) return;
        rateLimit.clearOverride();

        for (var liveProvider : TooltipObjectManager.liveProviders()) PROVIDER_DATA.remove(liveProvider);

        final var buffer = message.data();
        final var dataCount = buffer.readVarInt();

        for (int i = 0; i < dataCount; i++) {
            var provider = buffer.readRegistryValue(OwoWhatsThis.INFORMATION_PROVIDER);
            var data = buffer.read(provider.endec());
            PROVIDER_DATA.put(provider, data);
        }

        buffer.release();
    }

}

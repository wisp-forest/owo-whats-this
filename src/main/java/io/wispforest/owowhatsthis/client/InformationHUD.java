package io.wispforest.owowhatsthis.client;

import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.VerticalFlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.hud.Hud;
import io.wispforest.owo.util.OwoFreezer;
import io.wispforest.owowhatsthis.OwoWhatsThis;
import io.wispforest.owowhatsthis.information.TargetType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

public class InformationHUD {

    public static final Identifier COMPONENT_ID = OwoWhatsThis.id("information-component");

    private static final Map<Identifier, TargetType<?>> SORTED_TARGET_TYPES = new LinkedHashMap<>();

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

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
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
                            ((TargetType.DisplayAdapter<Object>) DisplayAdapters.get(type)).build(transformed)
                    );
                }
            });
        });
    }

}

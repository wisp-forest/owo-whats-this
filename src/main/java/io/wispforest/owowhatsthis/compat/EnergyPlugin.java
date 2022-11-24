package io.wispforest.owowhatsthis.compat;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owowhatsthis.NumberFormatter;
import io.wispforest.owowhatsthis.OwoWhatsThis;
import io.wispforest.owowhatsthis.client.DisplayAdapters;
import io.wispforest.owowhatsthis.information.InformationProvider;
import io.wispforest.owowhatsthis.information.TargetType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import team.reborn.energy.api.EnergyStorage;

public class EnergyPlugin implements OwoWhatsThisPlugin {

    private static final Direction[] ALL_DIRECTIONS = Direction.values();

    @Override
    public boolean shouldLoad() {
        return FabricLoader.getInstance().isModLoaded("team_reborn_energy");
    }

    @Override
    public void loadServer() {
        Registry.register(OwoWhatsThis.INFORMATION_PROVIDER, OwoWhatsThis.id("block_energy_storage"), BLOCK_ENERGY_STORAGE);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void loadClient() {
        DisplayAdapters.register(BLOCK_ENERGY_STORAGE, DisplayAdapter.ENERGY_STORAGE);
    }

    public static final InformationProvider<BlockPos, EnergyStorageData> BLOCK_ENERGY_STORAGE = InformationProvider.server(
            TargetType.BLOCK, true, 0, EnergyStorageData.class,
            (player, world, target) -> {
                for (var side : ALL_DIRECTIONS) {
                    var storage = EnergyStorage.SIDED.find(world, target, side);
                    if (storage == null) continue;

                    return new EnergyStorageData(storage.getAmount(), storage.getCapacity());
                }

                return null;
            }
    );

    public record EnergyStorageData(long stored, long capacity) {}

    @Environment(EnvType.CLIENT)
    public static class DisplayAdapter {
        public static final InformationProvider.DisplayAdapter<EnergyStorageData> ENERGY_STORAGE = data -> {
            final var energyText = Text.translatable(
                    "text.owo-whats-this.tooltip.blockEnergyAmount",
                    NumberFormatter.quantity(data.stored, "E"),
                    NumberFormatter.quantity(data.capacity, "E")
            );

            final int barWidth = Math.max(
                    MinecraftClient.getInstance().textRenderer.getWidth(energyText) + 15,
                    110
            );

            return Containers.horizontalFlow(Sizing.fixed(barWidth), Sizing.fixed(12)).<FlowLayout>configure(flowLayout -> {
                flowLayout.padding(Insets.of(1)).surface(Surface.outline(0xA7000000));

                int width = Math.round(barWidth * (data.stored / (float) data.capacity));
                while (width > 0) {
                    flowLayout.child(
                            Components.texture(OwoWhatsThis.id("textures/gui/energy_bar.png"), 0, 0, 32, 10, 32, 16)
                                    .visibleArea(PositionedRectangle.of(0, 0, Math.min(32, width), 10))
                    );
                    width -= 32;
                }

                flowLayout.child(
                        Components.label(energyText)
                                .positioning(Positioning.relative(0, 50))
                                .margins(Insets.left(5))
                );
            });
        };
    }
}

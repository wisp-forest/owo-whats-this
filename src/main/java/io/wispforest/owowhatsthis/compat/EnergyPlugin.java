package io.wispforest.owowhatsthis.compat;

import io.wispforest.owowhatsthis.NumberFormatter;
import io.wispforest.owowhatsthis.OwoWhatsThis;
import io.wispforest.owowhatsthis.client.DisplayAdapters;
import io.wispforest.owowhatsthis.client.component.TexturedProgressBarComponent;
import io.wispforest.owowhatsthis.information.BlockStateWithPosition;
import io.wispforest.owowhatsthis.information.InformationProvider;
import io.wispforest.owowhatsthis.information.TargetType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
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

    public static final InformationProvider<BlockStateWithPosition, EnergyStorageData> BLOCK_ENERGY_STORAGE = InformationProvider.server(
            TargetType.BLOCK, true, 0, EnergyStorageData.class,
            (player, world, target) -> {
                for (var side : ALL_DIRECTIONS) {
                    var storage = EnergyStorage.SIDED.find(world, target.pos(), side);
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

            return TexturedProgressBarComponent.ofTexture(
                    energyText, data.stored / (float) data.capacity, OwoWhatsThis.id("textures/gui/energy_bar.png"),
                    32, 16, 32, 10
            );
        };
    }
}

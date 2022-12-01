package io.wispforest.owowhatsthis.compat;


import io.wispforest.owo.ui.core.Color;
import io.wispforest.owowhatsthis.NumberFormatter;
import io.wispforest.owowhatsthis.OwoWhatsThis;
import io.wispforest.owowhatsthis.client.DisplayAdapters;
import io.wispforest.owowhatsthis.client.component.ColoredProgressBarComponent;
import io.wispforest.owowhatsthis.information.BlockStateWithPosition;
import io.wispforest.owowhatsthis.information.InformationProvider;
import io.wispforest.owowhatsthis.information.TargetType;
import io.wispforest.owowhatsthis.mixin.compat.ForgeControllerBlockEntityAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

public class AlloyForgeryPlugin implements OwoWhatsThisPlugin {

    @Override
    public boolean shouldLoad() {
        return FabricLoader.getInstance().isModLoaded("alloy_forgery");
    }

    @Override
    public void loadServer() {
        Registry.register(OwoWhatsThis.INFORMATION_PROVIDER, OwoWhatsThis.id("block_alloy_forge_fuel"), BLOCK_ALLOY_FORGE_FUEL);
    }

    @Override
    public void loadClient() {
        DisplayAdapters.register(BLOCK_ALLOY_FORGE_FUEL, DisplayAdapter.FUEL);
    }

    public static final InformationProvider<BlockStateWithPosition, FuelData> BLOCK_ALLOY_FORGE_FUEL = InformationProvider.server(
            TargetType.BLOCK,
            true, 0, FuelData.class,
            (player, world, target) -> {
                if (!(world.getBlockEntity(target.pos()) instanceof ForgeControllerBlockEntityAccessor controller)) return null;

                return new FuelData(controller.whatsthis$fuel(), controller.whatsthis$forgeDefinition().fuelCapacity());
            }
    );

    public record FuelData(float stored, int capacity) {}

    @Environment(EnvType.CLIENT)
    public static class DisplayAdapter {
        public static final InformationProvider.DisplayAdapter<FuelData> FUEL = data -> {
            final var fuelText = Text.translatable(
                    "text.owo-whats-this.tooltip.blockAlloyForgeFuel",
                    NumberFormatter.quantityText(data.stored, ""),
                    NumberFormatter.quantityText(data.capacity, "")
            );


            return new ColoredProgressBarComponent(fuelText)
                    .progress(data.stored / (float) data.capacity)
                    .color(Color.ofRgb(0xFFAC1D));
        };
    }
}

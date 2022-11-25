package io.wispforest.owowhatsthis.compat;


import io.wispforest.owowhatsthis.NumberFormatter;
import io.wispforest.owowhatsthis.OwoWhatsThis;
import io.wispforest.owowhatsthis.client.DisplayAdapters;
import io.wispforest.owowhatsthis.information.BlockStateWithPosition;
import io.wispforest.owowhatsthis.information.InformationProvider;
import io.wispforest.owowhatsthis.information.InformationProviders;
import io.wispforest.owowhatsthis.information.TargetType;
import io.wispforest.owowhatsthis.mixin.compat.ForgeControllerBlockEntityAccessor;
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
        DisplayAdapters.register(BLOCK_ALLOY_FORGE_FUEL, InformationProviders.DisplayAdapters.TEXT);
    }

    public static final InformationProvider<BlockStateWithPosition, Text> BLOCK_ALLOY_FORGE_FUEL = InformationProvider.server(
            TargetType.BLOCK,
            true, 0, Text.class,
            (player, world, target) -> {
                if (!(world.getBlockEntity(target.pos()) instanceof ForgeControllerBlockEntityAccessor controller)) return null;

                return Text.translatable(
                        "text.owo-whats-this.tooltip.blockAlloyForgeFuel",
                        NumberFormatter.quantityText(controller.whatsthis$fuel(), ""),
                        NumberFormatter.quantityText(controller.whatsthis$forgeDefinition().fuelCapacity(), "")
                );
            }
    );
}

package io.wispforest.owowhatsthis.client;

import io.wispforest.owo.config.ui.ConfigScreen;
import io.wispforest.owowhatsthis.client.component.OwoWhatsThisEntityComponent;
import io.wispforest.owowhatsthis.compat.OwoWhatsThisPlugin;
import io.wispforest.owowhatsthis.information.InformationProviders;
import io.wispforest.owowhatsthis.information.TargetType;
import io.wispforest.owowhatsthis.network.OwoWhatsThisNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class OwoWhatsThisClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        DisplayAdapters.register(TargetType.BLOCK, TargetType.DisplayAdapter.BLOCK);
        DisplayAdapters.register(TargetType.ENTITY, TargetType.DisplayAdapter.ENTITY);
        DisplayAdapters.register(TargetType.PLAYER, TargetType.DisplayAdapter.PLAYER);
        DisplayAdapters.register(TargetType.FLUID, TargetType.DisplayAdapter.FLUID);

        DisplayAdapters.register(InformationProviders.BLOCK_HARDNESS, InformationProviders.DisplayAdapters.TEXT);
        DisplayAdapters.register(InformationProviders.BLOCK_ITEM_STORAGE, InformationProviders.DisplayAdapters.ITEM_STACK_LIST);
        DisplayAdapters.register(InformationProviders.BLOCK_FLUID_STORAGE, InformationProviders.DisplayAdapters.FLUID_STORAGE_LIST);
        DisplayAdapters.register(InformationProviders.BLOCK_HARVESTABILITY, InformationProviders.DisplayAdapters.TEXT);
        DisplayAdapters.register(InformationProviders.BLOCK_BREAKING_PROGRESS, InformationProviders.DisplayAdapters.BREAKING_PROGRESS);
        DisplayAdapters.register(InformationProviders.BLOCK_CROP_GROWTH, InformationProviders.DisplayAdapters.TEXT);

        DisplayAdapters.register(InformationProviders.FLUID_VISCOSITY, InformationProviders.DisplayAdapters.TEXT);

        DisplayAdapters.register(InformationProviders.ENTITY_HEALTH_AND_ARMOR, InformationProviders.DisplayAdapters.ENTITY_HEALTH);
        DisplayAdapters.register(InformationProviders.ENTITY_STATUS_EFFECTS, InformationProviders.DisplayAdapters.TEXT);
        DisplayAdapters.register(InformationProviders.ENTITY_GROWING_TIME, InformationProviders.DisplayAdapters.TEXT);
        DisplayAdapters.register(InformationProviders.ENTITY_BREEDING_COOLDOWN, InformationProviders.DisplayAdapters.TEXT);
        DisplayAdapters.register(InformationProviders.ENTITY_TNT_FUSE, InformationProviders.DisplayAdapters.TEXT);
        DisplayAdapters.register(InformationProviders.ENTITY_ITEM_COUNT, InformationProviders.DisplayAdapters.TEXT);

        DisplayAdapters.register(InformationProviders.PLAYER_INVENTORY, InformationProviders.DisplayAdapters.ITEM_STACK_LIST);

        OwoWhatsThisEntityComponent.registerSpecialHandler(EntityType.ITEM, component -> {
            component.scale(component.scale() * .65f);
            component.transform(matrices -> {
                matrices.translate(0, -.15, 0);
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(35));
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-component.entity().getBodyYaw()));
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-35));
            });
        });

        for (var entrypoint : FabricLoader.getInstance().getEntrypoints("owo-whats-this-plugin", OwoWhatsThisPlugin.class)) {
            if (!entrypoint.shouldLoad()) continue;
            entrypoint.loadClient();
        }

        OwoWhatsThisHUD.initialize();
        OwoWhatsThisNetworking.initializeClient();

        ConfigScreen.registerProvider("owo-whats-this", OwoWhatsThisConfigScreen::new);
    }
}

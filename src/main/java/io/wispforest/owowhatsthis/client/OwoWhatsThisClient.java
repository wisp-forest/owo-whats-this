package io.wispforest.owowhatsthis.client;

import io.wispforest.owowhatsthis.information.InformationProviders;
import io.wispforest.owowhatsthis.information.TargetType;
import io.wispforest.owowhatsthis.network.OwoWhatsThisNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class OwoWhatsThisClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        DisplayAdapters.register(TargetType.BLOCK, TargetType.DisplayAdapter.BLOCK);
        DisplayAdapters.register(TargetType.ENTITY, TargetType.DisplayAdapter.ENTITY);

        DisplayAdapters.register(InformationProviders.BLOCK_HARDNESS, InformationProviders.DisplayAdapters.TEXT);
        DisplayAdapters.register(InformationProviders.BLOCK_INVENTORY, InformationProviders.DisplayAdapters.ITEM_STACK_LIST);
        DisplayAdapters.register(InformationProviders.BLOCK_FLUID_STORAGE, InformationProviders.DisplayAdapters.FLUID_STORAGE_LIST);
        DisplayAdapters.register(InformationProviders.BLOCK_HARVESTABILITY, InformationProviders.DisplayAdapters.TEXT);

        DisplayAdapters.register(InformationProviders.ENTITY_HEALTH, InformationProviders.DisplayAdapters.ENTITY_HEALTH);
        DisplayAdapters.register(InformationProviders.ENTITY_STATUS_EFFECTS, InformationProviders.DisplayAdapters.TEXT);

        OwoWhatsThisHUD.initialize();

        OwoWhatsThisNetworking.initializeClient();
    }
}

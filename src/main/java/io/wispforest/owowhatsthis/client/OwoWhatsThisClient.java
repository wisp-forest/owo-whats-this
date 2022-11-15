package io.wispforest.owowhatsthis.client;

import io.wispforest.owowhatsthis.information.TargetType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class OwoWhatsThisClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        DisplayAdapters.register(TargetType.BLOCK, TargetType.DisplayAdapter.BLOCK);
        DisplayAdapters.register(TargetType.ENTITY, TargetType.DisplayAdapter.ENTITY);

        InformationHUD.initialize();
    }
}

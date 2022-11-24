package io.wispforest.owowhatsthis.compat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface OwoWhatsThisPlugin {

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    default boolean shouldLoad() {
        return true;
    }

    void loadServer();

    @Environment(EnvType.CLIENT)
    void loadClient();

}

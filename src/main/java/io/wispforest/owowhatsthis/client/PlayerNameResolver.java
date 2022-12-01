package io.wispforest.owowhatsthis.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerNameResolver {

    private static final Map<UUID, Text> NAME_CACHE = new HashMap<>();

    public static @Nullable Text getName(MinecraftServer server, UUID uuid) {
        if (NAME_CACHE.containsKey(uuid)) return NAME_CACHE.get(uuid);

        var presentPlayer = server.getPlayerManager().getPlayer(uuid);
        if (presentPlayer != null) {
            NAME_CACHE.put(uuid, presentPlayer.getName());
            return presentPlayer.getName();
        }

        var profile = new GameProfile(uuid, null);
        Util.getIoWorkerExecutor().submit(() -> {
            server.getSessionService().fillProfileProperties(profile, false);
            if (profile.getName() == null) return;

            NAME_CACHE.put(uuid, Text.literal(profile.getName()));
        });

        NAME_CACHE.put(uuid, null);
        return null;
    }

}

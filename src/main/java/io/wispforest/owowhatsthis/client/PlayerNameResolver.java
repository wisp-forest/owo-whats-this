package io.wispforest.owowhatsthis.client;

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

        var userCache = server.getUserCache();
        if (userCache != null && userCache.getByUuid(uuid).isPresent()) {
            NAME_CACHE.put(uuid, Text.literal(userCache.getByUuid(uuid).get().getName()));
            return NAME_CACHE.get(uuid);
        }

        Util.getIoWorkerExecutor().submit(() -> {
            var profile = server.getSessionService().fetchProfile(uuid, false);
            if (profile == null) return;

            NAME_CACHE.put(uuid, Text.literal(profile.profile().getName()));
        });

        NAME_CACHE.put(uuid, null);
        return null;
    }

}

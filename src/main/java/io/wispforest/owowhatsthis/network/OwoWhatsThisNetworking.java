package io.wispforest.owowhatsthis.network;

import io.wispforest.owo.network.OwoNetChannel;
import io.wispforest.owowhatsthis.OwoWhatsThis;
import io.wispforest.owowhatsthis.RateLimitTracker;
import io.wispforest.owowhatsthis.TooltipObjectManager;
import io.wispforest.owowhatsthis.client.OwoWhatsThisHUD;
import io.wispforest.owowhatsthis.information.InformationProvider;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class OwoWhatsThisNetworking {

    public static final OwoNetChannel CHANNEL = OwoNetChannel.create(OwoWhatsThis.id("main"));

    private static final Object2ObjectMap<UUID, ClientData> CLIENT_DATA = new Object2ObjectLinkedOpenHashMap<>();

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public static void initialize() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            CLIENT_DATA.remove(handler.player.getUuid());
        });

        CHANNEL.registerServerbound(RequestDataPacket.class, (message, access) -> {
            var type = message.targetData().readRegistryValue(OwoWhatsThis.TARGET_TYPE);
            var target = type.deserializer().apply(access, message.targetData());
            if (target == null) return;

            if (!updateRateLimit(access.player(), target.hashCode())) return;

            var buffer = PacketByteBufs.create();
            var applicableProviders = new HashMap<InformationProvider<Object, Object>, Object>();

            for (var provider : TooltipObjectManager.getProviders(type, access.player().isSneaking())) {
                if (provider.client()) continue;
                applicableProviders.put(
                        (InformationProvider<Object, Object>) provider,
                        ((InformationProvider<Object, Object>) provider).transformer().apply(access.player(), access.player().world, target)
                );
            }

            applicableProviders.values().removeIf(Objects::isNull);

            buffer.writeVarInt(applicableProviders.size());
            applicableProviders.forEach((provider, transformed) -> {
                buffer.writeRegistryValue(OwoWhatsThis.INFORMATION_PROVIDER, provider);
                provider.serializer().serializer().accept(buffer, transformed);
            });

            CHANNEL.serverHandle(access.player()).send(new DataUpdatePacket(message.nonce(), buffer));
        });

        CHANNEL.registerClientboundDeferred(DataUpdatePacket.class);
    }

    private static boolean updateRateLimit(ServerPlayerEntity player, int nonce) {
        var data = CLIENT_DATA.computeIfAbsent(player.getUuid(), uuid -> new ClientData());

        if (data.lastTargetNonce != nonce) {
            data.lastTargetNonce = nonce;
            data.rateLimit.setOverride(OwoWhatsThis.CONFIG.updateDelay() / 2);
        }

        if (data.rateLimit.update(player.getWorld().getTime())) {
            data.rateLimit.clearOverride();
            return true;
        } else {
            return false;
        }
    }

    @Environment(EnvType.CLIENT)
    public static void initializeClient() {
        CHANNEL.registerClientbound(DataUpdatePacket.class, (message, access) -> {
            OwoWhatsThisHUD.readProviderData(message);
        });
    }

    private static class ClientData {
        public int lastTargetNonce = -1;
        public final RateLimitTracker rateLimit = new RateLimitTracker();
    }
}

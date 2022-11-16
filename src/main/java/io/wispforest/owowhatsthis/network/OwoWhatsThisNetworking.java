package io.wispforest.owowhatsthis.network;

import io.wispforest.owo.network.OwoNetChannel;
import io.wispforest.owowhatsthis.OwoWhatsThis;
import io.wispforest.owowhatsthis.client.OwoWhatsThisHUD;
import io.wispforest.owowhatsthis.information.InformationProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

import java.util.ArrayList;

public class OwoWhatsThisNetworking {

    public static final OwoNetChannel CHANNEL = OwoNetChannel.create(OwoWhatsThis.id("main"));

    @SuppressWarnings("unchecked")
    public static void initialize() {
        CHANNEL.registerServerbound(RequestDataPacket.class, (message, access) -> {
            var target = OwoWhatsThis.raycast(access.player(), 0);
            for (var type : OwoWhatsThis.TARGET_TYPES) {
                var transformed = type.transformer().apply(access.player().world, target);
                if (transformed == null) continue;

                var buffer = PacketByteBufs.create();
                var applicableProviders = new ArrayList<InformationProvider<Object, Object>>();

                for (var provider : OwoWhatsThis.INFORMATION_PROVIDERS) {
                    if (provider.applicableTargetType != type) continue;
                    applicableProviders.add((InformationProvider<Object, Object>) provider);
                }

                buffer.writeVarInt(applicableProviders.size());
                for (var provider : applicableProviders) {
                    buffer.writeRegistryValue(OwoWhatsThis.INFORMATION_PROVIDERS, provider);
                    provider.serializer.serializer().accept(buffer, provider.apply(access.player().world, transformed));
                }

                CHANNEL.serverHandle(access.player()).send(new DataUpdatePacket(message.nonce(), buffer));
                return;
            }
        });

        CHANNEL.registerClientboundDeferred(DataUpdatePacket.class);
    }

    @Environment(EnvType.CLIENT)
    public static void initializeClient() {
        CHANNEL.registerClientbound(DataUpdatePacket.class, (message, access) -> {
            if (OwoWhatsThisHUD.currentHash() != message.nonce()) return;
            OwoWhatsThisHUD.loadProviderData(message.data());
        });
    }

}

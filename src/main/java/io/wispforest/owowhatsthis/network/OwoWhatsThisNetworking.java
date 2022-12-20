package io.wispforest.owowhatsthis.network;

import io.wispforest.owo.network.OwoNetChannel;
import io.wispforest.owowhatsthis.OwoWhatsThis;
import io.wispforest.owowhatsthis.TooltipObjectManager;
import io.wispforest.owowhatsthis.client.OwoWhatsThisHUD;
import io.wispforest.owowhatsthis.information.InformationProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

import java.util.HashMap;
import java.util.Objects;

public class OwoWhatsThisNetworking {

    public static final OwoNetChannel CHANNEL = OwoNetChannel.create(OwoWhatsThis.id("main"));

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public static void initialize() {
        CHANNEL.registerServerbound(RequestDataPacket.class, (message, access) -> {
            var type = message.targetData().readRegistryValue(OwoWhatsThis.TARGET_TYPE);
            var target = type.deserializer().apply(access, message.targetData());
            if (target == null) return;

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

    @Environment(EnvType.CLIENT)
    public static void initializeClient() {
        CHANNEL.registerClientbound(DataUpdatePacket.class, (message, access) -> {
            OwoWhatsThisHUD.readProviderData(message);
        });
    }
}

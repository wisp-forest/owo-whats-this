package io.wispforest.owowhatsthis.network;

import net.minecraft.network.PacketByteBuf;

public record DataUpdatePacket(int nonce, PacketByteBuf data) {}

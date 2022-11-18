package io.wispforest.owowhatsthis.network;

import net.minecraft.network.PacketByteBuf;

public record RequestDataPacket(int nonce, PacketByteBuf targetData) {}

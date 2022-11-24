package io.wispforest.owowhatsthis.information;

import io.wispforest.owo.network.ServerAccess;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public record FluidStateWithPosition(BlockPos pos, FluidState state) {
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public static FluidStateWithPosition read(ServerAccess access, PacketByteBuf buf) {
        var pos = buf.readBlockPos();
        if (access.player().getPos().squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) > 75) return null;

        return new FluidStateWithPosition(
                pos,
                access.player().world.getFluidState(pos)
        );
    }
}

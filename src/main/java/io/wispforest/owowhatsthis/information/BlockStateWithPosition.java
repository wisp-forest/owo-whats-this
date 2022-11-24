package io.wispforest.owowhatsthis.information;

import io.wispforest.owo.network.ServerAccess;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public record BlockStateWithPosition(BlockPos pos, BlockState state) {
    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public static BlockStateWithPosition read(ServerAccess access, PacketByteBuf buf) {
        var pos = buf.readBlockPos();
        if (access.player().getPos().squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) > 75) return null;

        return new BlockStateWithPosition(
                pos,
                access.player().world.getBlockState(pos)
        );
    }
}

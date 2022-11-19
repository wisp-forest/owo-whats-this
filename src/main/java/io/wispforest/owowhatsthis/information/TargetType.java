package io.wispforest.owowhatsthis.information;

import io.wispforest.owo.network.ServerAccess;
import io.wispforest.owo.ops.TextOps;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owowhatsthis.FluidToVariant;
import io.wispforest.owowhatsthis.OwoWhatsThis;
import io.wispforest.owowhatsthis.client.component.AligningEntityComponent;
import io.wispforest.owowhatsthis.client.component.ColoringComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registries;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public record TargetType<T>(BiFunction<World, HitResult, @Nullable T> transformer, BiConsumer<PacketByteBuf, T> serializer,
                            BiFunction<ServerAccess, PacketByteBuf, @Nullable T> deserializer, int priority) {

    public static final TargetType<BlockPos> BLOCK = new TargetType<>(
            (world, hitResult) -> hitResult instanceof BlockHitResult blockHit
                    && blockHit.getType() == HitResult.Type.BLOCK
                    && !world.getBlockState(blockHit.getBlockPos()).isAir()
                    ? blockHit.getBlockPos()
                    : null,
            PacketByteBuf::writeBlockPos,
            TargetType::readBlockPos,
            0
    );

    public static final TargetType<BlockPos> FLUID = new TargetType<>(
            (world, hitResult) -> hitResult instanceof BlockHitResult blockHit
                    && blockHit.getType() == HitResult.Type.BLOCK
                    && world.getBlockState(blockHit.getBlockPos()).getBlock() instanceof FluidBlock
                    && !world.getFluidState(blockHit.getBlockPos()).isEmpty()
                    ? blockHit.getBlockPos()
                    : null,
            PacketByteBuf::writeBlockPos,
            TargetType::readBlockPos,
            10
    );

    public static final TargetType<Entity> ENTITY = new TargetType<>(
            (world, hitResult) -> hitResult instanceof EntityHitResult entityHit ? entityHit.getEntity() : null,
            (buf, entity) -> buf.writeVarInt(entity.getId()),
            (access, buf) -> access.player().world.getEntityById(buf.readVarInt()),
            20
    );

    private static BlockPos readBlockPos(ServerAccess access, PacketByteBuf buf) {
        var pos = buf.readBlockPos();
        if (access.player().getPos().squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) > 75) return null;
        return pos;
    }

    @Environment(EnvType.CLIENT)
    public interface DisplayAdapter<T> {

        DisplayAdapter<BlockPos> BLOCK = blockPos -> {
            var targetState = MinecraftClient.getInstance().world.getBlockState(blockPos);

            return new PreviewData(
                    TextOps.concat(
                            targetState.getBlock().getName(),
                            Text.literal("\n").append(
                                    TextOps.withFormatting(OwoWhatsThis.modNameOf(Registries.BLOCK.getId(targetState.getBlock())), Formatting.BLUE)
                            )
                    ),
                    Components.item(targetState.getBlock().asItem().getDefaultStack())
            );
        };

        @SuppressWarnings("UnstableApiUsage")
        DisplayAdapter<BlockPos> FLUID = blockPos -> {
            var fluidVariant = FluidToVariant.apply(MinecraftClient.getInstance().world.getFluidState(blockPos));

            return new PreviewData(
                    TextOps.concat(
                            FluidVariantAttributes.getName(fluidVariant),
                            Text.literal("\n").append(
                                    TextOps.withFormatting(OwoWhatsThis.modNameOf(Registries.FLUID.getId(fluidVariant.getFluid())), Formatting.BLUE)
                            )
                    ),
                    new ColoringComponent<>(
                            Color.ofArgb(FluidVariantRendering.getColor(fluidVariant)),
                            Components.sprite(FluidVariantRendering.getSprite(fluidVariant))
                    )
            );
        };

        DisplayAdapter<Entity> ENTITY = entity -> new PreviewData(
                TextOps.concat(
                        entity.getDisplayName(),
                        Text.literal("\n").append(
                                TextOps.withFormatting(OwoWhatsThis.modNameOf(Registries.ENTITY_TYPE.getId(entity.getType())), Formatting.BLUE)
                        )
                ),
                new AligningEntityComponent<>(Sizing.fixed(24), entity).scaleToFit(true)
        );

        PreviewData buildPreview(T value);
    }

    @Environment(EnvType.CLIENT)
    public record PreviewData(Text name, Component preview) {}

}

package io.wispforest.owowhatsthis.information;

import io.wispforest.owo.network.ServerAccess;
import io.wispforest.owo.ops.TextOps;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owowhatsthis.FluidToVariant;
import io.wispforest.owowhatsthis.OwoWhatsThis;
import io.wispforest.owowhatsthis.client.component.ColoringComponent;
import io.wispforest.owowhatsthis.client.component.OwoWhatsThisEntityComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
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
                            BiFunction<ServerAccess, PacketByteBuf, @Nullable T> deserializer, int priority, @Nullable TargetType<? super T> parent) {

    public TargetType {
        if (parent != null && parent.priority >= priority) {
            throw new IllegalArgumentException("Parent priority must be lower than own priority");
        }
    }

    public static final TargetType<BlockPos> BLOCK = new TargetType<>(
            (world, hitResult) -> hitResult instanceof BlockHitResult blockHit
                    && blockHit.getType() == HitResult.Type.BLOCK
                    && !world.getBlockState(blockHit.getBlockPos()).isAir()
                    ? blockHit.getBlockPos()
                    : null,
            PacketByteBuf::writeBlockPos,
            TargetType::readBlockPos,
            0, null
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
            10, null
    );

    public static final TargetType<Entity> ENTITY = new TargetType<>(
            (world, hitResult) -> hitResult instanceof EntityHitResult entityHit ? entityHit.getEntity() : null,
            (buf, entity) -> buf.writeVarInt(entity.getId()),
            (access, buf) -> access.player().world.getEntityById(buf.readVarInt()),
            20, null
    );

    public static final TargetType<PlayerEntity> PLAYER = new TargetType<>(
            (world, hitResult) -> hitResult instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof PlayerEntity player ? player : null,
            (buf, player) -> buf.writeVarInt(player.getId()),
            (access, buf) -> (PlayerEntity) access.player().world.getEntityById(buf.readVarInt()),
            30, ENTITY
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
            var previewItem = targetState.getBlock().asItem().getDefaultStack();

            return new PreviewData(
                    Components.label(
                            TextOps.concat(
                                    targetState.getBlock().getName(),
                                    Text.literal("\n").append(
                                            TextOps.withFormatting(OwoWhatsThis.modNameOf(Registries.BLOCK.getId(targetState.getBlock())), Formatting.BLUE)
                                    )
                            )
                    ).shadow(true),
                    previewItem.isEmpty()
                            ? Containers.verticalFlow(Sizing.fixed(0), Sizing.fixed(0))
                            : Components.item(previewItem)
            );
        };

        @SuppressWarnings("UnstableApiUsage")
        DisplayAdapter<BlockPos> FLUID = blockPos -> {
            var fluidVariant = FluidToVariant.apply(MinecraftClient.getInstance().world.getFluidState(blockPos));

            return new PreviewData(
                    Components.label(
                            TextOps.concat(
                                    FluidVariantAttributes.getName(fluidVariant),
                                    Text.literal("\n").append(
                                            TextOps.withFormatting(OwoWhatsThis.modNameOf(Registries.FLUID.getId(fluidVariant.getFluid())), Formatting.BLUE)
                                    )
                            )
                    ).shadow(true),
                    new ColoringComponent<>(
                            Color.ofArgb(FluidVariantRendering.getColor(fluidVariant)),
                            Components.sprite(FluidVariantRendering.getSprite(fluidVariant))
                    )
            );
        };

        DisplayAdapter<Entity> ENTITY = entity -> new PreviewData(
                Components.label(
                        TextOps.concat(
                                entity.getDisplayName(),
                                Text.literal("\n").append(
                                        TextOps.withFormatting(OwoWhatsThis.modNameOf(Registries.ENTITY_TYPE.getId(entity.getType())), Formatting.BLUE)
                                )
                        )
                ).shadow(true),
                new OwoWhatsThisEntityComponent<>(Sizing.fixed(24), entity).scaleToFit(true)
        );

        DisplayAdapter<PlayerEntity> PLAYER = player -> {
            int pingStep = 0;

            var playerListEntry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(player.getUuid());
            if (playerListEntry != null) {
                int ping = playerListEntry.getLatency();

                if (ping < 0) {
                    pingStep = 5;
                } else if (ping < 150) {
                    pingStep = 0;
                } else if (ping < 300) {
                    pingStep = 1;
                } else if (ping < 600) {
                    pingStep = 2;
                } else if (ping < 1000) {
                    pingStep = 3;
                } else {
                    pingStep = 4;
                }
            }

            var entityPreview = ENTITY.buildPreview(player);
            return new PreviewData(
                    Containers.horizontalFlow(Sizing.content(), Sizing.content())
                            .child(entityPreview.title())
                            .child(
                                    Components.texture(
                                            InGameHud.GUI_ICONS_TEXTURE,
                                            0, 176 + pingStep * 8, 10, 8
                                    )
                            ).gap(3),
                    entityPreview.preview()
            );
        };

        PreviewData buildPreview(T value);
    }

    @Environment(EnvType.CLIENT)
    public record PreviewData(Component title, Component preview) {}

}

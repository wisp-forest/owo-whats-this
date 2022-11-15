package io.wispforest.owowhatsthis.information;

import io.wispforest.owo.ops.TextOps;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.VerticalAlignment;
import io.wispforest.owowhatsthis.OwoWhatsThis;
import io.wispforest.owowhatsthis.client.component.AligningEntityComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registries;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public record TargetType<T>(BiFunction<World, HitResult, @Nullable T> transformer, int priority) {

    public static final TargetType<BlockPos> BLOCK = new TargetType<>(
            (world, hitResult) -> hitResult instanceof BlockHitResult blockHit && !world.getBlockState(blockHit.getBlockPos()).isAir()
                    ? blockHit.getBlockPos()
                    : null,
            0
    );

    public static final TargetType<Entity> ENTITY = new TargetType<>(
            (world, hitResult) -> hitResult instanceof EntityHitResult entityHit ? entityHit.getEntity() : null,
            10
    );

    @Environment(EnvType.CLIENT)
    public interface DisplayAdapter<T> {

        DisplayAdapter<BlockPos> BLOCK = blockPos -> {
            return Containers.horizontalFlow(Sizing.content(), Sizing.content()).<FlowLayout>configure(titleRow -> {
                titleRow.gap(5).verticalAlignment(VerticalAlignment.CENTER);

                var targetState = MinecraftClient.getInstance().world.getBlockState(blockPos);
                var title = TextOps.concat(
                        targetState.getBlock().getName(),
                        Text.literal("\n").append(
                                TextOps.withFormatting(OwoWhatsThis.modNameOf(Registries.BLOCK.getId(targetState.getBlock())), Formatting.BLUE)
                        )
                );

                titleRow.child(Components.item(targetState.getBlock().asItem().getDefaultStack()));
                titleRow.child(Components.label(title).shadow(true));
            });
        };

        DisplayAdapter<Entity> ENTITY = entity -> {
            return Containers.horizontalFlow(Sizing.content(), Sizing.content()).<FlowLayout>configure(titleRow -> {
                titleRow.gap(5).verticalAlignment(VerticalAlignment.CENTER);

                var title = TextOps.concat(
                        entity.getDisplayName(),
                        Text.literal("\n").append(
                                TextOps.withFormatting(OwoWhatsThis.modNameOf(Registries.ENTITY_TYPE.getId(entity.getType())), Formatting.BLUE)
                        )
                );

                titleRow.child(new AligningEntityComponent<>(Sizing.fixed(24), entity).scaleToFit(true));
                titleRow.child(Components.label(title).shadow(true));
            });
        };

        Component build(T value);
    }

}

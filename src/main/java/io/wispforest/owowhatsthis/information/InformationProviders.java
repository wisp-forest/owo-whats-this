package io.wispforest.owowhatsthis.information;

import io.wispforest.owo.ui.component.Components;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class InformationProviders {

    public static final InformationProvider<BlockPos, Text> BLOCK_HARDNESS = new InformationProvider<>(
            TargetType.BLOCK,
            (world, blockPos) -> Text.literal("Hardness: " + world.getBlockState(blockPos).getHardness(world, blockPos)),
            Text.class
    );

    public static final InformationProvider<Entity, Text> ENTITY_HEALTH = new InformationProvider<>(
            TargetType.ENTITY,
            (world, entity) -> Text.literal("Health: " + (entity instanceof LivingEntity living ? living.getHealth() + "/" + living.getMaxHealth() : "0")),
            Text.class
    );

    @Environment(EnvType.CLIENT)
    public static class DisplayAdapters {

        public static final InformationProvider.DisplayAdapter<Text> TEXT = Components::label;

    }

}

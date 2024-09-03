package io.wispforest.owowhatsthis.information;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

import java.util.List;

// MiningLevelManager was entirely removed, so we have to make do.
public class MiningLevelHack {
    private static final List<TagKey<Block>> TAGS = List.of(
        BlockTags.NEEDS_STONE_TOOL,
        BlockTags.NEEDS_IRON_TOOL,
        BlockTags.NEEDS_DIAMOND_TOOL
    );

    public static TagKey<Block> getRequiredMiningLeveltag(BlockState state) {
        for (var tag : TAGS) {
            if (state.isIn(tag)) return tag;
        }

        return null;
    }
}

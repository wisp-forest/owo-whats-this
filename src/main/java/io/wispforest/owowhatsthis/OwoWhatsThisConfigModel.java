package io.wispforest.owowhatsthis;

import io.wispforest.owo.config.annotation.*;
import net.fabricmc.fabric.api.mininglevel.v1.FabricMineableTags;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Config(name = "owo-whats-this", wrapperName = "OwoWhatsThisConfig")
public class OwoWhatsThisConfigModel {

    @SectionHeader("main")
    public boolean includeFluids = false;

    @RegexConstraint("#[0-9a-fA-F]{0,8}")
    public String tooltipColor = "#77000000";
    @RegexConstraint("#[0-9a-fA-F]{0,8}")
    public String tooltipBorderColor = "#77000000";

    @RangeConstraint(min = 0, max = 5)
    public int decimalPlaces = 1;

    @Hook
    public List<String> effectiveToolTags = new ArrayList<>(
            List.of(
                    BlockTags.AXE_MINEABLE.id().toString(),
                    BlockTags.PICKAXE_MINEABLE.id().toString(),
                    BlockTags.SHOVEL_MINEABLE.id().toString(),
                    BlockTags.HOE_MINEABLE.id().toString(),
                    FabricMineableTags.SHEARS_MINEABLE.id().toString(),
                    FabricMineableTags.SWORD_MINEABLE.id().toString()
            )
    );

    @Hook
    public List<String> miningLevelTags = new ArrayList<>(
            List.of(
                    BlockTags.NEEDS_STONE_TOOL.id().toString(),
                    BlockTags.NEEDS_IRON_TOOL.id().toString(),
                    BlockTags.NEEDS_DIAMOND_TOOL.id().toString()
            )
    );

    @Hook
    @SectionHeader("providers")
    public Set<Identifier> disabledProviders = new HashSet<>();

}

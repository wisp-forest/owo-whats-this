package io.wispforest.owowhatsthis;

import io.wispforest.owo.config.annotation.*;
import net.fabricmc.fabric.api.mininglevel.v1.FabricMineableTags;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * Mapping from {@code provider id -> allow while sneaking}
     * <p>
     * {@code <id> -> true} means shown while sneaking
     * <br>
     * {@code <id> -> false} means always hide
     */
    @Hook
    @SectionHeader("providers")
    public Map<Identifier, Boolean> disabledProviders = new HashMap<>();

    public enum ProviderState {
        ENABLED, WHEN_SNEAKING, DISABLED
    }
}

package io.wispforest.owowhatsthis;

import io.wispforest.owo.config.annotation.*;
import net.minecraft.util.Identifier;

import java.util.*;

@Config(name = "owo-whats-this", wrapperName = "OwoWhatsThisConfig")
public class OwoWhatsThisConfigModel {

    @SectionHeader("main")
    public boolean includeFluids = false;

    @RegexConstraint("#[0-9a-fA-F]{0,8}")
    public String tooltipColor = "#77000000";
    @RegexConstraint("#[0-9a-fA-F]{0,8}")
    public String tooltipBorderColor = "#77000000";

    @Hook
    public List<String> effectiveToolTags = new ArrayList<>(
            List.of("minecraft:mineable/axe", "minecraft:mineable/pickaxe", "minecraft:mineable/shovel", "minecraft:mineable/hoe")
    );

    @Hook
    @SectionHeader("providers")
    public Set<Identifier> disabledProviders = new HashSet<>();

}

package io.wispforest.owowhatsthis;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Hook;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.RegexConstraint;

import java.util.ArrayList;
import java.util.List;

@Modmenu(modId = "owo-whats-this")
@Config(name = "owo-whats-this", wrapperName = "OwoWhatsThisConfig")
public class OwoWhatsThisConfigModel {

    public boolean includeFluids = false;

    @RegexConstraint("#[0-9a-fA-F]{0,8}")
    public String tooltipColor = "#77000000";
    @RegexConstraint("#[0-9a-fA-F]{0,8}")
    public String tooltipBorderColor = "#77000000";

    @Hook
    public List<String> effectiveToolTags = new ArrayList<>(
            List.of("minecraft:mineable/axe", "minecraft:mineable/pickaxe", "minecraft:mineable/shovel", "minecraft:mineable/hoe")
    );

}

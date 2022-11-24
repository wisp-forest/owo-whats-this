package io.wispforest.owowhatsthis;

import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.util.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class OwoWhatsThisConfig extends ConfigWrapper<io.wispforest.owowhatsthis.OwoWhatsThisConfigModel> {

    private final Option<java.lang.Boolean> includeFluids = this.optionForKey(new Option.Key("includeFluids"));
    private final Option<java.lang.String> tooltipColor = this.optionForKey(new Option.Key("tooltipColor"));
    private final Option<java.lang.String> tooltipBorderColor = this.optionForKey(new Option.Key("tooltipBorderColor"));
    private final Option<java.util.List<java.lang.String>> effectiveToolTags = this.optionForKey(new Option.Key("effectiveToolTags"));
    private final Option<java.util.List<java.lang.String>> miningLevelTags = this.optionForKey(new Option.Key("miningLevelTags"));
    private final Option<java.util.Set<net.minecraft.util.Identifier>> disabledProviders = this.optionForKey(new Option.Key("disabledProviders"));

    private OwoWhatsThisConfig() {
        super(io.wispforest.owowhatsthis.OwoWhatsThisConfigModel.class);
    }

    public static OwoWhatsThisConfig createAndLoad() {
        var wrapper = new OwoWhatsThisConfig();
        wrapper.load();
        return wrapper;
    }

    public boolean includeFluids() {
        return includeFluids.value();
    }

    public void includeFluids(boolean value) {
        includeFluids.set(value);
    }

    public java.lang.String tooltipColor() {
        return tooltipColor.value();
    }

    public void tooltipColor(java.lang.String value) {
        tooltipColor.set(value);
    }

    public java.lang.String tooltipBorderColor() {
        return tooltipBorderColor.value();
    }

    public void tooltipBorderColor(java.lang.String value) {
        tooltipBorderColor.set(value);
    }

    public java.util.List<java.lang.String> effectiveToolTags() {
        return effectiveToolTags.value();
    }

    public void effectiveToolTags(java.util.List<java.lang.String> value) {
        effectiveToolTags.set(value);
    }

    public void subscribeToEffectiveToolTags(Consumer<java.util.List<java.lang.String>> subscriber) {
        effectiveToolTags.observe(subscriber);
    }

    public java.util.List<java.lang.String> miningLevelTags() {
        return miningLevelTags.value();
    }

    public void miningLevelTags(java.util.List<java.lang.String> value) {
        miningLevelTags.set(value);
    }

    public void subscribeToMiningLevelTags(Consumer<java.util.List<java.lang.String>> subscriber) {
        miningLevelTags.observe(subscriber);
    }

    public java.util.Set<net.minecraft.util.Identifier> disabledProviders() {
        return disabledProviders.value();
    }

    public void disabledProviders(java.util.Set<net.minecraft.util.Identifier> value) {
        disabledProviders.set(value);
    }

    public void subscribeToDisabledProviders(Consumer<java.util.Set<net.minecraft.util.Identifier>> subscriber) {
        disabledProviders.observe(subscriber);
    }




}


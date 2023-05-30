package io.wispforest.owowhatsthis;

import blue.endless.jankson.Jankson;
import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.util.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class OwoWhatsThisConfig extends ConfigWrapper<io.wispforest.owowhatsthis.OwoWhatsThisConfigModel> {

    public final Keys keys = new Keys();

    private final Option<java.lang.Boolean> enableTooltip = this.optionForKey(this.keys.enableTooltip);
    private final Option<java.lang.Boolean> showFluids = this.optionForKey(this.keys.showFluids);
    private final Option<io.wispforest.owo.ui.core.Color> tooltipColor = this.optionForKey(this.keys.tooltipColor);
    private final Option<io.wispforest.owo.ui.core.Color> tooltipBorderColor = this.optionForKey(this.keys.tooltipBorderColor);
    private final Option<java.lang.Integer> decimalPlaces = this.optionForKey(this.keys.decimalPlaces);
    private final Option<java.lang.Integer> maxItemContainerPreviewRows = this.optionForKey(this.keys.maxItemContainerPreviewRows);
    private final Option<java.lang.Integer> updateDelay = this.optionForKey(this.keys.updateDelay);
    private final Option<java.util.List<java.lang.String>> effectiveToolTags = this.optionForKey(this.keys.effectiveToolTags);
    private final Option<java.util.Map<net.minecraft.util.Identifier,java.lang.Boolean>> disabledProviders = this.optionForKey(this.keys.disabledProviders);

    private OwoWhatsThisConfig() {
        super(io.wispforest.owowhatsthis.OwoWhatsThisConfigModel.class);
    }

    private OwoWhatsThisConfig(Consumer<Jankson.Builder> janksonBuilder) {
        super(io.wispforest.owowhatsthis.OwoWhatsThisConfigModel.class, janksonBuilder);
    }

    public static OwoWhatsThisConfig createAndLoad() {
        var wrapper = new OwoWhatsThisConfig();
        wrapper.load();
        return wrapper;
    }

    public static OwoWhatsThisConfig createAndLoad(Consumer<Jankson.Builder> janksonBuilder) {
        var wrapper = new OwoWhatsThisConfig(janksonBuilder);
        wrapper.load();
        return wrapper;
    }

    public boolean enableTooltip() {
        return enableTooltip.value();
    }

    public void enableTooltip(boolean value) {
        enableTooltip.set(value);
    }

    public boolean showFluids() {
        return showFluids.value();
    }

    public void showFluids(boolean value) {
        showFluids.set(value);
    }

    public io.wispforest.owo.ui.core.Color tooltipColor() {
        return tooltipColor.value();
    }

    public void tooltipColor(io.wispforest.owo.ui.core.Color value) {
        tooltipColor.set(value);
    }

    public io.wispforest.owo.ui.core.Color tooltipBorderColor() {
        return tooltipBorderColor.value();
    }

    public void tooltipBorderColor(io.wispforest.owo.ui.core.Color value) {
        tooltipBorderColor.set(value);
    }

    public int decimalPlaces() {
        return decimalPlaces.value();
    }

    public void decimalPlaces(int value) {
        decimalPlaces.set(value);
    }

    public int maxItemContainerPreviewRows() {
        return maxItemContainerPreviewRows.value();
    }

    public void maxItemContainerPreviewRows(int value) {
        maxItemContainerPreviewRows.set(value);
    }

    public int updateDelay() {
        return updateDelay.value();
    }

    public void updateDelay(int value) {
        updateDelay.set(value);
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

    public java.util.Map<net.minecraft.util.Identifier,java.lang.Boolean> disabledProviders() {
        return disabledProviders.value();
    }

    public void disabledProviders(java.util.Map<net.minecraft.util.Identifier,java.lang.Boolean> value) {
        disabledProviders.set(value);
    }

    public void subscribeToDisabledProviders(Consumer<java.util.Map<net.minecraft.util.Identifier,java.lang.Boolean>> subscriber) {
        disabledProviders.observe(subscriber);
    }


    public static class Keys {
        public final Option.Key enableTooltip = new Option.Key("enableTooltip");
        public final Option.Key showFluids = new Option.Key("showFluids");
        public final Option.Key tooltipColor = new Option.Key("tooltipColor");
        public final Option.Key tooltipBorderColor = new Option.Key("tooltipBorderColor");
        public final Option.Key decimalPlaces = new Option.Key("decimalPlaces");
        public final Option.Key maxItemContainerPreviewRows = new Option.Key("maxItemContainerPreviewRows");
        public final Option.Key updateDelay = new Option.Key("updateDelay");
        public final Option.Key effectiveToolTags = new Option.Key("effectiveToolTags");
        public final Option.Key disabledProviders = new Option.Key("disabledProviders");
    }
}


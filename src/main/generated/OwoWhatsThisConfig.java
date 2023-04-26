package io.wispforest.owowhatsthis;

import blue.endless.jankson.Jankson;
import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.util.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class OwoWhatsThisConfig extends ConfigWrapper<io.wispforest.owowhatsthis.OwoWhatsThisConfigModel> {

    private final Option<java.lang.Boolean> enableTooltip = this.optionForKey(new Option.Key("enableTooltip"));
    private final Option<java.lang.Boolean> showFluids = this.optionForKey(new Option.Key("showFluids"));
    private final Option<io.wispforest.owo.ui.core.Color> tooltipColor = this.optionForKey(new Option.Key("tooltipColor"));
    private final Option<io.wispforest.owo.ui.core.Color> tooltipBorderColor = this.optionForKey(new Option.Key("tooltipBorderColor"));
    private final Option<java.lang.Integer> decimalPlaces = this.optionForKey(new Option.Key("decimalPlaces"));
    private final Option<java.lang.Integer> maxItemContainerPreviewRows = this.optionForKey(new Option.Key("maxItemContainerPreviewRows"));
    private final Option<java.lang.Integer> updateDelay = this.optionForKey(new Option.Key("updateDelay"));
    private final Option<java.util.List<java.lang.String>> effectiveToolTags = this.optionForKey(new Option.Key("effectiveToolTags"));
    private final Option<java.util.Map<net.minecraft.util.Identifier,java.lang.Boolean>> disabledProviders = this.optionForKey(new Option.Key("disabledProviders"));

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




}


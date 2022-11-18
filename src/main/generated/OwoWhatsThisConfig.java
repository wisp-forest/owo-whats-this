package io.wispforest.owowhatsthis;

import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.util.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class OwoWhatsThisConfig extends ConfigWrapper<io.wispforest.owowhatsthis.OwoWhatsThisConfigModel> {

    private final Option<java.lang.Boolean> includeFluids = this.optionForKey(new Option.Key("includeFluids"));
    private final Option<java.util.List<java.lang.String>> effectiveToolTags = this.optionForKey(new Option.Key("effectiveToolTags"));

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

    public java.util.List<java.lang.String> effectiveToolTags() {
        return effectiveToolTags.value();
    }

    public void effectiveToolTags(java.util.List<java.lang.String> value) {
        effectiveToolTags.set(value);
    }

    public void subscribeToEffectiveToolTags(Consumer<java.util.List<java.lang.String>> subscriber) {
        effectiveToolTags.observe(subscriber);
    }




}


package io.wispforest.owowhatsthis;

import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.util.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class OwoWhatsThisConfig extends ConfigWrapper<io.wispforest.owowhatsthis.OwoWhatsThisConfigModel> {

    private final Option<java.lang.Boolean> includeFluids = this.optionForKey(new Option.Key("includeFluids"));

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




}


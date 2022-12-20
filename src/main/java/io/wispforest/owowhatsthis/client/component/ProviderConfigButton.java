package io.wispforest.owowhatsthis.client.component;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.ui.component.ConfigEnumButton;
import io.wispforest.owo.util.Observable;
import io.wispforest.owowhatsthis.OwoWhatsThisConfigModel.ProviderState;
import net.minecraft.text.Text;

import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public class ProviderConfigButton extends ConfigEnumButton {

    protected final Observable<ProviderState> listeners = Observable.of(null);

    public ProviderConfigButton() {
        this.backingValues = ProviderState.values();
    }

    public ProviderConfigButton init(ProviderState selected) {
        this.selectedIndex = selected.ordinal();
        this.updateMessage();
        return this;
    }

    public ProviderConfigButton onChanged(Consumer<ProviderState> listener) {
        this.listeners.observe(listener);
        return this;
    }

    @Override
    protected void updateMessage() {
        if (this.backingValues == null) return;

        var selected = (ProviderState) this.backingValues[this.selectedIndex];
        this.listeners.set(selected);

        this.setMessage(switch (selected) {
            case ENABLED -> Text.translatable("text.owo-whats-this.config.provider_toggle.enabled");
            case WHEN_SNEAKING -> Text.translatable("text.owo-whats-this.config.provider_toggle.when_sneaking");
            case DISABLED -> Text.translatable("text.owo-whats-this.config.provider_toggle.disabled");
        });
    }

    @Override
    @Deprecated
    public ConfigEnumButton init(Option<? extends Enum<?>> option, int selectedIndex) {
        return super.init(option, selectedIndex);
    }
}

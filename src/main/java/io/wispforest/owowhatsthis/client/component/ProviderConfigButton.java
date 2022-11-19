package io.wispforest.owowhatsthis.client.component;

import io.wispforest.owo.config.ui.component.ConfigToggleButton;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public class ProviderConfigButton extends ConfigToggleButton {

    protected static final Text ENABLED_MESSAGE = Text.translatable("text.owo-whats-this.config.provider_toggle.enabled");
    protected static final Text DISABLED_MESSAGE = Text.translatable("text.owo-whats-this.config.provider_toggle.disabled");

    protected final List<Consumer<Boolean>> listeners = new ArrayList<>();

    @Override
    protected void updateMessage() {
        this.setMessage(this.enabled ? ENABLED_MESSAGE : DISABLED_MESSAGE);
    }

    @Override
    public void onPress() {
        super.onPress();
        for (var listener : this.listeners) listener.accept(this.enabled);
    }

    public ProviderConfigButton onChanged(Consumer<Boolean> listener) {
        this.listeners.add(listener);
        return this;
    }
}

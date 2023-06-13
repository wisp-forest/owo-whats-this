package io.wispforest.owowhatsthis.client.component;

import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.AnimatableProperty;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.util.Formatting;

public class ProgressBarComponent extends BaseComponent {

    protected AnimatableProperty<Color> trackColor = AnimatableProperty.of(Color.ofArgb(0x77000000));
    protected AnimatableProperty<Color> barColor = AnimatableProperty.of(Color.ofFormatting(Formatting.BLUE));

    protected float progress = 0f;

    public ProgressBarComponent(Sizing horizontalSizing, Sizing verticalSizing) {
        this.sizing(horizontalSizing, verticalSizing);
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        context.fill(x, y, this.x + this.width, this.y + this.height, this.trackColor.get().argb());
        context.fill(x, y, this.x + (int) (this.width * this.progress), this.y + this.height, this.barColor.get().argb());
    }

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        super.update(delta, mouseX, mouseY);
        this.trackColor.update(delta);
        this.barColor.update(delta);
    }

    public ProgressBarComponent progress(float progress) {
        this.progress = progress;
        return this;
    }

    public float progress() {
        return this.progress;
    }

    public ProgressBarComponent trackColor(Color trackColor) {
        this.trackColor.set(trackColor);
        return this;
    }

    public AnimatableProperty<Color> trackColor() {
        return this.trackColor;
    }

    public ProgressBarComponent barColor(Color barColor) {
        this.barColor.set(barColor);
        return this;
    }

    public AnimatableProperty<Color> barColor() {
        return this.barColor;
    }
}

package io.wispforest.owowhatsthis.client.component;

import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.util.Drawer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Formatting;

public class ProgressBarComponent extends BaseComponent {

    protected Color trackColor = Color.ofArgb(0x77000000);
    protected Color barColor = Color.ofFormatting(Formatting.BLUE);

    protected float progress = 0f;

    public ProgressBarComponent(Sizing horizontalSizing, Sizing verticalSizing) {
        this.sizing(horizontalSizing, verticalSizing);
    }

    @Override
    public void draw(MatrixStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
        Drawer.fill(matrices, x, y, this.x + this.width, this.y + this.height, this.trackColor.argb());
        Drawer.fill(matrices, x, y, this.x + (int) (this.width * this.progress), this.y + this.height, this.barColor.argb());
    }

    public ProgressBarComponent progress(float progress) {
        this.progress = progress;
        return this;
    }

    public float progress() {
        return this.progress;
    }

    public ProgressBarComponent trackColor(Color trackColor) {
        this.trackColor = trackColor;
        return this;
    }

    public Color trackColor() {
        return this.trackColor;
    }

    public ProgressBarComponent barColor(Color barColor) {
        this.barColor = barColor;
        return this;
    }

    public Color barColor() {
        return this.barColor;
    }
}

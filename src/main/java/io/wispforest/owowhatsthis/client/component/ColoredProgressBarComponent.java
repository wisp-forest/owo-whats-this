package io.wispforest.owowhatsthis.client.component;

import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.HorizontalFlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.util.NinePatchRenderer;
import io.wispforest.owo.ui.util.ScissorStack;
import io.wispforest.owowhatsthis.OwoWhatsThis;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ColoredProgressBarComponent extends HorizontalFlowLayout {

    protected static final NinePatchRenderer BAR_RENDERER = new NinePatchRenderer(
            OwoWhatsThis.id("textures/gui/progress_bar.png"),
            0, 0, Size.of(1, 1), Size.of(30, 6), Size.of(32, 16), true
    );

    protected final AnimatableProperty<Color> color = AnimatableProperty.of(Color.WHITE);
    protected float progress = 0f;

    public ColoredProgressBarComponent(Text message) {
        super(Sizing.fixed(Math.max(110, MinecraftClient.getInstance().textRenderer.getWidth(message) + 10)), Sizing.fixed(12));
        this.surface(Surface.outline(0xA7000000));
        this.child(
                Components.label(message)
                        .positioning(Positioning.relative(0, 50))
                        .margins(Insets.left(5))
        );
    }

    @Override
    public void draw(MatrixStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
        final var color = this.color.get();
        RenderSystem.setShaderColor(color.red(), color.green(), color.blue(), color.alpha());

        RenderSystem.defaultBlendFunc();
        RenderSystem.enableBlend();

        ScissorStack.push(this.x + 1, this.y + 1, (int) ((this.width - 2) * progress), this.height - 2, matrices);
        BAR_RENDERER.draw(matrices, this.x, this.y, this.width, this.height);
        ScissorStack.pop();

        RenderSystem.setShaderColor(1, 1, 1, 1);

        super.draw(matrices, mouseX, mouseY, partialTicks, delta);
    }

    public ColoredProgressBarComponent color(Color color) {
        this.color.set(color);
        return this;
    }

    public AnimatableProperty<Color> color() {
        return this.color;
    }

    public ColoredProgressBarComponent progress(float progress) {
        this.progress = progress;
        return this;
    }

    public float progress() {
        return this.progress;
    }
}

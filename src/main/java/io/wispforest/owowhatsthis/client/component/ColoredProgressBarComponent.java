package io.wispforest.owowhatsthis.client.component;

import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.StackLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.util.NinePatchTexture;
import io.wispforest.owowhatsthis.OwoWhatsThis;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ColoredProgressBarComponent extends StackLayout {

    protected static final Identifier COLORED_PROGRESS_BAR = OwoWhatsThis.id("colored_progress_bar");

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
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        final var color = this.color.get();
        RenderSystem.setShaderColor(color.red(), color.green(), color.blue(), color.alpha());

        RenderSystem.defaultBlendFunc();
        RenderSystem.enableBlend();
        NinePatchTexture.draw(COLORED_PROGRESS_BAR, context, this.x + 1, this.y + 1, (int) ((this.width - 2) * progress), this.height - 2);

        RenderSystem.setShaderColor(1, 1, 1, 1);

        super.draw(context, mouseX, mouseY, partialTicks, delta);
    }

    @Override
    protected void parentUpdate(float delta, int mouseX, int mouseY) {
        super.parentUpdate(delta, mouseX, mouseY);
        this.color.update(delta);
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

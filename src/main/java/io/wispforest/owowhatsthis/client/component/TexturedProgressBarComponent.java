package io.wispforest.owowhatsthis.client.component;

import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.util.ScissorStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TexturedProgressBarComponent extends FlowLayout {

    protected final DrawFunction drawFunction;

    protected float progress = 0.5f;
    protected Color color = Color.WHITE;

    protected TexturedProgressBarComponent(Text message, DrawFunction drawFunction) {
        super(Sizing.fixed(Math.max(110, MinecraftClient.getInstance().textRenderer.getWidth(message) + 10)), Sizing.fixed(12), Algorithm.HORIZONTAL);
        this.drawFunction = drawFunction;
        this.surface(Surface.outline(0xA7000000));
        this.child(
                Components.label(message)
                        .positioning(Positioning.relative(0, 50))
                        .margins(Insets.left(5))
        );
    }

    public static TexturedProgressBarComponent ofTexture(Text message, float progress, Identifier texture, int textureWidth, int textureHeight, int regionWidth, int regionHeight) {
        return new TexturedProgressBarComponent(message, (context, x, y) -> {
            context.drawTexture(texture, x, y, regionWidth, regionHeight, 0, 0, regionWidth, regionHeight, textureWidth, textureHeight);
            return regionWidth;
        }).progress(progress);
    }

    public static TexturedProgressBarComponent ofSprite(Text message, float progress, Sprite sprite) {
        return new TexturedProgressBarComponent(message, (context, x, y) -> {
            context.drawSprite(x, y, 0, sprite.getContents().getWidth(), sprite.getContents().getHeight(), sprite);
            return sprite.getContents().getWidth();
        }).progress(progress);
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        int barWidth = (int) ((this.width - 2) * this.progress);

        ScissorStack.push(this.x + 1, this.y + 1, barWidth, this.height - 2, context.getMatrices());
        RenderSystem.setShaderColor(this.color.red(), this.color.green(), this.color.blue(), this.color.alpha());

        int width = 0;
        while (width < barWidth) {
            width += this.drawFunction.draw(context, this.x + 1 + width, this.y + 1);
        }

        ScissorStack.pop();
        RenderSystem.setShaderColor(1, 1, 1, 1);

        super.draw(context, mouseX, mouseY, partialTicks, delta);
    }

    public TexturedProgressBarComponent progress(float progress) {
        this.progress = progress;
        return this;
    }

    public float progress() {
        return this.progress;
    }

    public TexturedProgressBarComponent color(Color color) {
        this.color = color;
        return this;
    }

    public Color color() {
        return this.color;
    }

    @FunctionalInterface
    public interface DrawFunction {
        int draw(OwoUIDrawContext context, int x, int y);
    }
}

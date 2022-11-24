package io.wispforest.owowhatsthis.client.component;

import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.HorizontalFlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.util.Drawer;
import io.wispforest.owo.ui.util.ScissorStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TexturedProgressBarComponent extends HorizontalFlowLayout {

    protected final DrawFunction drawFunction;

    protected float progress = 0.5f;
    protected Color color = Color.WHITE;

    protected TexturedProgressBarComponent(Text message, DrawFunction drawFunction) {
        super(Sizing.fixed(Math.max(110, MinecraftClient.getInstance().textRenderer.getWidth(message) + 15)), Sizing.fixed(12));
        this.drawFunction = drawFunction;
        this.surface(Surface.outline(0xA7000000));
        this.child(
                Components.label(message)
                        .positioning(Positioning.relative(0, 50))
                        .margins(Insets.left(5))
        );
    }

    public static TexturedProgressBarComponent ofTexture(Text message, float progress, Identifier texture, int textureWidth, int textureHeight, int regionWidth, int regionHeight) {
        return new TexturedProgressBarComponent(message, (matrices, x, y) -> {
            RenderSystem.setShaderTexture(0, texture);
            Drawer.drawTexture(matrices, x, y, regionWidth, regionHeight, 0, 0, regionWidth, regionHeight, textureWidth, textureHeight);
            return regionWidth;
        }).progress(progress);
    }

    public static TexturedProgressBarComponent ofSprite(Text message, float progress, Sprite sprite) {
        return new TexturedProgressBarComponent(message, (matrices, x, y) -> {
            RenderSystem.setShaderTexture(0, sprite.getAtlas().getId());
            Drawer.drawSprite(matrices, x, y, 0, sprite.getWidth(), sprite.getWidth(), sprite);
            return sprite.getWidth();
        }).progress(progress);
    }

    @Override
    public void draw(MatrixStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
        int barWidth = (int) ((this.width - 2) * this.progress);

        ScissorStack.push(this.x + 1, this.y + 1, barWidth, this.height - 2, matrices);
        RenderSystem.setShaderColor(this.color.red(), this.color.green(), this.color.blue(), this.color.alpha());

        int width = 0;
        while (width < barWidth) {
            width += this.drawFunction.draw(matrices, this.x + 1 + width, this.y + 1);
        }

        ScissorStack.pop();
        RenderSystem.setShaderColor(1, 1, 1, 1);

        super.draw(matrices, mouseX, mouseY, partialTicks, delta);
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
        int draw(MatrixStack matrices, int x, int y);
    }
}

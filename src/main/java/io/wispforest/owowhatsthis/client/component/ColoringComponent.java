package io.wispforest.owowhatsthis.client.component;

import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.container.WrappingParentComponent;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

public class ColoringComponent<C extends Component> extends WrappingParentComponent<C> {

    protected Color color;

    public ColoringComponent(Color color, C child) {
        super(Sizing.content(), Sizing.content(), child);
        this.color = color;
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        super.draw(context, mouseX, mouseY, partialTicks, delta);

        RenderSystem.setShaderColor(this.color.red(), this.color.green(), this.color.blue(), this.color.alpha());
        this.drawChildren(context, mouseX, mouseY, partialTicks, delta, List.of(this.child));
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    public Color color() {
        return this.color;
    }

    public ColoringComponent<C> color(Color color) {
        this.color = color;
        return this;
    }
}

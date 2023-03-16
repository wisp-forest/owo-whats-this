package io.wispforest.owowhatsthis.client.component;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.PositionedRectangle;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.gui.hud.InGameHud;

public class HeartSpriteComponent extends FlowLayout {

    public HeartSpriteComponent(float progress) {
        super(Sizing.fixed(9), Sizing.fixed(9), Algorithm.VERTICAL);

        this.child(
                Components.texture(InGameHud.GUI_ICONS_TEXTURE, 16, 0, 9, 9)
                        .positioning(Positioning.absolute(0, 0))
        );

        this.child(
                Components.texture(InGameHud.GUI_ICONS_TEXTURE, 52, 0, 9, 9)
                        .visibleArea(PositionedRectangle.of(0, 0, Math.round(9 * progress), 9))
                        .positioning(Positioning.absolute(0, 0))
        );
    }

}

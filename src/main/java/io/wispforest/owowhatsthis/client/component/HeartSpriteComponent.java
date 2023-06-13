package io.wispforest.owowhatsthis.client.component;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.StackLayout;
import io.wispforest.owo.ui.core.PositionedRectangle;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owowhatsthis.information.InformationProviders;

public class HeartSpriteComponent extends StackLayout {

    public HeartSpriteComponent(float progress) {
        super(Sizing.fixed(9), Sizing.fixed(9));

        this.child(Components.texture(InformationProviders.GUI_ICONS_TEXTURE, 16, 0, 9, 9));
        this.child(Components.texture(InformationProviders.GUI_ICONS_TEXTURE, 52, 0, 9, 9)
                .visibleArea(PositionedRectangle.of(0, 0, Math.round(9 * progress), 9)));
    }

}

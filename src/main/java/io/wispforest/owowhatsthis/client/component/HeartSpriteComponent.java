package io.wispforest.owowhatsthis.client.component;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.StackLayout;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class HeartSpriteComponent extends StackLayout {

    public HeartSpriteComponent(float progress) {
        super(Sizing.fixed(9), Sizing.fixed(9));

        var atlas = MinecraftClient.getInstance().getGuiAtlasManager();
        this.child(Components.sprite(atlas.getSprite(new Identifier("hud/heart/container"))));

        if (progress > 0) {
            this.child(Components.sprite(atlas.getSprite(progress <= .5f ? new Identifier("hud/heart/half") : new Identifier("hud/heart/full"))));
        }
    }

}

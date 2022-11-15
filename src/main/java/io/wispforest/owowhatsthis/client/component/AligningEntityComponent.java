package io.wispforest.owowhatsthis.client.component;

import io.wispforest.owo.ui.component.EntityComponent;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class AligningEntityComponent<E extends Entity> extends EntityComponent<E> {

    public AligningEntityComponent(Sizing sizing, E entity) {
        super(sizing, entity);
    }

    @Override
    public void draw(MatrixStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
        float prevBodyYaw = this.entity.getBodyYaw();
        float prevPrevBodyYaw = 0;

        float prevYaw = this.entity.getYaw();
        float prevPrevYaw = this.entity.prevYaw;

        float prevHeadYaw = this.entity.getHeadYaw();
        float prevPrevHeadYaw = 0;

        this.entity.setBodyYaw(-90);
        this.entity.setHeadYaw(-90);
        if (this.entity instanceof LivingEntity living) {
            prevPrevHeadYaw = living.prevHeadYaw;
            living.prevHeadYaw = -90;
            prevPrevBodyYaw = living.prevBodyYaw;
            living.prevBodyYaw = -90;
        }
        this.entity.prevYaw = 0;

        float prevPitch = this.entity.getPitch();
        float prevPrevPitch = entity.prevPitch;

        this.entity.setPitch(0);
        this.entity.prevPitch = 0;

        super.draw(matrices, mouseX, mouseY, partialTicks, delta);

        this.entity.setYaw(prevYaw);
        this.entity.setBodyYaw(prevBodyYaw);

        this.entity.setHeadYaw(prevHeadYaw);
        if (this.entity instanceof LivingEntity living) {
            living.prevHeadYaw = prevPrevHeadYaw;
            living.prevBodyYaw = prevPrevBodyYaw;
        }
        this.entity.prevYaw = prevPrevYaw;

        this.entity.setPitch(prevPitch);
        this.entity.prevPitch = prevPrevPitch;
    }
}

package io.wispforest.owowhatsthis.client.component;

import io.wispforest.owo.ui.component.EntityComponent;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class OwoWhatsThisEntityComponent<E extends Entity> extends EntityComponent<E> {

    protected static boolean drawing = false;
    protected static final Map<EntityType<?>, Consumer<OwoWhatsThisEntityComponent<?>>> SPECIAL_HANDLERS = new HashMap<>();

    protected boolean handlerExecuted = false;

    public OwoWhatsThisEntityComponent(Sizing sizing, E entity) {
        super(sizing, entity);
    }

    @Override
    public void draw(MatrixStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
        float prevHeadYaw = 0f;
        float prevPrevHeadYaw = 0f;

        if (this.entity instanceof LivingEntity living) {
            prevHeadYaw = living.headYaw;
            prevPrevHeadYaw = living.prevHeadYaw;

            living.headYaw = living.prevBodyYaw;
            living.prevHeadYaw = living.prevBodyYaw;
        }

        this.mouseRotation = 90 + (entity instanceof LivingEntity living ? living.prevBodyYaw : entity.getBodyYaw());

        this.runHandler();

        drawing = true;
        super.draw(matrices, mouseX, mouseY, partialTicks, delta);
        drawing = false;

        if (this.entity instanceof LivingEntity living) {
            living.headYaw = prevHeadYaw;
            living.prevHeadYaw = prevPrevHeadYaw;
        }
    }

    @SuppressWarnings("unchecked")
    public static <E extends Entity> void registerSpecialHandler(EntityType<E> entityType, Consumer<OwoWhatsThisEntityComponent<E>> handler) {
        ((Map<EntityType<E>, Consumer<OwoWhatsThisEntityComponent<E>>>) (Object) SPECIAL_HANDLERS).put(entityType, handler);
    }

    private void runHandler() {
        if (this.handlerExecuted || !SPECIAL_HANDLERS.containsKey(this.entity.getType())) return;

        SPECIAL_HANDLERS.get(this.entity.getType()).accept(this);
        this.handlerExecuted = true;
    }

    public static boolean drawing() {
        return drawing;
    }
}

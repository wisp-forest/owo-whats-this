package io.wispforest.owowhatsthis.mixin;

import io.wispforest.owowhatsthis.client.component.OwoWhatsThisEntityComponent;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Shadow public Camera camera;

    @Inject(method = "renderFire", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lnet/minecraft/util/math/Quaternion;)V"))
    private void cancelFireRotation(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity, CallbackInfo ci) {
        if (!OwoWhatsThisEntityComponent.drawing()) return;

        float yaw = entity instanceof LivingEntity living ? living.prevBodyYaw : entity.getBodyYaw();
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(this.camera.getYaw() + 135 - yaw));
    }

}

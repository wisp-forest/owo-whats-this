package io.wispforest.owowhatsthis.mixin.compat;


import io.wispforest.owowhatsthis.compat.CompatMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import wraith.alloyforgery.block.ForgeControllerBlockEntity;

@CompatMixin("alloy_forgery")
@Mixin(value = ForgeControllerBlockEntity.class, remap = false)
public interface ForgeControllerBlockEntityAccessor {

    @Accessor("fuel")
    float whatsthis$fuel();

}

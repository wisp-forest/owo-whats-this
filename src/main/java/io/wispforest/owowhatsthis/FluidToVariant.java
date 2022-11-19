package io.wispforest.owowhatsthis;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;

@SuppressWarnings("UnstableApiUsage")
public class FluidToVariant {
    public static FluidVariant apply(FluidState state) {
        return apply(state.getFluid());
    }

    public static FluidVariant apply(Fluid fluid) {
        return fluid instanceof FlowableFluid flowable
                ? FluidVariant.of(flowable.getStill())
                : FluidVariant.of(fluid);
    }
}

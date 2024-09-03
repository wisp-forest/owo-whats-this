package io.wispforest.owowhatsthis.information;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.ReflectiveEndecBuilder;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import io.wispforest.owo.ui.core.Component;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public record InformationProvider<T, D>(TargetType<T> applicableTargetType, Transformer<T, D> transformer, Endec<D> endec,
                                        boolean live, boolean client, int priority) {
    private static final ReflectiveEndecBuilder REFLECTIVE_BUILDER = new ReflectiveEndecBuilder(builder -> {
        builder.register(Endec.VAR_INT, Integer.class, int.class);
        builder.register(Endec.VAR_LONG, Long.class, long.class);
        MinecraftEndecs.addDefaults(builder);
    });

    public static <T, D> InformationProvider<T, D> client(TargetType<T> applicableTargetType, int priority, Transformer<T, D> transformer) {
        return new InformationProvider<>(
                applicableTargetType, transformer, null,
                false, true, priority
        );
    }

    public static <T, D> InformationProvider<T, D> server(TargetType<T> applicableTargetType, boolean live, int priority, Endec<D> endec, Transformer<T, D> transformer) {
        return new InformationProvider<>(
                applicableTargetType, transformer, endec,
                live, false, priority
        );
    }

    public static <T, D> InformationProvider<T, D> server(TargetType<T> applicableTargetType, boolean live, int priority, Class<D> dataTransportClass, Transformer<T, D> transformer) {
        return new InformationProvider<>(
                applicableTargetType, transformer, REFLECTIVE_BUILDER.get(dataTransportClass),
                live, false, priority
        );
    }

    public interface Transformer<T, D> {
        D apply(PlayerEntity player, World world, T target);
    }

    @Environment(EnvType.CLIENT)
    public interface DisplayAdapter<D> {
        Component build(D data);
    }

}

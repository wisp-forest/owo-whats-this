package io.wispforest.owowhatsthis.information;

import io.wispforest.owo.network.serialization.PacketBufSerializer;
import io.wispforest.owo.ui.core.Component;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public record InformationProvider<T, D>(TargetType<T> applicableTargetType, Transformer<T, D> transformer, PacketBufSerializer<D> serializer,
                                        boolean live, boolean client, int priority) {

    public static <T, D> InformationProvider<T, D> client(TargetType<T> applicableTargetType, int priority, Transformer<T, D> transformer) {
        return new InformationProvider<>(
                applicableTargetType, transformer, null,
                false, true, priority
        );
    }

    public static <T, D> InformationProvider<T, D> server(TargetType<T> applicableTargetType,  boolean live, int priority, PacketBufSerializer<D> serializer, Transformer<T, D> transformer) {
        return new InformationProvider<>(
                applicableTargetType, transformer, serializer,
                live, false, priority
        );
    }

    public static <T, D> InformationProvider<T, D> server(TargetType<T> applicableTargetType, boolean live, int priority, Class<D> dataTransportClass, Transformer<T, D> transformer) {
        return new InformationProvider<>(
                applicableTargetType, transformer, PacketBufSerializer.get(dataTransportClass),
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

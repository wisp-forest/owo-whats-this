package io.wispforest.owowhatsthis.information;

import io.wispforest.owo.network.serialization.PacketBufSerializer;
import io.wispforest.owo.ui.core.Component;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public record InformationProvider<T, D>(TargetType<T> applicableTargetType, Transformer<T, D> transformer, PacketBufSerializer<D> serializer,
                                        boolean live, boolean client, int priority) {

    public InformationProvider(TargetType<T> applicableTargetType, Transformer<T, D> transformer, Class<D> dataTransportClass,
                               boolean live, boolean client, int priority) {
        this(applicableTargetType, transformer, PacketBufSerializer.get(dataTransportClass), live, client, priority);
    }

    public interface Transformer<T, D> {
        D apply(PlayerEntity player, World world, T target);
    }

    @Environment(EnvType.CLIENT)
    public interface DisplayAdapter<D> {
        Component build(D data);
    }

}

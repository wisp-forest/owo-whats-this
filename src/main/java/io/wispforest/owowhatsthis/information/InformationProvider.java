package io.wispforest.owowhatsthis.information;

import io.wispforest.owo.network.serialization.PacketBufSerializer;
import io.wispforest.owo.ui.core.Component;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.World;

import java.util.function.BiFunction;

public record InformationProvider<T, D>(TargetType<T> applicableTargetType, BiFunction<World, T, D> transformer, PacketBufSerializer<D> serializer,
                                        boolean live, boolean client) {

    public InformationProvider(TargetType<T> applicableTargetType, BiFunction<World, T, D> transformer, Class<D> dataTransportClass,
                               boolean live, boolean client) {
        this(applicableTargetType, transformer, PacketBufSerializer.get(dataTransportClass), live, client);
    }

    @Environment(EnvType.CLIENT)
    public interface DisplayAdapter<D> {
        Component build(D data);
    }

}

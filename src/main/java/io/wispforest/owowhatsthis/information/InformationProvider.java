package io.wispforest.owowhatsthis.information;

import io.wispforest.owo.network.serialization.PacketBufSerializer;
import io.wispforest.owo.ui.core.Component;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class InformationProvider<T, D> {

    public final TargetType<T> applicableTargetType;
    public final PacketBufSerializer<D> serializer;

    private final BiFunction<World, T, @Nullable D> transformer;

    public InformationProvider(TargetType<T> applicableTargetType, BiFunction<World, T, @Nullable D> transformer, Class<D> dataTransportType) {
        this.applicableTargetType = applicableTargetType;
        this.transformer = transformer;

        this.serializer = PacketBufSerializer.get(dataTransportType);
    }

    public @Nullable D apply(World world, T data) {
        return this.transformer.apply(world, data);
    }

    @Environment(EnvType.CLIENT)
    public interface DisplayAdapter<D> {
        Component build(D data);
    }

}

package io.wispforest.owowhatsthis.client;

import io.wispforest.owowhatsthis.information.InformationProvider;
import io.wispforest.owowhatsthis.information.TargetType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class DisplayAdapters {

    private static final Map<TargetType<?>, TargetType.DisplayAdapter<?>> TYPE_DISPLAY_ADAPTERS = new HashMap<>();
    private static final Map<InformationProvider<?, ?>, InformationProvider.DisplayAdapter<?>> INFORMATION_DISPLAY_ADAPTERS = new HashMap<>();

    public static <T> void register(TargetType<T> targetType, TargetType.DisplayAdapter<T> displayAdapter) {
        TYPE_DISPLAY_ADAPTERS.put(targetType, displayAdapter);
    }

    @SuppressWarnings("unchecked")
    public static <T> TargetType.DisplayAdapter<T> get(TargetType<T> targetType) {
        return (TargetType.DisplayAdapter<T>) TYPE_DISPLAY_ADAPTERS.get(targetType);
    }


    public static <D> void register(InformationProvider<?, D> informationProvider, InformationProvider.DisplayAdapter<D> displayAdapter) {
        INFORMATION_DISPLAY_ADAPTERS.put(informationProvider, displayAdapter);
    }

    @SuppressWarnings("unchecked")
    public static <D> InformationProvider.DisplayAdapter<D> get(InformationProvider<?, D> informationProvider) {
        return (InformationProvider.DisplayAdapter<D>) INFORMATION_DISPLAY_ADAPTERS.get(informationProvider);
    }
}

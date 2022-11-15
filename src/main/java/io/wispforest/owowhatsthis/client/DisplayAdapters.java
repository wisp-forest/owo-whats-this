package io.wispforest.owowhatsthis.client;

import io.wispforest.owowhatsthis.information.TargetType;

import java.util.HashMap;
import java.util.Map;

public class DisplayAdapters {

    private static final Map<TargetType<?>, TargetType.DisplayAdapter<?>> REGISTRY = new HashMap<>();

    public static <T> void register(TargetType<T> targetType, TargetType.DisplayAdapter<T> displayAdapter) {
        REGISTRY.put(targetType, displayAdapter);
    }

    @SuppressWarnings("unchecked")
    public static <T> TargetType.DisplayAdapter<T> get(TargetType<T> targetType) {
        return (TargetType.DisplayAdapter<T>) REGISTRY.get(targetType);
    }

}

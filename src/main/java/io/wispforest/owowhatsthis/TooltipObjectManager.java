package io.wispforest.owowhatsthis;

import io.wispforest.owowhatsthis.information.InformationProvider;
import io.wispforest.owowhatsthis.information.TargetType;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;

import java.util.*;

public class TooltipObjectManager {

    private static final List<TargetType<?>> SORTED_TARGET_TYPES = new ArrayList<>();
    private static final List<TargetType<?>> SORTED_TARGET_TYPES_VIEW = Collections.unmodifiableList(SORTED_TARGET_TYPES);

    private static final Map<TargetType<?>, List<InformationProvider<?, ?>>> PROVIDERS_BY_TYPE = new HashMap<>();

    private static final List<InformationProvider<?, ?>> LIVE_PROVIDERS = new ArrayList<>();
    private static final List<InformationProvider<?, ?>> LIVE_PROVIDERS_VIEW = Collections.unmodifiableList(LIVE_PROVIDERS);

    public static void updateAndSort() {
        SORTED_TARGET_TYPES.clear();
        PROVIDERS_BY_TYPE.clear();
        LIVE_PROVIDERS.clear();

        OwoWhatsThis.TARGET_TYPES.streamEntries()
                .sorted(Comparator.comparingInt(entry -> -entry.value().priority()))
                .map(RegistryEntry.Reference::value)
                .forEach(SORTED_TARGET_TYPES::add);

        var providersByType = new HashMap<TargetType<?>, List<InformationProvider<?, ?>>>();
        OwoWhatsThis.INFORMATION_PROVIDERS.streamEntries()
                .filter(entry -> !OwoWhatsThis.CONFIG.disabledProviders().contains(entry.getKey().map(RegistryKey::getValue).orElse(null)))
                .sorted(Comparator.comparingInt(entry -> -entry.value().priority()))
                .map(RegistryEntry.Reference::value)
                .forEach(provider -> {
                    providersByType.computeIfAbsent(provider.applicableTargetType(), targetType -> new ArrayList<>()).add(provider);
                    if (provider.live()) LIVE_PROVIDERS.add(provider);
                });

        SORTED_TARGET_TYPES.forEach(targetType -> {
            PROVIDERS_BY_TYPE.put(targetType, Collections.unmodifiableList(providersByType.getOrDefault(targetType, List.of())));
        });
    }

    public static List<TargetType<?>> sortedTargetTypes() {
        return SORTED_TARGET_TYPES_VIEW;
    }

    public static List<InformationProvider<?, ?>> liveProviders() {
        return LIVE_PROVIDERS_VIEW;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<InformationProvider<T, ?>> getProviders(TargetType<T> type) {
        return (List<InformationProvider<T, ?>>) (Object) PROVIDERS_BY_TYPE.get(type);
    }
}

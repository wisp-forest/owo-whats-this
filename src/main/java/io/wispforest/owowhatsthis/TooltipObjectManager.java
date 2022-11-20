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

        OwoWhatsThis.TARGET_TYPE.streamEntries()
                .map(RegistryEntry.Reference::value)
                .sorted(Comparator.comparingInt(type -> -type.priority()))
                .forEach(SORTED_TARGET_TYPES::add);

        var providersByType = new HashMap<TargetType<?>, List<InformationProvider<?, ?>>>();
        OwoWhatsThis.INFORMATION_PROVIDER.streamEntries()
                .filter(entry -> !OwoWhatsThis.CONFIG.disabledProviders().contains(entry.getKey().map(RegistryKey::getValue).orElse(null)))
                .map(RegistryEntry.Reference::value)
                .sorted(Comparator.comparingInt(provider -> -provider.priority()))
                .forEach(provider -> {
                    providersByType.computeIfAbsent(provider.applicableTargetType(), type -> new ArrayList<>()).add(provider);
                    if (provider.live()) LIVE_PROVIDERS.add(provider);
                });

        SORTED_TARGET_TYPES.forEach(targetType -> {
            var providers = providersByType.getOrDefault(targetType, new ArrayList<>());
            var parent = targetType.parent();
            while (parent != null) {
                providers.addAll(providersByType.getOrDefault(parent, List.of()));
                parent = parent.parent();
            }

            PROVIDERS_BY_TYPE.put(
                    targetType,
                    Collections.unmodifiableList(providers)
            );
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

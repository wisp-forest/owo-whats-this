package io.wispforest.owowhatsthis.client;

import io.wispforest.owowhatsthis.OwoWhatsThis;
import io.wispforest.owowhatsthis.information.InformationProvider;
import io.wispforest.owowhatsthis.information.TargetType;
import net.minecraft.util.registry.RegistryEntry;

import java.util.*;

public class HudElementManager {

    private static final List<TargetType<?>> SORTED_TARGET_TYPES = new ArrayList<>();
    private static final List<TargetType<?>> SORTED_TARGET_TYPES_VIEW = Collections.unmodifiableList(SORTED_TARGET_TYPES);

    private static final Map<TargetType<?>, List<InformationProvider<?, ?>>> PROVIDERS_BY_TYPE = new HashMap<>();

    private static final List<InformationProvider<?, ?>> LIVE_PROVIDERS = new ArrayList<>();
    private static final List<InformationProvider<?, ?>> LIVE_PROVIDERS_VIEW = Collections.unmodifiableList(LIVE_PROVIDERS);

    public static void sortAndFreeze() {
        OwoWhatsThis.TARGET_TYPES.streamEntries()
                .sorted(Comparator.comparingInt(entry -> -entry.value().priority()))
                .map(RegistryEntry.Reference::value)
                .forEach(SORTED_TARGET_TYPES::add);

        var providersByType = new HashMap<TargetType<?>, List<InformationProvider<?, ?>>>();
        for (var provider : OwoWhatsThis.INFORMATION_PROVIDERS) {
            providersByType.computeIfAbsent(provider.applicableTargetType(), targetType -> new ArrayList<>()).add(provider);
            if (provider.live()) LIVE_PROVIDERS.add(provider);
        }

        providersByType.forEach((targetType, informationProviders) -> PROVIDERS_BY_TYPE.put(targetType, Collections.unmodifiableList(informationProviders)));
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

package io.wispforest.owowhatsthis;

import io.wispforest.owo.util.OwoFreezer;
import io.wispforest.owowhatsthis.information.InformationProvider;
import io.wispforest.owowhatsthis.information.InformationProviders;
import io.wispforest.owowhatsthis.information.TargetType;
import io.wispforest.owowhatsthis.network.OwoWhatsThisNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.registry.Registry;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OwoWhatsThis implements ModInitializer {

    public static final String MOD_ID = "owo-whats-this";
    public static final io.wispforest.owowhatsthis.OwoWhatsThisConfig CONFIG = io.wispforest.owowhatsthis.OwoWhatsThisConfig.createAndLoad();

    @SuppressWarnings("unchecked")
    public static final Registry<TargetType<?>> TARGET_TYPES =
            (Registry<TargetType<?>>) (Object) FabricRegistryBuilder.createSimple(TargetType.class, id("target_types")).buildAndRegister();

    @SuppressWarnings("unchecked")
    public static final Registry<InformationProvider<?, ?>> INFORMATION_PROVIDERS =
            (Registry<InformationProvider<?, ?>>) (Object) FabricRegistryBuilder.createSimple(InformationProvider.class, id("information_providers")).buildAndRegister();

    private static final Map<Identifier, Text> EFFECTIVE_TOOL_TAGS = new HashMap<>();
    private static final Map<Identifier, Text> EFFECTIVE_TOOL_TAGS_VIEW = Collections.unmodifiableMap(EFFECTIVE_TOOL_TAGS);

    @Override
    public void onInitialize() {
        Registry.register(TARGET_TYPES, id("block"), TargetType.BLOCK);
        Registry.register(TARGET_TYPES, id("entity"), TargetType.ENTITY);

        Registry.register(INFORMATION_PROVIDERS, id("block_breaking_progress"), InformationProviders.BLOCK_BREAKING_PROGRESS);
        Registry.register(INFORMATION_PROVIDERS, id("block_harvestability"), InformationProviders.BLOCK_HARVESTABILITY);
        Registry.register(INFORMATION_PROVIDERS, id("block_hardness"), InformationProviders.BLOCK_HARDNESS);
        Registry.register(INFORMATION_PROVIDERS, id("block_inventory"), InformationProviders.BLOCK_INVENTORY);
        Registry.register(INFORMATION_PROVIDERS, id("block_fluid_storage"), InformationProviders.BLOCK_FLUID_STORAGE);

        Registry.register(INFORMATION_PROVIDERS, id("entity_health"), InformationProviders.ENTITY_HEALTH);
        Registry.register(INFORMATION_PROVIDERS, id("entity_status_effects"), InformationProviders.ENTITY_STATUS_EFFECTS);

        OwoWhatsThisNetworking.initialize();
        OwoFreezer.registerFreezeCallback(TooltipObjectManager::sortAndFreeze);

        cacheEffectiveToolTags();
        CONFIG.subscribeToEffectiveToolTags(strings -> cacheEffectiveToolTags());
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static String modNameOf(Identifier id) {
        return FabricLoader.getInstance()
                .getModContainer(id.getNamespace())
                .map(ModContainer::getMetadata)
                .map(ModMetadata::getName)
                .orElse(id.getNamespace());
    }

    public static Map<Identifier, Text> effectiveToolTags() {
        return EFFECTIVE_TOOL_TAGS_VIEW;
    }

    private static void cacheEffectiveToolTags() {
        EFFECTIVE_TOOL_TAGS.clear();
        CONFIG.effectiveToolTags().forEach(s -> {
            var splitName = s.split("/");
            EFFECTIVE_TOOL_TAGS.put(
                    new Identifier(s),
                    Text.translatable("text.owo-whats-this.toolType." + splitName[splitName.length - 1])
            );
        });
    }

    public static HitResult raycast(Entity entity, float tickDelta) {
        var blockTarget = entity.raycast(5, tickDelta, OwoWhatsThis.CONFIG.includeFluids());

        var maxReach = entity.getRotationVec(tickDelta).multiply(5);
        var entityTarget = ProjectileUtil.raycast(
                entity,
                entity.getEyePos(),
                entity.getEyePos().add(maxReach),
                entity.getBoundingBox().stretch(maxReach),
                candidate -> true,
                5 * 5
        );

        return entityTarget != null
                ? entityTarget
                : blockTarget;
    }
}


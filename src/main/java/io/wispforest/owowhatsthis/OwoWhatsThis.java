package io.wispforest.owowhatsthis;

import io.wispforest.owo.text.CustomTextRegistry;
import io.wispforest.owo.util.OwoFreezer;
import io.wispforest.owowhatsthis.compat.OwoWhatsThisPlugin;
import io.wispforest.owowhatsthis.information.InformationProvider;
import io.wispforest.owowhatsthis.information.InformationProviders;
import io.wispforest.owowhatsthis.information.TargetType;
import io.wispforest.owowhatsthis.network.OwoWhatsThisNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class OwoWhatsThis implements ModInitializer {

    public static final String MOD_ID = "owo-whats-this";
    public static final io.wispforest.owowhatsthis.OwoWhatsThisConfig CONFIG = io.wispforest.owowhatsthis.OwoWhatsThisConfig.createAndLoad();

    @SuppressWarnings("unchecked")
    public static final Registry<TargetType<?>> TARGET_TYPE =
            (Registry<TargetType<?>>) (Object) FabricRegistryBuilder.createSimple(TargetType.class, id("target_types"))
                    .attribute(RegistryAttribute.SYNCED)
                    .buildAndRegister();

    @SuppressWarnings("unchecked")
    public static final Registry<InformationProvider<?, ?>> INFORMATION_PROVIDER =
            (Registry<InformationProvider<?, ?>>) (Object) FabricRegistryBuilder.createSimple(InformationProvider.class, id("information_providers"))
                    .attribute(RegistryAttribute.SYNCED)
                    .buildAndRegister();

    private static final Map<Identifier, Text> EFFECTIVE_TOOL_TAGS = new HashMap<>();
    private static final Map<Identifier, Text> EFFECTIVE_TOOL_TAGS_VIEW = Collections.unmodifiableMap(EFFECTIVE_TOOL_TAGS);

    private static final Direction[] ALL_DIRECTIONS = Direction.values();

    @Override
    public void onInitialize() {
        Registry.register(TARGET_TYPE, id("block"), TargetType.BLOCK);
        Registry.register(TARGET_TYPE, id("entity"), TargetType.ENTITY);
        Registry.register(TARGET_TYPE, id("player"), TargetType.PLAYER);
        Registry.register(TARGET_TYPE, id("fluid"), TargetType.FLUID);

        Registry.register(INFORMATION_PROVIDER, id("block_breaking_progress"), InformationProviders.BLOCK_BREAKING_PROGRESS);
        Registry.register(INFORMATION_PROVIDER, id("block_harvestability"), InformationProviders.BLOCK_HARVESTABILITY);
        Registry.register(INFORMATION_PROVIDER, id("block_hardness"), InformationProviders.BLOCK_HARDNESS);
        Registry.register(INFORMATION_PROVIDER, id("block_item_storage"), InformationProviders.BLOCK_ITEM_STORAGE);
        Registry.register(INFORMATION_PROVIDER, id("block_fluid_storage"), InformationProviders.BLOCK_FLUID_STORAGE);
        Registry.register(INFORMATION_PROVIDER, id("block_crop_growth"), InformationProviders.BLOCK_CROP_GROWTH);

        Registry.register(INFORMATION_PROVIDER, id("fluid_viscosity"), InformationProviders.FLUID_VISCOSITY);

        Registry.register(INFORMATION_PROVIDER, id("entity_health_and_armor"), InformationProviders.ENTITY_HEALTH_AND_ARMOR);
        Registry.register(INFORMATION_PROVIDER, id("entity_status_effects"), InformationProviders.ENTITY_STATUS_EFFECTS);
        Registry.register(INFORMATION_PROVIDER, id("entity_growing_time"), InformationProviders.ENTITY_GROWING_TIME);
        Registry.register(INFORMATION_PROVIDER, id("entity_breeding_cooldown"), InformationProviders.ENTITY_BREEDING_COOLDOWN);
        Registry.register(INFORMATION_PROVIDER, id("entity_tnt_fuse"), InformationProviders.ENTITY_TNT_FUSE);
        Registry.register(INFORMATION_PROVIDER, id("entity_item_count"), InformationProviders.ENTITY_ITEM_COUNT);
        Registry.register(INFORMATION_PROVIDER, id("entity_owner"), InformationProviders.ENTITY_OWNER);

        Registry.register(INFORMATION_PROVIDER, id("player_inventory"), InformationProviders.PLAYER_INVENTORY);

        for (var entrypoint : FabricLoader.getInstance().getEntrypoints("owo-whats-this-plugin", OwoWhatsThisPlugin.class)) {
            if (!entrypoint.shouldLoad()) continue;
            entrypoint.loadServer();
        }

        OwoWhatsThisNetworking.initialize();
        OwoFreezer.registerFreezeCallback(TooltipObjectManager::updateAndSort);
        CONFIG.subscribeToDisabledProviders(strings -> TooltipObjectManager.updateAndSort());

        cacheEffectiveToolTags();
        CONFIG.subscribeToEffectiveToolTags(strings -> cacheEffectiveToolTags());

        CustomTextRegistry.register("quantity", QuantityTextContent.Serializer.INSTANCE);
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

    @SuppressWarnings("UnstableApiUsage")
    public static <T> @Nullable Set<StorageView<T>> getStorageContents(BlockApiLookup<Storage<T>, Direction> lookup, World world, BlockPos blockPos) {
        var views = new LinkedHashSet<StorageView<T>>();
        for (var side : ALL_DIRECTIONS) {
            var storage = lookup.find(world, blockPos, side);
            if (storage == null) continue;

            storage.forEach(view -> views.add(view.getUnderlyingView()));
        }
        return views.isEmpty() ? null : views;
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

        return entityTarget != null && entityTarget.squaredDistanceTo(entity) < blockTarget.squaredDistanceTo(entity)
                ? entityTarget
                : blockTarget;
    }
}


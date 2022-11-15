package io.wispforest.owowhatsthis;

import io.wispforest.owowhatsthis.information.TargetType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.registry.Registry;

public class OwoWhatsThis implements ModInitializer {

    public static final String MOD_ID = "owo-whats-this";
    public static final io.wispforest.owowhatsthis.OwoWhatsThisConfig CONFIG = io.wispforest.owowhatsthis.OwoWhatsThisConfig.createAndLoad();

    @SuppressWarnings("unchecked")
    public static final Registry<TargetType<?>> TARGET_TYPES =
            (Registry<TargetType<?>>) (Object) FabricRegistryBuilder.createSimple(TargetType.class, id("target_types")).buildAndRegister();

    @Override
    public void onInitialize() {
        Registry.register(TARGET_TYPES, id("block"), TargetType.BLOCK);
        Registry.register(TARGET_TYPES, id("entity"), TargetType.ENTITY);
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

    public static HitResult raycast(Entity entity, float tickDelta) {
        var blockTarget = entity.raycast(5, tickDelta, OwoWhatsThis.CONFIG.includeFluids());

        var maxReach = entity.getEyePos().add(entity.getRotationVec(tickDelta).multiply(5));
        var entityTarget = ProjectileUtil.raycast(
                entity,
                entity.getEyePos(),
                maxReach,
                entity.getBoundingBox().stretch(maxReach),
                candidate -> true,
                5 * 5
        );

        return entityTarget != null
                ? entityTarget
                : blockTarget;
    }
}


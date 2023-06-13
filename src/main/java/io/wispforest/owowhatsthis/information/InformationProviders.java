package io.wispforest.owowhatsthis.information;

import io.wispforest.owo.network.serialization.PacketBufSerializer;
import io.wispforest.owo.ops.TextOps;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.util.RegistryAccess;
import io.wispforest.owowhatsthis.NumberFormatter;
import io.wispforest.owowhatsthis.OwoWhatsThis;
import io.wispforest.owowhatsthis.client.PlayerNameResolver;
import io.wispforest.owowhatsthis.client.component.HeartSpriteComponent;
import io.wispforest.owowhatsthis.client.component.ProgressBarComponent;
import io.wispforest.owowhatsthis.client.component.TexturedProgressBarComponent;
import io.wispforest.owowhatsthis.mixin.ClientPlayerInteractionManagerAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.mininglevel.v1.MiningLevelManager;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class InformationProviders implements AutoRegistryContainer<InformationProvider<?, ?>> {

    public static final Identifier GUI_ICONS_TEXTURE = new Identifier("textures/gui/icons.png");

    private static final Predicate<BlockState> SWORD_MINEABLE = state -> {
        return state.isOf(Blocks.COBWEB) || state.isIn(BlockTags.SWORD_EFFICIENT);
    };

    private static final Predicate<BlockState> SHEARS_MINEABLE = state -> {
        return state.isOf(Blocks.COBWEB)
                || state.isIn(BlockTags.LEAVES)
                || state.isIn(BlockTags.WOOL)
                || state.isOf(Blocks.GLOW_LICHEN)
                || state.isOf(Blocks.REDSTONE_WIRE)
                || state.isOf(Blocks.TRIPWIRE)
                || state.isOf(Blocks.VINE);
    };

    public static final InformationProvider<BlockStateWithPosition, Text> BLOCK_HARDNESS = InformationProvider.client(
            TargetType.BLOCK, 0,
            (player, world, target) -> Text.translatable("text.owo-whats-this.tooltip.blockHardness", target.state().getHardness(world, target.pos()))
    );

    public static final InformationProvider<BlockStateWithPosition, Text> BLOCK_HARVESTABILITY = InformationProvider.client(
            TargetType.BLOCK, 0,
            (player, world, target) -> {
                var state = target.state();
                var harvestable = !state.isToolRequired() || player.getMainHandStack().isSuitableFor(state);

                var effectiveTools = RegistryAccess.getEntry(Registries.BLOCK, state.getBlock()).streamTags()
                        .filter(blockTagKey -> OwoWhatsThis.effectiveToolTags().containsKey(blockTagKey.id()))
                        .map(blockTagKey -> OwoWhatsThis.effectiveToolTags().get(blockTagKey.id()))
                        .collect(Collectors.toList());

                int miningLevel = MiningLevelManager.getRequiredMiningLevel(state);
                Text miningLevelName;
                if (miningLevel > 0) {
                    var miningLevelId = MiningLevelManager.getBlockTag(miningLevel).id().toString().split(":");
                    miningLevelName = Text.translatable(
                            "text.owo-whats-this.tooltip.miningLevel",
                            Text.translatable("text.owo-whats-this.miningLevel." + miningLevelId[miningLevelId.length - 1])
                    );
                } else {
                    miningLevelName = Text.empty();
                }

                if (SWORD_MINEABLE.test(state)) {
                    effectiveTools.add(Text.translatable("text.owo-whats-this.toolType.sword"));
                }

                if (SHEARS_MINEABLE.test(state)) {
                    effectiveTools.add(Text.translatable("text.owo-whats-this.toolType.shears"));
                }

                var toolsText = effectiveTools.stream().reduce((mutableText, text) -> TextOps.concat(mutableText, Text.of(", ")).append(text));
                // this cast is only here to appease IntelliJ, as it occasionally has a meltdown
                // trying to understand the return type of this lambda
                return (Text) toolsText.map(tools -> Text.translatable("text.owo-whats-this.tooltip.tools", tools, miningLevelName)).orElse(Text.translatable("text.owo-whats-this.tooltip.noTools"))
                        .append("\n")
                        .append(Text.translatable(harvestable ? "text.owo-whats-this.tooltip.harvestable" : "text.owo-whats-this.tooltip.not_harvestable"));
            }
    );

    public static final InformationProvider<BlockStateWithPosition, Float> BLOCK_BREAKING_PROGRESS = InformationProvider.client(
            TargetType.BLOCK, -6900,
            (player, world, target) -> {
                float progress = ((ClientPlayerInteractionManagerAccessor) MinecraftClient.getInstance().interactionManager).whatsthis$getCurrentBreakingProgress();
                return progress > 0 ? progress : null;
            }
    );

    @SuppressWarnings("unchecked")
    public static final InformationProvider<BlockStateWithPosition, List<ItemStack>> BLOCK_ITEM_STORAGE = InformationProvider.server(
            TargetType.BLOCK, true, -10,
            (PacketBufSerializer<List<ItemStack>>) (Object) PacketBufSerializer.createCollectionSerializer(List.class, ItemStack.class),
            (player, world, target) -> {
                var storage = OwoWhatsThis.getStorageContents(ItemStorage.SIDED, world, target.pos());
                if (storage == null) return null;

                var items = new ArrayList<ItemStack>();
                storage.forEach(variant -> {
                    var stack = variant.getResource().toStack((int) variant.getAmount());
                    if (stack.isEmpty()) return;
                    items.add(stack);
                });

                return items.isEmpty() ? null : items;
            }
    );

    @SuppressWarnings("unchecked")
    public static final InformationProvider<BlockStateWithPosition, List<NbtCompound>> BLOCK_FLUID_STORAGE = InformationProvider.server(
            TargetType.BLOCK, true, 0,
            (PacketBufSerializer<List<NbtCompound>>) (Object) PacketBufSerializer.createCollectionSerializer(List.class, NbtCompound.class),
            (player, world, target) -> {
                var storage = OwoWhatsThis.getStorageContents(FluidStorage.SIDED, world, target.pos());
                if (storage == null) return null;

                var fluidData = new ArrayList<NbtCompound>();
                for (var entry : storage) {
                    if (entry.isResourceBlank()) continue;

                    var nbt = entry.getResource().toNbt();
                    nbt.putLong("owo-whats-this:amount", entry.getAmount());
                    nbt.putLong("owo-whats-this:capacity", entry.getCapacity());
                    fluidData.add(nbt);
                }

                return fluidData.isEmpty() ? null : fluidData;
            }
    );

    public static final InformationProvider<BlockStateWithPosition, Text> BLOCK_CROP_GROWTH = InformationProvider.client(
            TargetType.BLOCK, 0,
            (player, world, target) -> {
                var state = target.state();

                int growth;
                int maxGrowth;

                if (state.getBlock() instanceof CropBlock crop) {
                    growth = crop.getAge(state);
                    maxGrowth = crop.getMaxAge();
                } else if (state.getBlock() instanceof StemBlock) {
                    growth = state.get(StemBlock.AGE);
                    maxGrowth = 7;
                } else if (state.getBlock() instanceof NetherWartBlock) {
                    growth = state.get(NetherWartBlock.AGE);
                    maxGrowth = 3;
                } else {
                    return null;
                }

                return growth >= maxGrowth
                        ? Text.translatable("text.owo-whats-this.tooltip.blockCropGrowth.fullyGrown")
                        : Text.translatable("text.owo-whats-this.tooltip.blockCropGrowth", (growth * 100) / maxGrowth);
            }
    );

    public static final InformationProvider<FluidStateWithPosition, Text> FLUID_VISCOSITY = InformationProvider.client(
            TargetType.FLUID, 0,
            (player, world, target) -> {
                return Text.translatable("text.owo-whats-this.tooltip.fluidViscosity", FluidVariantAttributes.getViscosity(FluidVariant.of(target.state().getFluid()), world));
            }
    );

    public static final InformationProvider<Entity, EntityHealthInfo> ENTITY_HEALTH_AND_ARMOR = InformationProvider.server(
            TargetType.ENTITY, true, 20, EntityHealthInfo.class,
            (player, world, entity) -> (entity instanceof LivingEntity living)
                    ? new EntityHealthInfo(living.getHealth(), living.getMaxHealth(), living.getArmor())
                    : null
    );

    public static final InformationProvider<Entity, Text> ENTITY_STATUS_EFFECTS = InformationProvider.server(
            TargetType.ENTITY, true, 0, Text.class,
            (player, world, entity) -> {
                if (!(entity instanceof LivingEntity living)) return null;

                var effects = living.getStatusEffects();
                if (effects.isEmpty()) return null;

                var effectTexts = new ArrayList<Text>();
                for (var effect : effects) {
                    effectTexts.add(Text.translatable("text.owo-whats-this.tooltip.status_effect", Text.translatable(effect.getTranslationKey()), StatusEffectUtil.getDurationText(effect, 1)));
                }

                var display = Text.empty();
                for (int i = 0; i < effectTexts.size(); i++) {
                    display.append(effectTexts.get(i));
                    if (i < effectTexts.size() - 1) display.append("\n");
                }
                return display;
            }
    );

    public static final InformationProvider<Entity, Text> ENTITY_GROWING_TIME = InformationProvider.server(
            TargetType.ENTITY, true, 0, Text.class,
            (player, world, entity) -> {
                if (!(entity instanceof PassiveEntity passive)) return null;
                if (passive.getBreedingAge() >= 0) return null;

                return Text.translatable("text.owo-whats-this.tooltip.entityGrowingTime", NumberFormatter.time(-passive.getBreedingAge() / 20));
            }
    );

    public static final InformationProvider<Entity, Text> ENTITY_BREEDING_COOLDOWN = InformationProvider.server(
            TargetType.ENTITY, true, 0, Text.class,
            (player, world, entity) -> {
                if (!(entity instanceof PassiveEntity passive)) return null;
                if (passive.getBreedingAge() <= 0) return null;

                return Text.translatable("text.owo-whats-this.tooltip.entityBreedingCooldown", NumberFormatter.time(passive.getBreedingAge() / 20));
            }
    );

    public static final InformationProvider<Entity, Text> ENTITY_OWNER = InformationProvider.server(
            TargetType.ENTITY, false, 0, Text.class,
            (player, world, entity) -> {
                if (!(entity instanceof TameableEntity tameable)) return null;
                if (tameable.getOwnerUuid() == null) return null;

                var name = PlayerNameResolver.getName(((ServerWorld) world).getServer(), tameable.getOwnerUuid());
                if (name == null) return Text.translatable("text.owo-whats-this.tooltip.entity_owner_unknown");

                return Text.translatable("text.owo-whats-this.tooltip.entity_owner", name);
            }
    );

    public static final InformationProvider<Entity, Text> ENTITY_TNT_FUSE = InformationProvider.client(
            TargetType.ENTITY, 0,
            (player, world, entity) -> {
                if (!(entity instanceof TntEntity tnt)) return null;
                return Text.translatable("text.owo-whats-this.tooltip.entityTntFuse", NumberFormatter.time(tnt.getFuse() / 20));
            }
    );

    public static final InformationProvider<Entity, Text> ENTITY_ITEM_COUNT = InformationProvider.client(
            TargetType.ENTITY, 0,
            (player, world, entity) -> {
                if (!(entity instanceof ItemEntity item)) return null;
                if (item.getStack().getCount() < 2) return null;

                return Text.translatable("text.owo-whats-this.tooltip.entityItemCount", item.getStack().getCount());
            }
    );

    @SuppressWarnings("unchecked")
    public static final InformationProvider<Entity, List<ItemStack>> ENTITY_MINECART_INVENTORY = InformationProvider.server(
            TargetType.ENTITY, true, 0,
            (PacketBufSerializer<List<ItemStack>>) (Object) PacketBufSerializer.createCollectionSerializer(List.class, ItemStack.class),
            (player, world, target) -> {
                if (!(target instanceof StorageMinecartEntity minecart)) return null;

                var items = new ArrayList<ItemStack>();
                for (int i = 0; i < minecart.getInventory().size(); i++) {
                    var stack = minecart.getInventory().get(i);
                    if (stack.isEmpty()) continue;

                    items.add(stack);
                }

                return items;
            }
    );

    @SuppressWarnings("unchecked")
    public static final InformationProvider<Entity, List<ItemStack>> ENTITY_CHEST_BOAT_INVENTORY = InformationProvider.server(
            TargetType.ENTITY, true, 0,
            (PacketBufSerializer<List<ItemStack>>) (Object) PacketBufSerializer.createCollectionSerializer(List.class, ItemStack.class),
            (player, world, target) -> {
                if (!(target instanceof ChestBoatEntity boat)) return null;

                var items = new ArrayList<ItemStack>();
                for (int i = 0; i < boat.getInventory().size(); i++) {
                    var stack = boat.getInventory().get(i);
                    if (stack.isEmpty()) continue;

                    items.add(stack);
                }

                return items;
            }
    );

    @SuppressWarnings("unchecked")
    public static final InformationProvider<PlayerEntity, List<ItemStack>> PLAYER_INVENTORY = InformationProvider.server(
            TargetType.PLAYER, true, 0,
            (PacketBufSerializer<List<ItemStack>>) (Object) PacketBufSerializer.createCollectionSerializer(List.class, ItemStack.class),
            (player, world, target) -> {
                var items = new ArrayList<ItemStack>();
                for (int i = 0; i < target.getInventory().size(); i++) {
                    var stack = target.getInventory().getStack(i);
                    if (stack.isEmpty()) continue;

                    items.add(stack);
                }

                return items;
            }
    );

    @Override
    public Registry<InformationProvider<?, ?>> getRegistry() {
        return OwoWhatsThis.INFORMATION_PROVIDER;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<InformationProvider<?, ?>> getTargetFieldType() {
        return (Class<InformationProvider<?, ?>>) (Object) InformationProvider.class;
    }

    public record EntityHealthInfo(float health, float maxHealth, int armor) {}

    @Environment(EnvType.CLIENT)
    public static class DisplayAdapters {

        public static final InformationProvider.DisplayAdapter<Text> TEXT = data -> {
            return Components.label(data).shadow(true);
        };

        public static final InformationProvider.DisplayAdapter<EntityHealthInfo> ENTITY_HEALTH = data -> {
            return Containers.verticalFlow(Sizing.content(), Sizing.content()).<FlowLayout>configure(view -> {
                view.gap(1);
                view.child(Containers.horizontalFlow(Sizing.content(), Sizing.content()).<FlowLayout>configure(flowLayout -> {
                    if (data.health <= 40 && data.maxHealth <= 40) {
                        flowLayout.gap(-1);
                        for (int i = 0; i < Math.floor(data.health / 2); i++) {
                            flowLayout.child(new HeartSpriteComponent(1f));
                        }

                        if (data.health % 2f > 0.05) {
                            flowLayout.child(new HeartSpriteComponent(data.health * .5f % 1f));
                        }

                        int missingHearts = (int) Math.floor((data.maxHealth - data.health) / 2);
                        for (int i = 0; i < missingHearts; i++) {
                            flowLayout.child(new HeartSpriteComponent(0));
                        }
                    } else {
                        flowLayout.gap(2);
                        flowLayout.child(
                                Components.label(Text.literal(Math.round(data.health / 2f) + "x"))
                        ).child(
                                new HeartSpriteComponent(1)
                        );
                    }
                }));

                if (data.armor > 0) {
                    view.child(Containers.horizontalFlow(Sizing.content(), Sizing.content()).<FlowLayout>configure(flowLayout -> {
                        if (data.armor <= 40) {
                            flowLayout.gap(-1);
                            for (int i = 0; i < data.armor / 2; i++) {
                                flowLayout.child(Components.texture(
                                        GUI_ICONS_TEXTURE,
                                        34, 9, 9, 9
                                ));
                            }

                            if (data.armor % 2 != 0) {
                                flowLayout.child(Components.texture(
                                        GUI_ICONS_TEXTURE,
                                        25, 9, 9, 9
                                ));
                            }
                        } else {
                            flowLayout.gap(2);
                            flowLayout.child(
                                    Components.label(Text.literal(Math.round(data.armor / 2f) + "x"))
                            ).child(
                                    Components.texture(
                                            GUI_ICONS_TEXTURE,
                                            34, 9, 9, 9
                                    )
                            );
                        }
                    }));
                }
            });
        };

        public static final InformationProvider.DisplayAdapter<List<NbtCompound>> FLUID_STORAGE_LIST = data -> {
            return Containers.verticalFlow(Sizing.content(), Sizing.content()).<FlowLayout>configure(layout -> {
                layout.gap(2);
                for (var fluidNbt : data) {
                    var variant = FluidVariant.fromNbt(fluidNbt);

                    var sprite = FluidVariantRendering.getSprite(variant);
                    int color = FluidVariantRendering.getColor(variant);

                    long amount = fluidNbt.getLong("owo-whats-this:amount");
                    long capacity = fluidNbt.getLong("owo-whats-this:capacity");

                    final var fluidText = Text.translatable(
                            "text.owo-whats-this.tooltip.blockFluidAmount",
                            FluidVariantAttributes.getName(variant),
                            NumberFormatter.quantity(amount / 81000d, "B"),
                            NumberFormatter.quantity(capacity / 81000d, "B")
                    );

                    layout.child(TexturedProgressBarComponent.ofSprite(
                            fluidText,
                            amount / (float) capacity,
                            sprite
                    ).color(Color.ofArgb(color)));
                }
            });
        };

        public static final InformationProvider.DisplayAdapter<Float> BREAKING_PROGRESS = data -> {
            return new ProgressBarComponent(Sizing.fixed(110), Sizing.fixed(2)).progress(data);
        };

        public static final InformationProvider.DisplayAdapter<List<ItemStack>> ITEM_STACK_LIST = data -> {
            int displayCount;
            boolean showEllipsis;
            if (data.size() <= OwoWhatsThis.CONFIG.maxItemContainerPreviewRows() * 9) {
                displayCount = data.size();
                showEllipsis = false;
            } else {
                displayCount = OwoWhatsThis.CONFIG.maxItemContainerPreviewRows() * 9 - 1;
                showEllipsis = true;
            }

            int rows = MathHelper.ceilDiv(displayCount, 9);
            return Containers.grid(Sizing.content(), Sizing.content(), rows, Math.min(displayCount, 9)).<GridLayout>configure(layout -> {
                for (int i = 0; i < displayCount; i++) {
                    layout.child(
                            Components.item(data.get(i)).showOverlay(true).margins(Insets.of(1)),
                            i / 9, i % 9
                    );
                }

                if (showEllipsis) {
                    layout.child(Components.label(Text.literal("...")).shadow(true).margins(Insets.of(3, 0, 6, 0)), OwoWhatsThis.CONFIG.maxItemContainerPreviewRows() - 1, 8);
                }
            });
        };
    }
}

package io.wispforest.owowhatsthis.information;

import io.wispforest.owo.network.serialization.PacketBufSerializer;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.core.PositionedRectangle;
import io.wispforest.owo.ui.core.Sizing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registries;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class InformationProviders {

    public static final InformationProvider<BlockPos, Text> BLOCK_HARDNESS = new InformationProvider<>(
            TargetType.BLOCK,
            (world, blockPos) -> Text.literal("Hardness: " + world.getBlockState(blockPos).getHardness(world, blockPos)),
            Text.class, false, true
    );

    @SuppressWarnings("unchecked")
    public static final InformationProvider<BlockPos, List<ItemStack>> BLOCK_INVENTORY = new InformationProvider<>(
            TargetType.BLOCK,
            (world, blockPos) -> {
                var storage = ItemStorage.SIDED.find(world, blockPos, null);
                if (storage == null) return null;

                var items = new ArrayList<ItemStack>();
                storage.forEach(variant -> {
                    var stack = variant.getResource().toStack((int) variant.getAmount());
                    if (stack.isEmpty()) return;
                    items.add(stack);
                });
                return items;
            },
            (PacketBufSerializer<List<ItemStack>>) (Object) PacketBufSerializer.createCollectionSerializer(List.class, ItemStack.class),
            true, false
    );

    public static final InformationProvider<BlockPos, Text> BLOCK_FLUID_STORAGE = new InformationProvider<>(
            TargetType.BLOCK,
            (world, blockPos) -> {
                var storage = FluidStorage.SIDED.find(world, blockPos, null);
                if (storage == null) return null;

                var fluidTexts = new ArrayList<Text>();
                storage.forEach(variant -> {
                    if (variant.getResource().isBlank()) return;
                    final var id = Registries.FLUID.getId(variant.getResource().getFluid());
                    fluidTexts.add(
                            Text.translatable("fluid." + id.toTranslationKey()).append(Text.literal(": " + (variant.getAmount() / 81) + " /" + (variant.getCapacity() / 81)))
                    );
                });

                var display = Text.empty();
                for (int i = 0; i < fluidTexts.size(); i++) {
                    display.append(fluidTexts.get(i));
                    if (i < fluidTexts.size() - 1) display.append("\n");
                }
                return display;
            },
            Text.class, true, false
    );

    public static final InformationProvider<Entity, Float> ENTITY_HEALTH = new InformationProvider<>(
            TargetType.ENTITY,
            (world, entity) -> (entity instanceof LivingEntity living)
                    ? living.getHealth()
                    : null,
            Float.class, true, false
    );

    public static final InformationProvider<Entity, Text> ENTITY_STATUS_EFFECTS = new InformationProvider<>(
            TargetType.ENTITY,
            (world, entity) -> {
                if (!(entity instanceof LivingEntity living)) return null;

                var effects = living.getStatusEffects();
                if (effects.isEmpty()) return null;

                var effectTexts = new ArrayList<Text>();
                for (var effect : effects) {
                    effectTexts.add(Text.translatable("text.owo-whats-this.tooltip.status_effect", Text.translatable(effect.getTranslationKey()), StatusEffectUtil.durationToString(effect, 1)));
                }

                var display = Text.empty();
                for (int i = 0; i < effectTexts.size(); i++) {
                    display.append(effectTexts.get(i));
                    if (i < effectTexts.size() - 1) display.append("\n");
                }
                return display;
            },
            Text.class, true, false
    );

    @Environment(EnvType.CLIENT)
    public static class DisplayAdapters {

        public static final InformationProvider.DisplayAdapter<Text> TEXT = Components::label;

        public static final InformationProvider.DisplayAdapter<Float> ENTITY_HEALTH = data -> {
            if (data < 30) {
                return Containers.horizontalFlow(Sizing.content(), Sizing.content()).<FlowLayout>configure(flowLayout -> {
                    flowLayout.gap(2);
                    for (int i = 0; i < data / 2; i++) {
                        flowLayout.child(
                                Components.texture(InGameHud.GUI_ICONS_TEXTURE, 53, 1, 7, 7)
                        );
                    }

                    if (data % 1f > 0.05) {
                        flowLayout.child(
                                Components.texture(InGameHud.GUI_ICONS_TEXTURE, 53, 1, 7, 7)
                                        .visibleArea(PositionedRectangle.of(0, 0, Math.round(7 * (data % 1f)), 7))
                        );
                    }
                });
            } else {
                return Containers.horizontalFlow(Sizing.content(), Sizing.content()).<FlowLayout>configure(flowLayout -> {
                    flowLayout.gap(2);
                    flowLayout.child(
                            Components.label(Text.literal(Math.round(data / 2f) + "x"))
                    ).child(
                            Components.texture(InGameHud.GUI_ICONS_TEXTURE, 53, 1, 7, 7)
                    );
                });
            }
        };

        public static final InformationProvider.DisplayAdapter<List<ItemStack>> ITEM_STACK_LIST = data -> {
            int rows = MathHelper.ceilDiv(data.size(), 9);
            return Containers.grid(Sizing.content(), Sizing.content(), rows, Math.min(data.size(), 9)).<GridLayout>configure(layout -> {
                for (int i = 0; i < data.size(); i++) {
                    layout.child(
                            Components.item(data.get(i)).showOverlay(true),
                            i / 9, i % 9
                    );
                }
            });
        };

    }

}

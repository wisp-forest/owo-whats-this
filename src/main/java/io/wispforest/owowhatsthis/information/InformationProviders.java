package io.wispforest.owowhatsthis.information;

import io.wispforest.owo.network.serialization.PacketBufSerializer;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.core.Sizing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class InformationProviders {

    public static final InformationProvider<BlockPos, Text> BLOCK_HARDNESS = new InformationProvider<>(
            TargetType.BLOCK,
            (world, blockPos) -> Text.literal("Hardness: " + world.getBlockState(blockPos).getHardness(world, blockPos)),
            Text.class, false, true
    );

    @SuppressWarnings({"unchecked", "UnstableApiUsage"})
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

    public static final InformationProvider<Entity, Text> ENTITY_HEALTH = new InformationProvider<>(
            TargetType.ENTITY,
            (world, entity) -> (entity instanceof LivingEntity living)
                    ? Text.literal("Health: " + living.getHealth() + "/" + living.getMaxHealth())
                    : null,
            Text.class, true, false
    );

    @Environment(EnvType.CLIENT)
    public static class DisplayAdapters {

        public static final InformationProvider.DisplayAdapter<Text> TEXT = Components::label;

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

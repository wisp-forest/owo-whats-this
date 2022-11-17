package io.wispforest.owowhatsthis.information;

import io.wispforest.owo.network.serialization.PacketBufSerializer;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Sizing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

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
                if (!(world.getBlockEntity(blockPos) instanceof Inventory inventory)) return null;
                var items = new ArrayList<ItemStack>();
                for (int i = 0; i < inventory.size(); i++) {
                    var stack = inventory.getStack(i);
                    if (stack.isEmpty()) continue;

                    items.add(stack);
                }

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
            return Containers.horizontalFlow(Sizing.content(), Sizing.content()).<FlowLayout>configure(layout -> {
                for (var stack : data) {
                    layout.child(Components.item(stack).showOverlay(true));
                }
            });
        };

    }

}

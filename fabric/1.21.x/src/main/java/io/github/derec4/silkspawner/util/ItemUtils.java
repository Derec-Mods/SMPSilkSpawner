package io.github.derec4.silkspawner.util;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.spawner.MobSpawnerLogic;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class ItemUtils {
    public static boolean checkSilkTouch(ItemStack item) {
        if (item == null || item.isEmpty()) return false;
        for (RegistryEntry<Enchantment> enchantment : item.getEnchantments().getEnchantments()) {
            if (enchantment.matchesKey(Enchantments.SILK_TOUCH)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkPickaxe(ItemStack item) {
        if (item == null || item.isEmpty()) return false;
        return item.isIn(ItemTags.PICKAXES);
    }

    public static ItemStack newSpawnerItem(@Nullable EntityType<?> entityType, int amount) {
        if (amount <= 0) return ItemStack.EMPTY;

        ItemStack spawner = new ItemStack(Items.SPAWNER, amount);
        if (entityType != null) {
            NbtCompound nbt = new NbtCompound();
            NbtCompound spawnData = new NbtCompound();
            NbtCompound entity = new NbtCompound();
            entity.putString("id", EntityType.getId(entityType).toString());
            spawnData.put("entity", entity);
            nbt.put(MobSpawnerLogic.SPAWN_DATA_KEY, spawnData);
            BlockItem.setBlockEntityData(spawner, BlockEntityType.MOB_SPAWNER, nbt);
            spawner.set(DataComponentTypes.LORE, new LoreComponent(List.of(
                    Text.literal(formatMobName(entityType) + " Spawner").formatted(Formatting.GOLD)
            )));
        }
        return spawner;
    }

    public static @Nullable EntityType<?> getSpawnedType(MobSpawnerBlockEntity spawner) {
        return getSpawnedType(spawner.getLogic().writeNbt(new NbtCompound()));
    }

    public static @Nullable EntityType<?> getSpawnedType(ItemStack stack) {
        NbtComponent component = stack.get(DataComponentTypes.BLOCK_ENTITY_DATA);
        return component == null ? null : getSpawnedType(component.copyNbt());
    }

    public static @Nullable EntityType<?> getSpawnedType(NbtCompound nbt) {
        if (!nbt.contains(MobSpawnerLogic.SPAWN_DATA_KEY, NbtElement.COMPOUND_TYPE)) {
            return null;
        }
        NbtCompound spawnData = nbt.getCompound(MobSpawnerLogic.SPAWN_DATA_KEY);
        if (!spawnData.contains("entity", NbtElement.COMPOUND_TYPE)) {
            return null;
        }
        String id = spawnData.getCompound("entity").getString("id");
        return id == null || id.isEmpty() ? null : EntityType.get(id).orElse(null);
    }

    private static String formatMobName(EntityType<?> entityType) {
        String[] parts = EntityType.getId(entityType).getPath().toLowerCase(Locale.ROOT).split("_");
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                name.append(' ');
            }
            String part = parts[i];
            if (!part.isEmpty()) {
                name.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    name.append(part.substring(1));
                }
            }
        }
        return name.toString();
    }
}

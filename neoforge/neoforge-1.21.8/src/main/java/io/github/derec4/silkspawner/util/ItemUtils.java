package io.github.derec4.silkspawner.util;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

import java.util.List;
import java.util.Locale;

public class ItemUtils {

    public static boolean checkSilkTouch(ItemStack item) {
        if (item == null || item.isEmpty()) {
            return false;
        }
        ItemEnchantments enchantments = EnchantmentHelper.getEnchantmentsForCrafting(item);
        for (Holder<Enchantment> enchantment : enchantments.keySet()) {
            if (enchantment.is(Enchantments.SILK_TOUCH)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkPickaxe(ItemStack item) {
        if (item == null || item.isEmpty()) {
            return false;
        }
        return item.is(ItemTags.PICKAXES);
    }

    public static ItemStack newSpawnerItem(String entityId, int amount) {
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }

        ItemStack spawner = new ItemStack(Blocks.SPAWNER, amount);
        if (entityId != null && !entityId.isEmpty()) {
            CompoundTag entity = new CompoundTag();
            entity.putString("id", entityId);
            CompoundTag spawnData = new CompoundTag();
            spawnData.put("entity", entity);
            CompoundTag blockEntityTag = new CompoundTag();
            blockEntityTag.put("SpawnData", spawnData);

            CustomData.set(DataComponents.BLOCK_ENTITY_DATA, spawner, blockEntityTag);

            ItemLore lore = new ItemLore(List.of(
                    Component.literal(formatMobName(entityId) + " Spawner").withStyle(ChatFormatting.GOLD)
            ));
            spawner.set(DataComponents.LORE, lore);
        }
        return spawner;
    }

    public static String getSpawnedType(SpawnerBlockEntity spawner) {
        if (spawner == null) {
            return null;
        }
        CompoundTag nbt = spawner.saveCustomOnly(spawner.getLevel().registryAccess());
        if (nbt.contains("SpawnData")) {
            CompoundTag spawnData = nbt.getCompoundOrEmpty("SpawnData");
            if (spawnData.contains("entity")) {
                return spawnData.getCompoundOrEmpty("entity").getStringOr("id", null);
            }
            if (spawnData.contains("id")) {
                return spawnData.getStringOr("id", null);
            }
        }
        return null;
    }

    public static String getSpawnedType(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        CustomData customData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (customData != null) {
            CompoundTag nbt = customData.copyTag();
            if (nbt.contains("SpawnData")) {
                CompoundTag spawnData = nbt.getCompoundOrEmpty("SpawnData");
                if (spawnData.contains("entity")) {
                    return spawnData.getCompoundOrEmpty("entity").getStringOr("id", null);
                }
                if (spawnData.contains("id")) {
                    return spawnData.getStringOr("id", null);
                }
            }
        }
        return null;
    }

    public static String formatMobName(String entityId) {
        if (entityId == null || entityId.isEmpty()) {
            return "Pig";
        }

        String path = entityId;
        int colonIndex = path.indexOf(':');
        if (colonIndex >= 0) {
            path = path.substring(colonIndex + 1);
        }

        String[] parts = path.toLowerCase(Locale.ROOT).split("_");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].isEmpty()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(parts[i].charAt(0)));
            if (parts[i].length() > 1) {
                builder.append(parts[i].substring(1));
            }
        }
        return builder.toString();
    }
}

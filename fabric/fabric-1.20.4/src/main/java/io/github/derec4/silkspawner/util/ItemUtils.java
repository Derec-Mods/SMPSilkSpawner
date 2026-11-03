package io.github.derec4.silkspawner.util;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

import java.util.Locale;

public class ItemUtils {

    public static boolean checkSilkTouch(ItemStack item) {
        if (item == null || item.isEmpty()) {
            return false;
        }
        return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, item) > 0;
    }

    public static boolean checkPickaxe(ItemStack item) {
        if (item == null || item.isEmpty()) {
            return false;
        }
        return item.is(ItemTags.PICKAXES) || item.getItem() instanceof PickaxeItem;
    }

    public static ItemStack newSpawnerItem(String entityId, int amount) {
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }

        ItemStack spawner = new ItemStack(Blocks.SPAWNER, amount);
        if (entityId != null && !entityId.isEmpty()) {
            CompoundTag tag = spawner.getOrCreateTag();

            CompoundTag blockEntityTag = new CompoundTag();
            CompoundTag spawnData = new CompoundTag();
            CompoundTag entity = new CompoundTag();
            entity.putString("id", entityId);
            spawnData.put("entity", entity);
            blockEntityTag.put("SpawnData", spawnData);
            tag.put("BlockEntityTag", blockEntityTag);

            CompoundTag display = spawner.getOrCreateTagElement("display");
            ListTag lore = new ListTag();
            lore.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(formatMobName(entityId) + " Spawner").withStyle(ChatFormatting.GOLD))));
            display.put("Lore", lore);
        }
        return spawner;
    }

    public static String getSpawnedType(SpawnerBlockEntity spawner) {
        if (spawner == null) {
            return null;
        }
        CompoundTag nbt = spawner.saveWithoutMetadata();
        if (nbt.contains("SpawnData", 10)) {
            CompoundTag spawnData = nbt.getCompound("SpawnData");
            if (spawnData.contains("entity", 10)) {
                return spawnData.getCompound("entity").getString("id");
            }
            if (spawnData.contains("id", 8)) {
                return spawnData.getString("id");
            }
        }
        return null;
    }

    public static String getSpawnedType(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.hasTag()) {
            return null;
        }
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("BlockEntityTag", 10)) {
            CompoundTag blockEntityTag = tag.getCompound("BlockEntityTag");
            if (blockEntityTag.contains("SpawnData", 10)) {
                CompoundTag spawnData = blockEntityTag.getCompound("SpawnData");
                if (spawnData.contains("entity", 10)) {
                    return spawnData.getCompound("entity").getString("id");
                }
                if (spawnData.contains("id", 8)) {
                    return spawnData.getString("id");
                }
            }
        }
        return null;
    }

    public static String formatMobName(String entityId) {
        String path = entityId;
        if (path.contains(":")) {
            path = path.substring(path.indexOf(':') + 1);
        }
        String[] parts = path.toLowerCase(Locale.ROOT).split("_");
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


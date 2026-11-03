package io.github.derec4.silkspawner.util;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.text.TextFormatting;

import java.util.Locale;

public class ItemUtils {

    public static boolean checkSilkTouch(ItemStack item) {
        if (item == null || item.isEmpty()) {
            return false;
        }
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, item) > 0;
    }

    public static boolean checkPickaxe(ItemStack item) {
        if (item == null || item.isEmpty()) {
            return false;
        }
        return item.getItem() instanceof ItemPickaxe || item.getItem().getToolClasses(item).contains("pickaxe");
    }

    public static ItemStack newSpawnerItem(String entityId, int amount) {
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }

        ItemStack spawner = new ItemStack(Blocks.MOB_SPAWNER, amount);
        if (entityId != null && !entityId.isEmpty()) {
            NBTTagCompound tag = spawner.getTagCompound();
            if (tag == null) {
                tag = new NBTTagCompound();
            }

            NBTTagCompound blockEntityTag = new NBTTagCompound();
            NBTTagCompound spawnData = new NBTTagCompound();
            spawnData.setString("id", entityId);
            blockEntityTag.setTag("SpawnData", spawnData);
            tag.setTag("BlockEntityTag", blockEntityTag);

            NBTTagCompound display = new NBTTagCompound();
            NBTTagList lore = new NBTTagList();
            lore.appendTag(new NBTTagString(TextFormatting.GOLD + formatMobName(entityId) + " Spawner"));
            display.setTag("Lore", lore);
            tag.setTag("display", display);

            spawner.setTagCompound(tag);
        }
        return spawner;
    }

    public static String getSpawnedType(TileEntityMobSpawner spawner) {
        if (spawner == null) {
            return null;
        }
        NBTTagCompound nbt = new NBTTagCompound();
        spawner.getSpawnerBaseLogic().writeToNBT(nbt);
        if (nbt.hasKey("SpawnData", 10)) {
            NBTTagCompound spawnData = nbt.getCompoundTag("SpawnData");
            if (spawnData.hasKey("id", 8)) {
                return spawnData.getString("id");
            }
        }
        return null;
    }

    public static String getSpawnedType(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.hasTagCompound()) {
            return null;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.hasKey("BlockEntityTag", 10)) {
            NBTTagCompound blockEntityTag = tag.getCompoundTag("BlockEntityTag");
            if (blockEntityTag.hasKey("SpawnData", 10)) {
                return blockEntityTag.getCompoundTag("SpawnData").getString("id");
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


package io.github.derec4.silkspawner.util;

import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ToolType;

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
        return item.getItem().getToolTypes(item).contains(ToolType.PICKAXE) || item.getItem() instanceof PickaxeItem;
    }

    public static ItemStack newSpawnerItem(String entityId, int amount) {
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }

        ItemStack spawner = new ItemStack(Blocks.SPAWNER, amount);
        if (entityId != null && !entityId.isEmpty()) {
            CompoundNBT tag = spawner.getOrCreateTag();

            CompoundNBT blockEntityTag = new CompoundNBT();
            CompoundNBT spawnData = new CompoundNBT();
            spawnData.putString("id", entityId);
            blockEntityTag.put("SpawnData", spawnData);
            tag.put("BlockEntityTag", blockEntityTag);

            CompoundNBT display = new CompoundNBT();
            ListNBT lore = new ListNBT();
            lore.add(StringNBT.valueOf(TextFormatting.GOLD + formatMobName(entityId) + " Spawner"));
            display.put("Lore", lore);
            tag.put("display", display);
        }
        return spawner;
    }

    public static String getSpawnedType(MobSpawnerTileEntity spawner) {
        if (spawner == null) {
            return null;
        }
        CompoundNBT nbt = new CompoundNBT();
        spawner.getSpawnerBaseLogic().write(nbt);
        if (nbt.contains("SpawnData", 10)) {
            CompoundNBT spawnData = nbt.getCompound("SpawnData");
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
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("BlockEntityTag", 10)) {
            CompoundNBT blockEntityTag = tag.getCompound("BlockEntityTag");
            if (blockEntityTag.contains("SpawnData", 10)) {
                return blockEntityTag.getCompound("SpawnData").getString("id");
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


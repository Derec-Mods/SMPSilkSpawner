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

public final class ItemUtils {

    private static final String ENTITY_KEY = "entity";
    private static final String ID_KEY = "id";

    private ItemUtils() {
    }

    public static boolean hasSilkTouch(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        for (RegistryEntry<Enchantment> enchantment : stack.getEnchantments().getEnchantments()) {
            if (enchantment.matchesKey(Enchantments.SILK_TOUCH)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPickaxe(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.isIn(ItemTags.PICKAXES);
    }

    public static boolean isSilkTouchPickaxe(ItemStack stack) {
        return hasSilkTouch(stack) && isPickaxe(stack);
    }

    public static ItemStack newSpawnerItem(@Nullable EntityType<?> entityType, int amount) {
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }

        ItemStack spawner = new ItemStack(Items.SPAWNER, amount);
        if (entityType != null) {
            NbtCompound nbt = new NbtCompound();
            NbtCompound spawnData = new NbtCompound();
            NbtCompound entity = new NbtCompound();
            entity.putString(ID_KEY, EntityType.getId(entityType).toString());
            spawnData.put(ENTITY_KEY, entity);
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
        if (component == null) {
            return null;
        }
        return getSpawnedType(component.copyNbt());
    }

    public static @Nullable EntityType<?> getSpawnedType(NbtCompound nbt) {
        if (!nbt.contains(MobSpawnerLogic.SPAWN_DATA_KEY, NbtElement.COMPOUND_TYPE)) {
            return null;
        }

        NbtCompound spawnData = nbt.getCompound(MobSpawnerLogic.SPAWN_DATA_KEY);
        if (!spawnData.contains(ENTITY_KEY, NbtElement.COMPOUND_TYPE)) {
            return null;
        }

        String id = spawnData.getCompound(ENTITY_KEY).getString(ID_KEY);
        if (id == null || id.isEmpty()) {
            return null;
        }
        return EntityType.get(id).orElse(null);
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

package io.github.derec4.sMPSilkSpawner.util;

import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {
    public static boolean checkSilkTouch(ItemStack item) {
        if (item == null) return false;
        return item.containsEnchantment(Enchantment.SILK_TOUCH);
    }

    /**
     * Generates a new spawner ItemStack with mob type stored in block state (and optional lore).
     * @param entityType The entity type to store (e.g. "PIG", "ZOMBIE").
     * @param customName The custom display name (use null for default).
     * @param amount The amount of spawners to create.
     * @return The ItemStack representing the spawner(s).
     *
     * @author (former) mushroomhostage
     * @author (former x2) xGhOsTkiLLeRx
     * @author DerexXD
     */
    public static ItemStack newSpawnerItem(EntityType entityType, String customName, int amount) {
        if (amount <= 0) return new ItemStack(Material.AIR);

        ItemStack spawner = new ItemStack(Material.SPAWNER, amount);
        BlockStateMeta meta = (BlockStateMeta) spawner.getItemMeta();
        if (meta == null) return spawner;

        if (entityType != null && meta.getBlockState() instanceof CreatureSpawner blockState) {
            blockState.setSpawnedType(entityType);
            meta.setBlockState(blockState);
        }

        if (customName != null && !customName.isEmpty()) {
            meta.setDisplayName(entityType.name() + " Spawner");
        }

        List<String> lore = new ArrayList<>();
        if (entityType != null) {
            lore.add("Stored Mob: " + entityType.name());
        }
        meta.setLore(lore);
        spawner.setItemMeta(meta);
        return spawner;
    }
}

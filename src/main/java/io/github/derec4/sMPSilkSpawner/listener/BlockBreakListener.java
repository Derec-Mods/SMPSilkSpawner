package io.github.derec4.sMPSilkSpawner.listener;

import io.github.derec4.sMPSilkSpawner.util.ExplosionUtils;
import io.github.derec4.sMPSilkSpawner.util.ItemUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

import static io.github.derec4.sMPSilkSpawner.util.ItemUtils.checkPickaxe;
import static io.github.derec4.sMPSilkSpawner.util.ItemUtils.checkSilkTouch;

/**
 * Handle the breaking of a spawner.
 *
 * @author (former) mushroomhostage
 * @author (former x2) xGhOsTkiLLeRx
 * @author DerexXD
 */
public class BlockBreakListener implements Listener {

    private static double spawnerDropChance = 0.5;
    private static boolean dropAsItem = true;
    private static int durabilityDamage = 100;
    private static double playerDamage = 0.0;

    public static void setSpawnerDropChance(double dropChance) {
        spawnerDropChance = dropChance;
    }

    public static void setDropAsItem(boolean dropAsItem) {
        BlockBreakListener.dropAsItem = dropAsItem;
    }

    public static void setDurabilityDamage(int durabilityDamage) {
        BlockBreakListener.durabilityDamage = durabilityDamage;
    }

    public static void setPlayerDamage(double playerDamage) {
        BlockBreakListener.playerDamage = playerDamage;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        final boolean isFakeEvent = !BlockBreakEvent.class.equals(event.getClass());
        if (isFakeEvent) {
            return;
        }

        Block block = event.getBlock();

        // Check if spawner
        if (block.getType() != Material.SPAWNER) {
            return;
        }

        if (!(block.getState() instanceof CreatureSpawner spawner)) {
            return;
        }

        Player player = event.getPlayer();
        GameMode gameMode = player.getGameMode();
        if (gameMode != GameMode.SURVIVAL && gameMode != GameMode.ADVENTURE) {
            return;
        }

        ItemStack tool = player.getInventory().getItemInMainHand();
        if (!checkSilkTouch(tool)) {
            tool = player.getInventory().getItemInOffHand();
            if (!checkSilkTouch(tool)) {
                return;
            }
        }

        if (!checkPickaxe(tool)) {
            return;
        }

        event.setDropItems(false);
        event.setExpToDrop(0);

        applyDurabilityDamage(tool);
        if (playerDamage > 0.0) {
            player.damage(playerDamage);
        }

        World world = block.getWorld();
        Location location = block.getLocation();
        ExplosionUtils.playExplosion(world, location, ExplosionUtils.rollExplosionSize());

        if (Math.random() >= spawnerDropChance) {
            return;
        }

        EntityType entityType = spawner.getSpawnedType();

        ItemStack spawnerItem = ItemUtils.newSpawnerItem(entityType, null, 1);
        if (dropAsItem) {
            world.dropItemNaturally(location, spawnerItem);
        } else {
            Map<Integer, ItemStack> leftover = player.getInventory().addItem(spawnerItem);
            for (ItemStack item : leftover.values()) {
                world.dropItemNaturally(location, item);
            }
        }
    }

    private static void applyDurabilityDamage(ItemStack tool) {
        if (durabilityDamage <= 0 || tool.getType().getMaxDurability() <= 0) {
            return;
        }

        ItemMeta meta = tool.getItemMeta();
        if (!(meta instanceof Damageable damageable)) {
            return;
        }

        int newDamage = damageable.getDamage() + durabilityDamage;
        if (newDamage >= tool.getType().getMaxDurability()) {
            tool.setAmount(0);
            return;
        }

        damageable.setDamage(newDamage);
        tool.setItemMeta(damageable);
    }
}

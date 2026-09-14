package io.github.derec4.sMPSilkSpawner.listener;

import io.github.derec4.sMPSilkSpawner.util.ExplosionUtils;
import io.github.derec4.sMPSilkSpawner.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
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
    private static int durabilityDamage = 1024;
    private static double playerDamagePercent = 50.0;

    public static void setSpawnerDropChance(double dropChance) {
        spawnerDropChance = dropChance;
    }

    public static void setDropAsItem(boolean dropAsItem) {
        BlockBreakListener.dropAsItem = dropAsItem;
    }

    public static void setDurabilityDamage(int durabilityDamage) {
        BlockBreakListener.durabilityDamage = durabilityDamage;
    }

    public static void setPlayerDamagePercent(double playerDamagePercent) {
        BlockBreakListener.playerDamagePercent = playerDamagePercent;
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
        applyMagicPlayerDamage(player);

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

    private static void applyMagicPlayerDamage(Player player) {
        if (playerDamagePercent <= 0.0) {
            return;
        }

        AttributeInstance maxHealthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        double maxHealth = maxHealthAttribute != null ? maxHealthAttribute.getValue() : 20.0;
        double amount = maxHealth * (playerDamagePercent / 100.0);
        if (amount <= 0.0) {
            return;
        }

        EntityDamageEvent damageEvent = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.MAGIC, amount);
        Bukkit.getPluginManager().callEvent(damageEvent);
        if (damageEvent.isCancelled()) {
            return;
        }

        player.setLastDamageCause(damageEvent);
        player.setHealth(Math.max(0.0, player.getHealth() - damageEvent.getFinalDamage()));
    }
}

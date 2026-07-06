package io.github.derec4.sMPSilkSpawner.listener;

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

import static io.github.derec4.sMPSilkSpawner.util.ItemUtils.checkSilkTouch;

/**
 * Handle the breaking of a spawner.
 *
 * @author (former) mushroomhostage
 * @author (former x2) xGhOsTkiLLeRx
 * @author DerexXD
 */
public class BlockBreakListener implements Listener {

    // no magic numbers lol 
    private static final double SPAWNER_DROP_CHANCE = 0.5;

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

        event.setDropItems(false);
        event.setExpToDrop(0);

        if (Math.random() >= SPAWNER_DROP_CHANCE) {
            return;
        }

        World world = block.getWorld();
        Location location = block.getLocation();
        EntityType entityType = spawner.getSpawnedType();

        ItemStack spawnerItem = ItemUtils.newSpawnerItem(entityType, null, 1);
        world.dropItemNaturally(location, spawnerItem);
    }
}

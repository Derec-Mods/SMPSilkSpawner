package io.github.derec4.sMPSilkSpawner.listener;

import io.github.derec4.sMPSilkSpawner.util.ParticleUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    private static boolean requireAdjacent = true;
    private static boolean requireSameMob = true;

    public static void setPlacementRules(boolean requireAdjacent, boolean requireSameMob) {
        BlockPlaceListener.requireAdjacent = requireAdjacent;
        BlockPlaceListener.requireSameMob = requireSameMob;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!(event.getBlock().getState() instanceof CreatureSpawner attempted)) {
            return;
        }

        if (!requireAdjacent && !requireSameMob) {
            return;
        }

        World world = event.getBlock().getWorld();
        Location location = event.getBlock().getLocation();
        Block against = event.getBlockAgainst();

        if (!(against.getState() instanceof CreatureSpawner spawner)) {
            if (requireAdjacent) {
                event.setCancelled(true);
                ParticleUtils.playFailedParticles(world, location);
            }
            return;
        }

        if (requireSameMob && !spawner.getSpawnedType().equals(attempted.getSpawnedType())) {
            event.setCancelled(true);
            ParticleUtils.playFailedParticles(world, location);
            return;
        }

        world.playSound(location, Sound.BLOCK_SOUL_SAND_PLACE, 1.0f, 1.0f);
    }
}

package io.github.derec4.sMPSilkSpawner.listener;

import io.github.derec4.sMPSilkSpawner.util.ParticleUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {
    // Event handling logic will go here

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!(event.getBlock() instanceof CreatureSpawner attempted)) return;

        World world = event.getBlock().getWorld();
        Location location = event.getBlock().getLocation();
        Block against = event.getBlockAgainst();

        // Can only place a spawner next to a spawner
        // Add configs to enable/disable this and config to check if same mob
        if (!(against instanceof CreatureSpawner spawner)) {
            ParticleUtils.playFailedParticles(world, location);
            return;
        }

        if (!spawner.getSpawnedType().equals(attempted.getSpawnedType())) {
            // Add configs to enable/disable this and config to check if same mob
            ParticleUtils.playFailedParticles(world, location);
            return;
        }

        world.playSound(location, Sound.BLOCK_SOUL_SAND_PLACE, 1.0f, 1.0f);
    }
}

package io.github.derec4.sMPSilkSpawner.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {
    // Event handling logic will go here

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() != Material.SPAWNER) {
            return;
        }

        World world = event.getBlock().getWorld();
        Location location = event.getBlock().getLocation();
        world.playSound(location, Sound.BLOCK_SOUL_SAND_PLACE, 1.0f, 1.0f);
    }
}

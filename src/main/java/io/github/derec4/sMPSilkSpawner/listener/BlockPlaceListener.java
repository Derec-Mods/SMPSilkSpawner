package io.github.derec4.sMPSilkSpawner.listener;

import io.github.derec4.sMPSilkSpawner.util.ParticleUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class BlockPlaceListener implements Listener {

    private static final BlockFace[] ADJACENT_FACES = {
            BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN
    };

    private static boolean requireAdjacent = true;
    private static boolean requireSameMob = true;
    private static int maxCluster = -1;

    public static void setPlacementRules(boolean requireAdjacent, boolean requireSameMob, int maxCluster) {
        BlockPlaceListener.requireAdjacent = requireAdjacent;
        BlockPlaceListener.requireSameMob = requireSameMob;
        BlockPlaceListener.maxCluster = maxCluster;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!(event.getBlock().getState() instanceof CreatureSpawner attempted)) {
            return;
        }

        if (!requireAdjacent && !requireSameMob && maxCluster < 0) {
            return;
        }

        World world = event.getBlock().getWorld();
        Location location = event.getBlock().getLocation();
        Block against = event.getBlockAgainst();

        if (!(against.getState() instanceof CreatureSpawner spawner)) {
            if (requireAdjacent) {
                event.setCancelled(true);
                ParticleUtils.playFailedParticles(world, location);
                return;
            }
        } else if (requireSameMob && !spawner.getSpawnedType().equals(attempted.getSpawnedType())) {
            event.setCancelled(true);
            ParticleUtils.playFailedParticles(world, location);
            return;
        }

        if (maxCluster >= 0 && countConnectedSpawners(event.getBlock()) > maxCluster) {
            event.setCancelled(true);
            ParticleUtils.playFailedParticles(world, location);
            return;
        }

        if (against.getState() instanceof CreatureSpawner) {
            world.playSound(location, Sound.BLOCK_SOUL_SAND_PLACE, 1.0f, 1.0f);
        }
    }

    private static int countConnectedSpawners(Block start) {
        Set<String> visited = new HashSet<>();
        Queue<Block> queue = new ArrayDeque<>();
        queue.add(start);
        visited.add(blockKey(start));

        int count = 0;
        while (!queue.isEmpty()) {
            Block current = queue.poll();
            if (current != start && current.getType() != Material.SPAWNER) {
                continue;
            }
            count++;

            for (BlockFace face : ADJACENT_FACES) {
                Block next = current.getRelative(face);
                if (visited.add(blockKey(next)) && next.getType() == Material.SPAWNER) {
                    queue.add(next);
                }
            }
        }
        return count;
    }

    private static String blockKey(Block block) {
        return block.getWorld().getUID() + ":" + block.getX() + ":" + block.getY() + ":" + block.getZ();
    }
}

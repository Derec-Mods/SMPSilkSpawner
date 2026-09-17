package io.github.derec4.silkspawner.listener;

import io.github.derec4.silkspawner.config.ConfigManager;
import io.github.derec4.silkspawner.util.ItemUtils;
import io.github.derec4.silkspawner.util.ParticleUtils;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

public class BlockPlaceHandler {
    public static boolean shouldCancel(ItemPlacementContext context) {
        if (!context.getStack().isOf(Items.SPAWNER) || context.getPlayer() == null) {
            return false;
        }

        boolean requireAdjacent = ConfigManager.isRequireAdjacent();
        boolean requireSameMob = ConfigManager.isRequireSameMob();
        int maxCluster = ConfigManager.getMaxCluster();
        if (!requireAdjacent && !requireSameMob && maxCluster < 0) {
            return false;
        }

        World world = context.getWorld();
        BlockPos placePos = context.getBlockPos();
        BlockPos againstPos = getAgainstPos(context);

        if (!world.getBlockState(againstPos).isOf(Blocks.SPAWNER)) {
            if (requireAdjacent) {
                ParticleUtils.playFailedParticles(world, placePos);
                return true;
            }
        } else if (requireSameMob) {
            EntityType<?> attempted = ItemUtils.getSpawnedType(context.getStack());
            EntityType<?> existing = null;
            if (world.getBlockEntity(againstPos) instanceof MobSpawnerBlockEntity spawner) {
                existing = ItemUtils.getSpawnedType(spawner);
            }
            if (!Objects.equals(existing, attempted)) {
                ParticleUtils.playFailedParticles(world, placePos);
                return true;
            }
        }

        if (maxCluster >= 0 && countConnectedSpawners(world, placePos) > maxCluster) {
            ParticleUtils.playFailedParticles(world, placePos);
            return true;
        }
        return false;
    }

    public static void onPlaced(ItemPlacementContext context) {
        if (context.getWorld().isClient || !context.getStack().isOf(Items.SPAWNER)) {
            return;
        }
        BlockPos againstPos = getAgainstPos(context);
        if (context.getWorld().getBlockState(againstPos).isOf(Blocks.SPAWNER)) {
            context.getWorld().playSound(null, context.getBlockPos(), SoundEvents.BLOCK_SOUL_SAND_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }

    private static BlockPos getAgainstPos(ItemPlacementContext context) {
        return context.canReplaceExisting() ? context.getBlockPos() : context.getBlockPos().offset(context.getSide().getOpposite());
    }

    private static int countConnectedSpawners(World world, BlockPos start) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(start);
        visited.add(start.toImmutable());

        int count = 0;
        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            if (!current.equals(start) && !world.getBlockState(current).isOf(Blocks.SPAWNER)) {
                continue;
            }
            count++;

            for (Direction direction : Direction.values()) {
                BlockPos next = current.offset(direction);
                if (visited.add(next.toImmutable()) && world.getBlockState(next).isOf(Blocks.SPAWNER)) {
                    queue.add(next);
                }
            }
        }
        return count;
    }
}

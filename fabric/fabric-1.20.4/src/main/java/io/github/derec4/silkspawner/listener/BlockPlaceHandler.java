package io.github.derec4.silkspawner.listener;

import io.github.derec4.silkspawner.config.ConfigManager;
import io.github.derec4.silkspawner.util.ItemUtils;
import io.github.derec4.silkspawner.util.ParticleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

public class BlockPlaceHandler {
    public static boolean shouldCancel(BlockPlaceContext context) {
        if (!context.getItemInHand().is(Items.SPAWNER) || context.getPlayer() == null) {
            return false;
        }

        boolean requireAdjacent = ConfigManager.isRequireAdjacent();
        boolean requireSameMob = ConfigManager.isRequireSameMob();
        int maxCluster = ConfigManager.getMaxCluster();
        if (!requireAdjacent && !requireSameMob && maxCluster < 0) {
            return false;
        }

        Level level = context.getLevel();
        BlockPos placePos = context.getClickedPos();
        BlockPos againstPos = getAgainstPos(context);

        if (!level.getBlockState(againstPos).is(Blocks.SPAWNER)) {
            if (requireAdjacent) {
                ParticleUtils.playFailedParticles(level, placePos);
                return true;
            }
        } else if (requireSameMob) {
            String attempted = ItemUtils.getSpawnedType(context.getItemInHand());
            String existing = null;
            if (level.getBlockEntity(againstPos) instanceof SpawnerBlockEntity spawner) {
                existing = ItemUtils.getSpawnedType(spawner);
            }
            if (!Objects.equals(existing, attempted)) {
                ParticleUtils.playFailedParticles(level, placePos);
                return true;
            }
        }

        if (maxCluster >= 0 && countConnectedSpawners(level, placePos) > maxCluster) {
            ParticleUtils.playFailedParticles(level, placePos);
            return true;
        }
        return false;
    }

    public static void onPlaced(BlockPlaceContext context) {
        if (context.getLevel().isClientSide() || !context.getItemInHand().is(Items.SPAWNER)) {
            return;
        }
        BlockPos againstPos = getAgainstPos(context);
        if (context.getLevel().getBlockState(againstPos).is(Blocks.SPAWNER)) {
            context.getLevel().playSound(null, context.getClickedPos(), SoundEvents.SOUL_SAND_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
    }

    private static BlockPos getAgainstPos(BlockPlaceContext context) {
        return context.replacingClickedOnBlock() ? context.getClickedPos() : context.getClickedPos().relative(context.getClickedFace().getOpposite());
    }

    private static int countConnectedSpawners(Level level, BlockPos start) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(start);
        visited.add(start);

        int count = 0;
        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            if (!current.equals(start) && !level.getBlockState(current).is(Blocks.SPAWNER)) {
                continue;
            }
            count++;

            for (Direction direction : Direction.values()) {
                BlockPos next = current.relative(direction);
                if (visited.add(next) && level.getBlockState(next).is(Blocks.SPAWNER)) {
                    queue.add(next);
                }
            }
        }
        return count;
    }
}


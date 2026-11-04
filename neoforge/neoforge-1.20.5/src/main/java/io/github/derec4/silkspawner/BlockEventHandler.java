package io.github.derec4.silkspawner;

import io.github.derec4.silkspawner.config.ConfigManager;
import io.github.derec4.silkspawner.util.ExplosionUtils;
import io.github.derec4.silkspawner.util.ItemUtils;
import io.github.derec4.silkspawner.util.ParticleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

public class BlockEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Level level = (Level) event.getLevel();
        if (level.isClientSide() || event.getState().getBlock() != Blocks.SPAWNER) {
            return;
        }

        Player player = event.getPlayer();
        if (player == null || player.isCreative()) {
            return;
        }

        ItemStack tool = player.getMainHandItem();
        if (!ItemUtils.checkSilkTouch(tool)) {
            tool = player.getOffhandItem();
            if (!ItemUtils.checkSilkTouch(tool)) {
                return;
            }
        }

        if (!ItemUtils.checkPickaxe(tool)) {
            return;
        }

        event.setExpToDrop(0);

        int durabilityDamage = ConfigManager.getDurabilityDamage();
        if (durabilityDamage > 0 && tool.isDamageableItem()) {
            tool.hurtAndBreak(durabilityDamage, player, EquipmentSlot.MAINHAND);
        }

        double playerDamagePercent = ConfigManager.getPlayerDamagePercent();
        if (playerDamagePercent > 0.0) {
            float amount = (float) (player.getMaxHealth() * (playerDamagePercent / 100.0));
            if (amount > 0.0f) {
                player.hurt(player.damageSources().magic(), amount);
            }
        }

        BlockPos pos = event.getPos();
        ExplosionUtils.playExplosion(level, pos, ExplosionUtils.rollExplosionSize());

        if (Math.random() >= ConfigManager.getDropChance()) {
            return;
        }

        BlockEntity be = level.getBlockEntity(pos);
        String entityId = null;
        if (be instanceof SpawnerBlockEntity spawner) {
            entityId = ItemUtils.getSpawnedType(spawner);
        }

        ItemStack spawnerItem = ItemUtils.newSpawnerItem(entityId, 1);
        if (ConfigManager.isDropAsItem()) {
            Block.popResource(level, pos, spawnerItem);
        } else if (!player.getInventory().add(spawnerItem) && !spawnerItem.isEmpty()) {
            Block.popResource(level, pos, spawnerItem);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel().isClientSide() || event.getPlacedBlock().getBlock() != Blocks.SPAWNER) {
            return;
        }

        Level level = (Level) event.getLevel();
        BlockPos placePos = event.getPos();
        boolean placedAgainstSpawner = event.getPlacedAgainst().getBlock() == Blocks.SPAWNER;

        ItemStack itemInHand = ItemStack.EMPTY;
        if (event.getEntity() instanceof Player player) {
            if (player.getMainHandItem().is(Blocks.SPAWNER.asItem())) {
                itemInHand = player.getMainHandItem();
            } else if (player.getOffhandItem().is(Blocks.SPAWNER.asItem())) {
                itemInHand = player.getOffhandItem();
            }
        }

        boolean requireAdjacent = ConfigManager.isRequireAdjacent();
        boolean requireSameMob = ConfigManager.isRequireSameMob();
        int maxCluster = ConfigManager.getMaxCluster();

        if (requireAdjacent || requireSameMob || maxCluster >= 0) {
            if (!placedAgainstSpawner) {
                if (requireAdjacent) {
                    ParticleUtils.playFailedParticles(level, placePos);
                    event.setCanceled(true);
                    return;
                }
            } else if (requireSameMob) {
                BlockPos againstPos = null;
                for (Direction dir : Direction.values()) {
                    BlockPos neighbor = placePos.relative(dir);
                    if (level.getBlockState(neighbor).getBlock() == Blocks.SPAWNER) {
                        againstPos = neighbor;
                        break;
                    }
                }
                String attempted = ItemUtils.getSpawnedType(itemInHand);
                String existing = null;
                if (againstPos != null) {
                    BlockEntity be = level.getBlockEntity(againstPos);
                    if (be instanceof SpawnerBlockEntity spawner) {
                        existing = ItemUtils.getSpawnedType(spawner);
                    }
                }
                if (!Objects.equals(existing, attempted)) {
                    ParticleUtils.playFailedParticles(level, placePos);
                    event.setCanceled(true);
                    return;
                }
            }

            if (maxCluster >= 0) {
                int count = countConnectedSpawners(level, placePos);
                if (count > maxCluster) {
                    ParticleUtils.playFailedParticles(level, placePos);
                    event.setCanceled(true);
                    return;
                }
            }
        }

        if (placedAgainstSpawner) {
            level.playSound(null, placePos, SoundEvents.SOUL_SAND_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
    }

    private static int countConnectedSpawners(Level level, BlockPos start) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(start);
        visited.add(start);

        int count = 0;
        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            if (!current.equals(start) && level.getBlockState(current).getBlock() != Blocks.SPAWNER) {
                continue;
            }
            count++;

            for (Direction direction : Direction.values()) {
                BlockPos next = current.relative(direction);
                if (visited.add(next) && level.getBlockState(next).getBlock() == Blocks.SPAWNER) {
                    queue.add(next);
                }
            }
        }
        return count;
    }
}


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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
            tool.hurtAndBreak(durabilityDamage, player, (p) -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
        }

        double playerDamagePercent = ConfigManager.getPlayerDamagePercent();
        if (playerDamagePercent > 0.0) {
            float amount = (float) (player.getMaxHealth() * (playerDamagePercent / 100.0));
            if (amount > 0.0f) {
                player.hurt(DamageSource.MAGIC, amount);
            }
        }

        BlockPos pos = event.getPos();
        ExplosionUtils.playExplosion(level, pos, ExplosionUtils.rollExplosionSize());

        if (Math.random() >= ConfigManager.getDropChance()) {
            return;
        }

        BlockEntity tileEntity = level.getBlockEntity(pos);
        String entityId = null;
        if (tileEntity instanceof SpawnerBlockEntity) {
            entityId = ItemUtils.getSpawnedType((SpawnerBlockEntity) tileEntity);
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
        Level level = (Level) event.getLevel();
        if (level.isClientSide() || event.getPlacedBlock().getBlock() != Blocks.SPAWNER) {
            return;
        }

        boolean requireAdjacent = ConfigManager.isRequireAdjacent();
        boolean requireSameMob = ConfigManager.isRequireSameMob();
        int maxCluster = ConfigManager.getMaxCluster();

        if (!requireAdjacent && !requireSameMob && maxCluster < 0) {
            return;
        }

        BlockPos placePos = event.getPos();
        BlockPos againstPos = null;
        if (event.getPlacedAgainst().getBlock() == Blocks.SPAWNER) {
            for (Direction facing : Direction.values()) {
                BlockPos neighbor = placePos.relative(facing);
                if (level.getBlockState(neighbor).getBlock() == Blocks.SPAWNER) {
                    againstPos = neighbor;
                    break;
                }
            }
        }

        if (againstPos == null) {
            if (requireAdjacent) {
                event.setCanceled(true);
                ParticleUtils.playFailedParticles(level, placePos);
                return;
            }
        } else if (requireSameMob) {
            String attempted = null;
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                attempted = ItemUtils.getSpawnedType(player.getMainHandItem());
                if (attempted == null) {
                    attempted = ItemUtils.getSpawnedType(player.getOffhandItem());
                }
            }
            String existing = null;
            BlockEntity te = level.getBlockEntity(againstPos);
            if (te instanceof SpawnerBlockEntity) {
                existing = ItemUtils.getSpawnedType((SpawnerBlockEntity) te);
            }
            if (!Objects.equals(existing, attempted)) {
                event.setCanceled(true);
                ParticleUtils.playFailedParticles(level, placePos);
                return;
            }
        }

        if (maxCluster >= 0 && countConnectedSpawners(level, placePos) > maxCluster) {
            event.setCanceled(true);
            ParticleUtils.playFailedParticles(level, placePos);
            return;
        }

        if (againstPos != null) {
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

            for (Direction facing : Direction.values()) {
                BlockPos next = current.relative(facing);
                if (visited.add(next) && level.getBlockState(next).getBlock() == Blocks.SPAWNER) {
                    queue.add(next);
                }
            }
        }
        return count;
    }
}


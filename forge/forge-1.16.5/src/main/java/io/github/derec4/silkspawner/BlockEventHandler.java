package io.github.derec4.silkspawner;

import io.github.derec4.silkspawner.config.ConfigManager;
import io.github.derec4.silkspawner.util.ExplosionUtils;
import io.github.derec4.silkspawner.util.ItemUtils;
import io.github.derec4.silkspawner.util.ParticleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
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
        World world = (World) event.getWorld();
        if (world.isRemote() || event.getState().getBlock() != Blocks.SPAWNER) {
            return;
        }

        PlayerEntity player = event.getPlayer();
        if (player == null || player.isCreative()) {
            return;
        }

        ItemStack tool = player.getHeldItemMainhand();
        if (!ItemUtils.checkSilkTouch(tool)) {
            tool = player.getHeldItemOffhand();
            if (!ItemUtils.checkSilkTouch(tool)) {
                return;
            }
        }

        if (!ItemUtils.checkPickaxe(tool)) {
            return;
        }

        event.setExpToDrop(0);

        int durabilityDamage = ConfigManager.getDurabilityDamage();
        if (durabilityDamage > 0 && tool.isDamageable()) {
            tool.damageItem(durabilityDamage, player, (p) -> p.sendBreakAnimation(Hand.MAIN_HAND));
        }

        double playerDamagePercent = ConfigManager.getPlayerDamagePercent();
        if (playerDamagePercent > 0.0) {
            float amount = (float) (player.getMaxHealth() * (playerDamagePercent / 100.0));
            if (amount > 0.0f) {
                player.attackEntityFrom(DamageSource.MAGIC, amount);
            }
        }

        BlockPos pos = event.getPos();
        ExplosionUtils.playExplosion(world, pos, ExplosionUtils.rollExplosionSize());

        if (Math.random() >= ConfigManager.getDropChance()) {
            return;
        }

        TileEntity tileEntity = world.getTileEntity(pos);
        String entityId = null;
        if (tileEntity instanceof MobSpawnerTileEntity) {
            entityId = ItemUtils.getSpawnedType((MobSpawnerTileEntity) tileEntity);
        }

        ItemStack spawnerItem = ItemUtils.newSpawnerItem(entityId, 1);
        if (ConfigManager.isDropAsItem()) {
            Block.spawnAsEntity(world, pos, spawnerItem);
        } else if (!player.inventory.addItemStackToInventory(spawnerItem) && !spawnerItem.isEmpty()) {
            Block.spawnAsEntity(world, pos, spawnerItem);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        World world = (World) event.getWorld();
        if (world.isRemote() || event.getPlacedBlock().getBlock() != Blocks.SPAWNER) {
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
                BlockPos neighbor = placePos.offset(facing);
                if (world.getBlockState(neighbor).getBlock() == Blocks.SPAWNER) {
                    againstPos = neighbor;
                    break;
                }
            }
        }

        if (againstPos == null) {
            if (requireAdjacent) {
                event.setCanceled(true);
                ParticleUtils.playFailedParticles(world, placePos);
                return;
            }
        } else if (requireSameMob) {
            String attempted = null;
            if (event.getEntity() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getEntity();
                attempted = ItemUtils.getSpawnedType(player.getHeldItemMainhand());
                if (attempted == null) {
                    attempted = ItemUtils.getSpawnedType(player.getHeldItemOffhand());
                }
            }
            String existing = null;
            TileEntity te = world.getTileEntity(againstPos);
            if (te instanceof MobSpawnerTileEntity) {
                existing = ItemUtils.getSpawnedType((MobSpawnerTileEntity) te);
            }
            if (!Objects.equals(existing, attempted)) {
                event.setCanceled(true);
                ParticleUtils.playFailedParticles(world, placePos);
                return;
            }
        }

        if (maxCluster >= 0 && countConnectedSpawners(world, placePos) > maxCluster) {
            event.setCanceled(true);
            ParticleUtils.playFailedParticles(world, placePos);
            return;
        }

        if (againstPos != null) {
            world.playSound(null, placePos, SoundEvents.BLOCK_SOUL_SAND_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }

    private static int countConnectedSpawners(World world, BlockPos start) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(start);
        visited.add(start);

        int count = 0;
        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            if (!current.equals(start) && world.getBlockState(current).getBlock() != Blocks.SPAWNER) {
                continue;
            }
            count++;

            for (Direction facing : Direction.values()) {
                BlockPos next = current.offset(facing);
                if (visited.add(next) && world.getBlockState(next).getBlock() == Blocks.SPAWNER) {
                    queue.add(next);
                }
            }
        }
        return count;
    }
}


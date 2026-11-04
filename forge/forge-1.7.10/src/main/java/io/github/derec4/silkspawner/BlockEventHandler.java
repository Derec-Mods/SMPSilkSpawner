package io.github.derec4.silkspawner;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import io.github.derec4.silkspawner.config.ConfigManager;
import io.github.derec4.silkspawner.util.ExplosionUtils;
import io.github.derec4.silkspawner.util.ItemUtils;
import io.github.derec4.silkspawner.util.ParticleUtils;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

public class BlockEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        World world = event.world;
        if (world.isRemote || event.block != Blocks.mob_spawner) {
            return;
        }

        EntityPlayer player = event.getPlayer();
        if (player == null || player.capabilities.isCreativeMode) {
            return;
        }

        ItemStack tool = player.getHeldItem();
        if (!ItemUtils.checkSilkTouch(tool) || !ItemUtils.checkPickaxe(tool)) {
            return;
        }

        event.setExpToDrop(0);

        int durabilityDamage = ConfigManager.getDurabilityDamage();
        if (durabilityDamage > 0 && tool.isItemStackDamageable()) {
            tool.damageItem(durabilityDamage, player);
        }

        double playerDamagePercent = ConfigManager.getPlayerDamagePercent();
        if (playerDamagePercent > 0.0) {
            float amount = (float) (player.getMaxHealth() * (playerDamagePercent / 100.0));
            if (amount > 0.0f) {
                player.attackEntityFrom(DamageSource.magic, amount);
            }
        }

        int x = event.x;
        int y = event.y;
        int z = event.z;
        ExplosionUtils.playExplosion(world, x, y, z, ExplosionUtils.rollExplosionSize());

        if (Math.random() >= ConfigManager.getDropChance()) {
            return;
        }

        TileEntity tileEntity = world.getTileEntity(x, y, z);
        String entityId = null;
        if (tileEntity instanceof TileEntityMobSpawner) {
            entityId = ItemUtils.getSpawnedType((TileEntityMobSpawner) tileEntity);
        }

        ItemStack spawnerItem = ItemUtils.newSpawnerItem(entityId, 1);
        if (spawnerItem == null) {
            return;
        }

        if (ConfigManager.isDropAsItem()) {
            world.spawnEntityInWorld(new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, spawnerItem));
        } else if (!player.inventory.addItemStackToInventory(spawnerItem)) {
            world.spawnEntityInWorld(new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, spawnerItem));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockEvent.PlaceEvent event) {
        World world = event.world;
        if (world.isRemote || event.placedBlock != Blocks.mob_spawner) {
            return;
        }

        boolean requireAdjacent = ConfigManager.isRequireAdjacent();
        boolean requireSameMob = ConfigManager.isRequireSameMob();
        int maxCluster = ConfigManager.getMaxCluster();

        if (!requireAdjacent && !requireSameMob && maxCluster < 0) {
            return;
        }

        int x = event.x;
        int y = event.y;
        int z = event.z;

        int againstX = 0;
        int againstY = 0;
        int againstZ = 0;
        boolean foundAdjacent = false;

        if (event.placedAgainst == Blocks.mob_spawner) {
            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                int nx = x + dir.offsetX;
                int ny = y + dir.offsetY;
                int nz = z + dir.offsetZ;
                if (world.getBlock(nx, ny, nz) == Blocks.mob_spawner) {
                    againstX = nx;
                    againstY = ny;
                    againstZ = nz;
                    foundAdjacent = true;
                    break;
                }
            }
        }

        if (!foundAdjacent) {
            if (requireAdjacent) {
                event.setCanceled(true);
                ParticleUtils.playFailedParticles(world, x, y, z);
                return;
            }
        } else if (requireSameMob) {
            String attempted = ItemUtils.getSpawnedType(event.itemInHand);
            String existing = null;
            TileEntity te = world.getTileEntity(againstX, againstY, againstZ);
            if (te instanceof TileEntityMobSpawner) {
                existing = ItemUtils.getSpawnedType((TileEntityMobSpawner) te);
            }
            if (!Objects.equals(existing, attempted)) {
                event.setCanceled(true);
                ParticleUtils.playFailedParticles(world, x, y, z);
                return;
            }
        }

        if (maxCluster >= 0 && countConnectedSpawners(world, x, y, z) > maxCluster) {
            event.setCanceled(true);
            ParticleUtils.playFailedParticles(world, x, y, z);
            return;
        }

        String attempted = ItemUtils.getSpawnedType(event.itemInHand);
        if (attempted != null && !attempted.isEmpty()) {
            TileEntity newTe = world.getTileEntity(x, y, z);
            if (newTe instanceof TileEntityMobSpawner) {
                ((TileEntityMobSpawner) newTe).func_145881_a().setEntityName(attempted);
                newTe.markDirty();
                world.markBlockForUpdate(x, y, z);
            }
        }

        if (foundAdjacent) {
            world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, Blocks.soul_sand.stepSound.func_150496_b(), 1.0f, 1.0f);
        }
    }

    private static int countConnectedSpawners(World world, int startX, int startY, int startZ) {
        Set<String> visited = new HashSet<>();
        Queue<int[]> queue = new ArrayDeque<>();
        queue.add(new int[]{startX, startY, startZ});
        visited.add(startX + "," + startY + "," + startZ);

        int count = 0;
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int cx = current[0];
            int cy = current[1];
            int cz = current[2];

            if ((cx != startX || cy != startY || cz != startZ) && world.getBlock(cx, cy, cz) != Blocks.mob_spawner) {
                continue;
            }
            count++;

            for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                int nx = cx + dir.offsetX;
                int ny = cy + dir.offsetY;
                int nz = cz + dir.offsetZ;
                String key = nx + "," + ny + "," + nz;
                if (visited.add(key) && world.getBlock(nx, ny, nz) == Blocks.mob_spawner) {
                    queue.add(new int[]{nx, ny, nz});
                }
            }
        }
        return count;
    }
}


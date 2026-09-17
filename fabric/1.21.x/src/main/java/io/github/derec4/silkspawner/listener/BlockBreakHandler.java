package io.github.derec4.silkspawner.listener;

import io.github.derec4.silkspawner.config.ConfigManager;
import io.github.derec4.silkspawner.util.ExplosionUtils;
import io.github.derec4.silkspawner.util.ItemUtils;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static io.github.derec4.silkspawner.util.ItemUtils.checkPickaxe;
import static io.github.derec4.silkspawner.util.ItemUtils.checkSilkTouch;

public class BlockBreakHandler {
    public static boolean skipXp;

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register(BlockBreakHandler::onBlockBreak);
    }

    private static void onBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        if (world.isClient || !state.isOf(Blocks.SPAWNER)) {
            return;
        }
        if (!(blockEntity instanceof MobSpawnerBlockEntity spawner)) {
            return;
        }
        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        ItemStack tool = player.getMainHandStack();
        if (!checkSilkTouch(tool)) {
            tool = player.getOffHandStack();
            if (!checkSilkTouch(tool)) {
                return;
            }
        }
        if (!checkPickaxe(tool)) {
            return;
        }

        skipXp = true;
        applyDurabilityDamage(tool);
        applyMagicPlayerDamage(world, player);
        ExplosionUtils.playExplosion(world, pos, ExplosionUtils.rollExplosionSize());

        if (Math.random() >= ConfigManager.getDropChance()) {
            return;
        }

        EntityType<?> entityType = ItemUtils.getSpawnedType(spawner);
        ItemStack spawnerItem = ItemUtils.newSpawnerItem(entityType, 1);
        if (ConfigManager.isDropAsItem()) {
            Block.dropStack(world, pos, spawnerItem);
        } else if (!player.getInventory().insertStack(spawnerItem) && !spawnerItem.isEmpty()) {
            Block.dropStack(world, pos, spawnerItem);
        }
    }

    private static void applyDurabilityDamage(ItemStack tool) {
        int durabilityDamage = ConfigManager.getDurabilityDamage();
        if (durabilityDamage <= 0 || !tool.isDamageable()) {
            return;
        }

        int newDamage = tool.getDamage() + durabilityDamage;
        if (newDamage >= tool.getMaxDamage()) {
            tool.setCount(0);
            return;
        }
        tool.setDamage(newDamage);
    }

    private static void applyMagicPlayerDamage(World world, PlayerEntity player) {
        double playerDamagePercent = ConfigManager.getPlayerDamagePercent();
        if (playerDamagePercent <= 0.0) {
            return;
        }

        float amount = (float) (player.getMaxHealth() * (playerDamagePercent / 100.0));
        if (amount <= 0.0f) {
            return;
        }
        player.damage(world.getDamageSources().magic(), amount);
    }
}

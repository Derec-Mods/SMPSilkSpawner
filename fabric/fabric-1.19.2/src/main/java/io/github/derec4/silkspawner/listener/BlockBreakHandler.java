package io.github.derec4.silkspawner.listener;

import io.github.derec4.silkspawner.config.ConfigManager;
import io.github.derec4.silkspawner.util.ExplosionUtils;
import io.github.derec4.silkspawner.util.ItemUtils;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockBreakHandler {
    public static boolean skipXp;

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register(BlockBreakHandler::onBlockBreak);
    }

    private static void onBlockBreak(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (level.isClientSide() || !state.is(Blocks.SPAWNER)) {
            return;
        }
        if (!(blockEntity instanceof SpawnerBlockEntity spawner)) {
            return;
        }
        if (player.isCreative() || player.isSpectator()) {
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

        skipXp = true;
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

        ExplosionUtils.playExplosion(level, pos, ExplosionUtils.rollExplosionSize());

        if (Math.random() >= ConfigManager.getDropChance()) {
            return;
        }

        String entityId = ItemUtils.getSpawnedType(spawner);
        ItemStack spawnerItem = ItemUtils.newSpawnerItem(entityId, 1);
        if (ConfigManager.isDropAsItem()) {
            Block.popResource(level, pos, spawnerItem);
        } else if (!player.getInventory().add(spawnerItem) && !spawnerItem.isEmpty()) {
            Block.popResource(level, pos, spawnerItem);
        }
    }
}


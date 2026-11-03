package io.github.derec4.silkspawner.util;

import io.github.derec4.silkspawner.config.ConfigManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

public class ExplosionUtils {

    public enum SpawnerExplosionSize {
        NONE,
        SMALL,
        LARGE,
        MASSIVE
    }

    public static SpawnerExplosionSize rollExplosionSize() {
        double roll = Math.random();
        if (roll < ConfigManager.getChanceMassive()) {
            return SpawnerExplosionSize.MASSIVE;
        }
        if (roll < ConfigManager.getChanceLarge()) {
            return SpawnerExplosionSize.LARGE;
        }
        if (roll < ConfigManager.getChanceSmall()) {
            return SpawnerExplosionSize.SMALL;
        }
        return SpawnerExplosionSize.NONE;
    }

    public static void playExplosion(Level level, BlockPos blockPos, SpawnerExplosionSize size) {
        if (size == SpawnerExplosionSize.NONE || level.isClientSide()) {
            return;
        }

        float power = 0.0f;
        if (size == SpawnerExplosionSize.SMALL) {
            power = ConfigManager.getPowerSmall();
        } else if (size == SpawnerExplosionSize.LARGE) {
            power = ConfigManager.getPowerLarge();
        } else if (size == SpawnerExplosionSize.MASSIVE) {
            power = ConfigManager.getPowerMassive();
        }

        if (power > 0.0f) {
            level.explode(
                    null,
                    blockPos.getX() + 0.5,
                    blockPos.getY() + 0.5,
                    blockPos.getZ() + 0.5,
                    power,
                    Explosion.BlockInteraction.BREAK
            );
        }
    }
}


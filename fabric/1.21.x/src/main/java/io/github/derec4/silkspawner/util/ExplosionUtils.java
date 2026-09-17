package io.github.derec4.silkspawner.util;

import io.github.derec4.silkspawner.config.ConfigManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class ExplosionUtils {

    public enum SpawnerExplosionSize {
        NONE,
        SMALL,
        LARGE,
        MASSIVE
    }

    private ExplosionUtils() {
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

    public static void playExplosion(World world, BlockPos blockPos, SpawnerExplosionSize size) {
        if (size == SpawnerExplosionSize.NONE || world.isClient) {
            return;
        }

        float power = switch (size) {
            case SMALL -> ConfigManager.getPowerSmall();
            case LARGE -> ConfigManager.getPowerLarge();
            case MASSIVE -> ConfigManager.getPowerMassive();
            default -> 0.0f;
        };

        world.createExplosion(
                null,
                blockPos.getX() + 0.5,
                blockPos.getY() + 0.5,
                blockPos.getZ() + 0.5,
                power,
                false,
                World.ExplosionSourceType.TNT
        );
    }
}

package io.github.derec4.sMPSilkSpawner.util;

import io.github.derec4.sMPSilkSpawner.config.ConfigManager;
import org.bukkit.Location;
import org.bukkit.World;

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

    public static void playExplosion(World world, Location blockLocation, SpawnerExplosionSize size) {
        if (size == SpawnerExplosionSize.NONE) {
            return;
        }

        Location center = blockLocation.clone().add(0.5, 0.5, 0.5);
        float power = switch (size) {
            case SMALL -> ConfigManager.getPowerSmall();
            case LARGE -> ConfigManager.getPowerLarge();
            case MASSIVE -> ConfigManager.getPowerMassive();
            default -> 0.0f;
        };

        world.createExplosion(center, power, false, true);
    }
}

package io.github.derec4.silkspawner.util;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ParticleUtils {
    public static void playFailedParticles(World world, int x, int y, int z) {
        world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, Blocks.soul_sand.stepSound.func_150496_b(), 1.0f, 1.0f);
        for (int i = 0; i < 5; i++) {
            world.spawnParticle("flame", x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0);
        }
    }
}


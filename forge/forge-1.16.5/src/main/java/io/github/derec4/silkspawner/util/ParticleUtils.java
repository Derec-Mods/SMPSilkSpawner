package io.github.derec4.silkspawner.util;

import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ParticleUtils {
    public static void playFailedParticles(World world, BlockPos pos) {
        if (!(world instanceof ServerWorld)) {
            return;
        }
        ServerWorld serverWorld = (ServerWorld) world;
        serverWorld.playSound(null, pos, SoundEvents.BLOCK_SOUL_SAND_HIT, SoundCategory.BLOCKS, 1.0f, 1.0f);
        serverWorld.spawnParticle(ParticleTypes.FLAME, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5, 0.2, 0.2, 0.2, 0.0);
    }
}


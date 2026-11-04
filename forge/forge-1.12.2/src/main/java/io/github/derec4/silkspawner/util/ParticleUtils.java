package io.github.derec4.silkspawner.util;

import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ParticleUtils {
    public static void playFailedParticles(World world, BlockPos pos) {
        if (!(world instanceof WorldServer)) {
            return;
        }
        WorldServer serverWorld = (WorldServer) world;
        serverWorld.playSound(null, pos, Blocks.SOUL_SAND.getSoundType().getHitSound(), SoundCategory.BLOCKS, 1.0f, 1.0f);
        serverWorld.spawnParticle(EnumParticleTypes.FLAME, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5, 0.2, 0.2, 0.2, 0.0);
    }
}

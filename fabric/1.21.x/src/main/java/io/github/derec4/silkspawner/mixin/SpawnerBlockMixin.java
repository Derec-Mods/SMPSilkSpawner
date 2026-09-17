package io.github.derec4.silkspawner.mixin;

import io.github.derec4.silkspawner.listener.SilkBreakContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpawnerBlock.class)
public class SpawnerBlockMixin {

    @Inject(method = "onStacksDropped", at = @At("HEAD"), cancellable = true)
    private void smpSilkSpawner$skipExperience(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience, CallbackInfo ci) {
        if (SilkBreakContext.shouldSkipXp()) {
            SilkBreakContext.clear();
            ci.cancel();
        }
    }
}

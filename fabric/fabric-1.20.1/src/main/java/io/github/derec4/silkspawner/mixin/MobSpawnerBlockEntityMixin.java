package io.github.derec4.silkspawner.mixin;

import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpawnerBlockEntity.class)
public class MobSpawnerBlockEntityMixin {
    @Inject(method = "onlyOpCanSetNbt", at = @At("HEAD"), cancellable = true)
    private void allowCopy(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}


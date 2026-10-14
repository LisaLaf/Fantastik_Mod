package net.lisalaf.fantastikmod.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Creeper.class)
public class CreeperExplosionMixin {

    @Inject(method = "explodeCreeper", at = @At("HEAD"), cancellable = true)
    private void onExplode(CallbackInfo ci) {
        Creeper creeper = (Creeper)(Object)this;
        CompoundTag persistentData = creeper.getPersistentData();
        if (persistentData.getBoolean("KitsuneProtectedExplosion")) {
            Level level = creeper.level();
            if (!level.isClientSide) {
                final Vec3 position = creeper.position();
                level.explode(null, position.x, position.y, position.z, 3.0F, false, Level.ExplosionInteraction.NONE);
            } ci.cancel();
            persistentData.remove("KitsuneProtectedExplosion");
        }
    }
}
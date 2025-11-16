package net.lisalaf.fantastikmod.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
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

        // Проверяем защиту
        if (persistentData.getBoolean("KitsuneProtectedExplosion")) {
            // Создаем взрыв только с уроном по сущностям (без разрушения блоков)
            Level level = creeper.level();
            if (!level.isClientSide) {
                level.explode(null, creeper.getX(), creeper.getY(), creeper.getZ(), 3.0F, false, Level.ExplosionInteraction.NONE);
            }
            ci.cancel(); // Отменяем оригинальный взрыв

            // Убираем метку после взрыва
            persistentData.remove("KitsuneProtectedExplosion");
        }
    }
}
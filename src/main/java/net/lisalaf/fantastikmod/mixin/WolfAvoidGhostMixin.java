package net.lisalaf.fantastikmod.mixin;

import net.lisalaf.fantastikmod.entity.custom.GhostGuardsEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Wolf.class)
public abstract class WolfAvoidGhostMixin extends Animal {

    protected WolfAvoidGhostMixin(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void addGhostFearGoal(CallbackInfo ci) {
        Wolf self = (Wolf)(Object)this;
        self.goalSelector.addGoal(3, new AvoidEntityGoal<>(
                self,
                GhostGuardsEntity.class,
                10.0F,
                1.4D,
                1.6D,
                (entity) -> true
        ));
    }
}
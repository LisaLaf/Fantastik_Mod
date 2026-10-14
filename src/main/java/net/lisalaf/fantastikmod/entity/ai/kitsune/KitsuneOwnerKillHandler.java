package net.lisalaf.fantastikmod.entity.ai.kitsune;

import net.lisalaf.fantastikmod.entity.custom.KitsuneLightEntity;
import net.lisalaf.fantastikmod.entity.phrases.KitsunePhrases;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class KitsuneOwnerKillHandler {

    @SubscribeEvent
    public static void onKitsuneDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof KitsuneLightEntity kitsune) {
            if (kitsune.isTamed() && event.getSource().getEntity() instanceof Player player) {
                if (player.getUUID().equals(kitsune.getOwnerUUID())) {
                    if (kitsune.level() instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 30; i++) {
                            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                                    kitsune.getX() + (kitsune.getRandom().nextDouble() - 0.5) * 2.0,
                                    kitsune.getY() + kitsune.getRandom().nextDouble() * 2.0,
                                    kitsune.getZ() + (kitsune.getRandom().nextDouble() - 0.5) * 2.0,
                                    3, 0.1, 0.1, 0.1, 0.05);
                            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                                    kitsune.getX() + (kitsune.getRandom().nextDouble() - 0.5) * 2.0,
                                    kitsune.getY() + kitsune.getRandom().nextDouble() * 2.0,
                                    kitsune.getZ() + (kitsune.getRandom().nextDouble() - 0.5) * 2.0,
                                    2, 0.1, 0.3, 0.1, 0.05);
                        }
                        kitsune.playSound(SoundEvents.SOUL_ESCAPE, 1.0F, 0.5F);
                    }

                    player.sendSystemMessage(KitsunePhrases.getOwnerKillMessage());
                }
            }
        }
    }
}
package net.lisalaf.fantastikmod.entity.ai.moondeer;

import net.lisalaf.fantastikmod.entity.custom.MoonDeerEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class MoonDeerDeathEffect {

    @SubscribeEvent
    public static void onMoonDeerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof MoonDeerEntity deer) {
            if (deer.level() instanceof ServerLevel serverLevel) {
                Vec3 pos = deer.position();

                deer.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.5F, 0.3F);
                deer.setGlowingTag(true);

                for (int i = 0; i < 40; i++) {
                    double angle = deer.getRandom().nextDouble() * Math.PI * 2;
                    double radius = 1.0 + deer.getRandom().nextDouble() * 2.0;
                    double dx = Math.cos(angle) * radius;
                    double dz = Math.sin(angle) * radius;
                    double dy = -0.2 + deer.getRandom().nextDouble() * 2.5;

                    serverLevel.sendParticles(ParticleTypes.END_ROD,
                            pos.x + dx, pos.y + dy, pos.z + dz,
                            2, 0.08, 0.08, 0.08, 0.02);
                    serverLevel.sendParticles(ParticleTypes.FIREWORK,
                            pos.x + dx, pos.y + dy, pos.z + dz,
                            1, 0.05, 0.05, 0.05, 0.01);
                }

                for (int i = 0; i < 100; i++) {
                    double progress = i / 100.0; // 0.0 → 1.0
                    double radius = 1.5 * Math.pow(1.0 - progress, 2.5) + 0.05;

                    double angle = deer.getRandom().nextDouble() * Math.PI * 2;
                    double dx = Math.cos(angle) * radius * deer.getRandom().nextDouble();
                    double dz = Math.sin(angle) * radius * deer.getRandom().nextDouble();

                    double dy = Math.pow(progress, 0.6) * 10.0;
                    if (deer.getRandom().nextDouble() > progress * 0.8) {
                        serverLevel.sendParticles(ParticleTypes.END_ROD,
                                pos.x + dx, pos.y + dy, pos.z + dz,
                                1, 0.02, 0.02, 0.02, 0.01);
                        serverLevel.sendParticles(ParticleTypes.FIREWORK,
                                pos.x + dx, pos.y + dy, pos.z + dz,
                                1, 0.01, 0.01, 0.01, 0.005);
                    }
                }
            }
        }
    }
}
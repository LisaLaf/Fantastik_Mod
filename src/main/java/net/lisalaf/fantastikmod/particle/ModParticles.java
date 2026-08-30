package net.lisalaf.fantastikmod.particle;

import com.mojang.serialization.Codec;
import net.lisalaf.fantastikmod.fantastikmod;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, fantastikmod.MOD_ID);

    public static final RegistryObject<ParticleType<ColoredParticleOptions>> COLORED_LEAVES_PARTICLE =
            PARTICLE_TYPES.register("leaves_particle",
                    () -> new ParticleType<>(false, ColoredParticleOptions.DESERIALIZER) {
                        @Override
                        public com.mojang.serialization.Codec<ColoredParticleOptions> codec() {
                            return ColoredParticleOptions.CODEC;
                        }
                    });

    public static final RegistryObject<ParticleType<WaterRippleOptions>> WATER_RIPPLE =
            PARTICLE_TYPES.register("water_ripple",
                    () -> new ParticleType<>(false, WaterRippleOptions.DESERIALIZER) {
                        @Override
                        public Codec<WaterRippleOptions> codec() {
                            return WaterRippleOptions.CODEC;
                        }
                    });
    public static final RegistryObject<ParticleType<HeatParticleOptions>> HEAT =
            PARTICLE_TYPES.register("heat",
                    () -> new ParticleType<>(false, HeatParticleOptions.DESERIALIZER) {
                        @Override
                        public Codec<HeatParticleOptions> codec() { return HeatParticleOptions.CODEC; }
                    });

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}
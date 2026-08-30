package net.lisalaf.fantastikmod.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

public class HeatParticleOptions implements ParticleOptions {

    private final float scale;

    public static final Codec<HeatParticleOptions> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.FLOAT.fieldOf("scale").forGetter(o -> o.scale))
                    .apply(instance, HeatParticleOptions::new));

    public HeatParticleOptions(float scale) { this.scale = scale; }
    public float getScale() { return scale; }

    @Override
    public ParticleType<?> getType() { return ModParticles.HEAT.get(); }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) { buf.writeFloat(scale); }

    @Override
    public String writeToString() {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(getType()) + " " + scale;
    }

    public static final Deserializer<HeatParticleOptions> DESERIALIZER = new Deserializer<>() {
        @Override
        public HeatParticleOptions fromCommand(ParticleType<HeatParticleOptions> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            return new HeatParticleOptions(reader.readFloat());
        }

        @Override
        public HeatParticleOptions fromNetwork(ParticleType<HeatParticleOptions> type, FriendlyByteBuf buf) {
            return new HeatParticleOptions(buf.readFloat());
        }
    };
}
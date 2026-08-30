package net.lisalaf.fantastikmod.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

public class WaterRippleOptions implements ParticleOptions {

    private final float scale;

    public static final Codec<WaterRippleOptions> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("scale").forGetter(options -> options.scale)
            ).apply(instance, WaterRippleOptions::new)
    );

    public WaterRippleOptions(float scale) {
        this.scale = scale;
    }

    public float getScale() { return scale; }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.WATER_RIPPLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFloat(scale);
    }

    @Override
    public String writeToString() {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(getType()) + " " + scale;
    }

    public static final Deserializer<WaterRippleOptions> DESERIALIZER = new Deserializer<>() {
        @Override
        public WaterRippleOptions fromCommand(ParticleType<WaterRippleOptions> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float scale = reader.readFloat();
            return new WaterRippleOptions(scale);
        }

        @Override
        public WaterRippleOptions fromNetwork(ParticleType<WaterRippleOptions> type, FriendlyByteBuf buffer) {
            return new WaterRippleOptions(buffer.readFloat());
        }
    };
}
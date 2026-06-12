package net.lisalaf.fantastikmod.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

public class ColoredParticleOptions implements ParticleOptions {
    private final float red;
    private final float green;
    private final float blue;

    public static final Codec<ColoredParticleOptions> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("red").forGetter(options -> options.red),
                    Codec.FLOAT.fieldOf("green").forGetter(options -> options.green),
                    Codec.FLOAT.fieldOf("blue").forGetter(options -> options.blue)
            ).apply(instance, ColoredParticleOptions::new)
    );

    public ColoredParticleOptions(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public float getRed() { return red; }
    public float getGreen() { return green; }
    public float getBlue() { return blue; }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.COLORED_LEAVES_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFloat(red);
        buffer.writeFloat(green);
        buffer.writeFloat(blue);
    }

    @Override
    public String writeToString() {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(getType()) +
                " " + red + " " + green + " " + blue;
    }

    public static final Deserializer<ColoredParticleOptions> DESERIALIZER = new Deserializer<>() {
        @Override
        public ColoredParticleOptions fromCommand(ParticleType<ColoredParticleOptions> type,
                                                  StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float red = reader.readFloat();
            reader.expect(' ');
            float green = reader.readFloat();
            reader.expect(' ');
            float blue = reader.readFloat();
            return new ColoredParticleOptions(red, green, blue);
        }

        @Override
        public ColoredParticleOptions fromNetwork(ParticleType<ColoredParticleOptions> type,
                                                  FriendlyByteBuf buffer) {
            return new ColoredParticleOptions(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
        }
    };
}
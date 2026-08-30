package net.lisalaf.fantastikmod.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeatParticle extends TextureSheetParticle {

    public HeatParticle(ClientLevel level, double x, double y, double z, float scale) {
        super(level, x, y, z);
        this.quadSize = scale;
        this.lifetime = 20 + random.nextInt(10);
        this.gravity = -0.02f;
        this.xd = (random.nextDouble() - 0.5) * 0.02;
        this.yd = 0.03 + random.nextDouble() * 0.05;
        this.zd = (random.nextDouble() - 0.5) * 0.02;
        this.rCol = 1.0f;
        this.gCol = 0.3f + random.nextFloat() * 0.3f;
        this.bCol = 0.0f;
        this.alpha = 0.7f;
    }

    @Override
    public void tick() {
        super.tick();
        this.alpha = 0.7f * (1.0f - (float) this.age / this.lifetime);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<HeatParticleOptions> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) { this.spriteSet = spriteSet; }

        @Override
        public Particle createParticle(HeatParticleOptions options, ClientLevel level,
                                       double x, double y, double z, double dx, double dy, double dz) {
            HeatParticle particle = new HeatParticle(level, x, y, z, options.getScale());
            particle.pickSprite(this.spriteSet);
            return particle;
        }
    }
}
package net.lisalaf.fantastikmod.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WaterRippleParticle extends TextureSheetParticle {

    private final float baseScale;

    public WaterRippleParticle(ClientLevel level, double x, double y, double z, float scale) {
        super(level, x, y, z);
        this.baseScale = scale;
        this.quadSize = scale;
        this.lifetime = 20;
        this.gravity = 0;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.alpha = 0.6f;
        this.rCol = 0.7f;
        this.gCol = 0.85f;
        this.bCol = 1.0f;
    }

    @Override
    public void tick() {
        super.tick();
        float ageFraction = (float) this.age / this.lifetime;
        this.quadSize = this.baseScale * (1.0f + ageFraction * 2.0f);
        this.alpha = 0.6f * (1.0f - ageFraction);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<WaterRippleOptions> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(WaterRippleOptions options, ClientLevel level,
                                       double x, double y, double z, double dx, double dy, double dz) {
            WaterRippleParticle particle = new WaterRippleParticle(level, x, y, z, options.getScale());
            particle.pickSprite(this.spriteSet);
            return particle;
        }
    }
}
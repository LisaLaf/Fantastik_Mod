package net.lisalaf.fantastikmod.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LeavesParticle extends TextureSheetParticle {
    private final float rotSpeed;

    public LeavesParticle(ClientLevel level, double x, double y, double z,
                          double speedX, double speedY, double speedZ,
                          float red, float green, float blue) {
        super(level, x, y, z);

        // РАЗМЕР маленький: от 0.05 до 0.1
        this.quadSize = 0.05F + random.nextFloat() * 0.05F;

        this.lifetime = 40 + random.nextInt(20);
        this.gravity = 0.06F;
        this.rotSpeed = (random.nextFloat() - 0.5f) * 2.0F;

        // ЗАМЕДЛЯЕМ СКОРОСТЬ
        this.xd = speedX * 0.5;  // в 2 раза медленнее по горизонтали
        this.yd = speedY * 0.7;  // немного медленнее по вертикали
        this.zd = speedZ * 0.5;  // в 2 раза медленнее по горизонтали

        // СЛУЧАЙНОЕ ЗАТЕМНЕНИЕ
        float darken = 0.6F + random.nextFloat() * 0.4F;
        this.rCol = red * darken;
        this.gCol = green * darken;
        this.bCol = blue * darken;

        this.alpha = 1.0f;
    }

    @Override
    public void tick() {
        super.tick();
        this.oRoll = this.roll;
        this.roll += this.rotSpeed * 0.1f;
        // ЗАТУХАНИЕ в конце жизни
        if (this.age > this.lifetime * 0.7f) {
            this.alpha = 1.0f - (this.age - this.lifetime * 0.7f) / (this.lifetime * 0.3f);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<ColoredParticleOptions> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(ColoredParticleOptions options, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            LeavesParticle particle = new LeavesParticle(level, x, y, z, dx, dy, dz,
                    options.getRed(), options.getGreen(), options.getBlue());
            particle.pickSprite(this.spriteSet);
            return particle;
        }
    }
}
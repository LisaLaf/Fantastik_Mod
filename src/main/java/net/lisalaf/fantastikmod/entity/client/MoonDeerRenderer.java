package net.lisalaf.fantastikmod.entity.client;

import net.lisalaf.fantastikmod.entity.custom.MoonDeerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@SuppressWarnings("removal")
public class MoonDeerRenderer extends GeoEntityRenderer<MoonDeerEntity> {

    private static final String[] MODEL_VARIANTS = {
            "geo/moon_deer/moon_deer_0.geo.json",
            "geo/moon_deer/moon_deer_1.geo.json",
            "geo/moon_deer/moon_deer_2.geo.json"
    };

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("fantastikmod", "textures/entity/moon_deer/moon_deer.png");

    private static final ResourceLocation HORNS_TEXTURE =
            new ResourceLocation("fantastikmod", "textures/entity/moon_deer/moon_deer_horns.png");

    private static final ResourceLocation TAMED_TEXTURE =
            new ResourceLocation("fantastikmod", "textures/entity/moon_deer/mascot_moon_deer.png");

    private static final String ANIMATION_PATH = "animations/moon_deer.animation.json";

    public MoonDeerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MoonDeerModel());
    }

    @Override
    public void render(MoonDeerEntity entity, float entityYaw, float partialTick,
                       com.mojang.blaze3d.vertex.PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(1.0f, 1.0f, 1.0f);
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        renderHorns(entity, poseStack, bufferSource, partialTick, packedLight);
        renderTamedOverlay(entity, poseStack, bufferSource, partialTick, packedLight);
    }

    private void renderHorns(MoonDeerEntity entity, com.mojang.blaze3d.vertex.PoseStack poseStack,
                             MultiBufferSource bufferSource, float partialTick, int packedLight) {
        RenderType hornsRenderType = RenderType.eyes(HORNS_TEXTURE);

        long dayTime = entity.level().getDayTime();
        int moonPhase = entity.level().getMoonPhase();
        float timeFactor = calculateTimeBrightness(dayTime);
        float moonFactor = calculateMoonBrightness(moonPhase);

        float combinedBrightness = timeFactor * moonFactor;
        float brightness = 0.1F + 0.9F * combinedBrightness;
        float alpha = 0.2F + 0.8F * combinedBrightness;

        float[] moonColor = getMoonColor(moonPhase);

        super.reRender(this.getGeoModel().getBakedModel(this.getGeoModel().getModelResource(entity)),
                poseStack, bufferSource, entity, hornsRenderType,
                bufferSource.getBuffer(hornsRenderType), partialTick,
                packedLight, getPackedOverlay(entity, 0),
                moonColor[0], moonColor[1], moonColor[2], alpha * brightness);
    }

    private float calculateTimeBrightness(long dayTime) {
        long timeOfDay = dayTime % 24000;
        float normalizedTime = (float) timeOfDay / 24000.0F;
        float angle = (normalizedTime - 0.5417f) * 2.0f * (float) Math.PI;
        float sineWave = (float) Math.sin(angle);
        return Math.max(0.0f, (sineWave + 1.0f) * 0.5f);
    }

    private float calculateMoonBrightness(int moonPhase) {
        switch (moonPhase) {
            case 0: return 1.0F;
            case 1: return 0.8F;
            case 2: return 0.6F;
            case 3: return 0.4F;
            case 4: return 0.1F;
            case 5: return 0.4F;
            case 6: return 0.6F;
            case 7: return 0.8F;
            default: return 0.5F;
        }
    }

    private float[] getMoonColor(int moonPhase) {
        switch (moonPhase) {
            case 0: return new float[]{0.4F, 0.7F, 1.0F};
            case 1: case 7: return new float[]{0.5F, 0.7F, 0.9F};
            case 2: case 6: return new float[]{0.6F, 0.6F, 0.8F};
            case 3: case 5: return new float[]{0.7F, 0.5F, 0.7F};
            case 4: return new float[]{0.8F, 0.4F, 0.6F};
            default: return new float[]{0.7F, 0.7F, 1.0F};
        }
    }

    private void renderTamedOverlay(MoonDeerEntity entity, com.mojang.blaze3d.vertex.PoseStack poseStack,
                                    MultiBufferSource bufferSource, float partialTick, int packedLight) {
        if (entity.isTamed()) {
            RenderType tamedRenderType = RenderType.entityCutoutNoCull(TAMED_TEXTURE);
            super.reRender(this.getGeoModel().getBakedModel(this.getGeoModel().getModelResource(entity)),
                    poseStack, bufferSource, entity, tamedRenderType,
                    bufferSource.getBuffer(tamedRenderType), partialTick,
                    packedLight, getPackedOverlay(entity, 0),
                    1.0F, 1.0F, 1.0F, 1.0F);

            float pulse = (float) (Math.sin(entity.tickCount * 0.2f) * 0.2f + 0.8f);
            RenderType glowRenderType = RenderType.eyes(TAMED_TEXTURE);
            super.reRender(this.getGeoModel().getBakedModel(this.getGeoModel().getModelResource(entity)),
                    poseStack, bufferSource, entity, glowRenderType,
                    bufferSource.getBuffer(glowRenderType), partialTick,
                    15728880, getPackedOverlay(entity, 0),
                    0.7F * pulse, 0.9F * pulse, 1.0F * pulse, 0.4F * pulse);
        }
    }

    public static class MoonDeerModel extends GeoModel<MoonDeerEntity> {

        @Override
        public ResourceLocation getModelResource(MoonDeerEntity entity) {
            int index = Math.abs(entity.getUUID().hashCode()) % 3;
            return new ResourceLocation("fantastikmod", MODEL_VARIANTS[index]);
        }

        @Override
        public ResourceLocation getTextureResource(MoonDeerEntity entity) {
            return TEXTURE;
        }

        @Override
        public ResourceLocation getAnimationResource(MoonDeerEntity entity) {
            return new ResourceLocation("fantastikmod", ANIMATION_PATH);
        }
    }
}
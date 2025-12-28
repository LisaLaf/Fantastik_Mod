package net.lisalaf.fantastikmod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lisalaf.fantastikmod.entity.custom.BlueButterflyEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@SuppressWarnings("removal")
public class BlueButterflyRenderer extends GeoEntityRenderer<BlueButterflyEntity> {

    private Boolean hasOptifineCache = null;

    private boolean hasOptifine() {
        if (hasOptifineCache == null) {
            try {
                // Простая проверка
                Class.forName("optifine.Installer");
                hasOptifineCache = true;
            } catch (ClassNotFoundException e) {
                hasOptifineCache = false;
            }
        }
        return hasOptifineCache;
    }

    public BlueButterflyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BlueButterflyModel());
        this.shadowRadius = 0.2F; // Маленькая тень
    }

    @Override
    public void render(BlueButterflyEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {

        float scale = entity.getScale();
        poseStack.scale(scale, scale, scale);

        // Основной рендер с максимальным светом
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, 15728880);

        if (!hasOptifine()){
            renderGlowingPollen(entity, poseStack, bufferSource, partialTick);
        }

       // renderGlowingPollen(entity, poseStack, bufferSource, partialTick);
    }

    private void renderGlowingPollen(BlueButterflyEntity entity, PoseStack poseStack,
                                     MultiBufferSource bufferSource, float partialTick) {

        // Оптимизация: рендерим только если близко к камере
        Vec3 cameraPos = this.entityRenderDispatcher.camera.getPosition();
        Vec3 entityPos = entity.getPosition(partialTick);
        if (cameraPos.distanceToSqr(entityPos) > 16 * 16) return;

        float time = (entity.tickCount + partialTick) * 0.15f;

        // 3 светящиеся частицы
        for (int i = 0; i < 3; i++) {
            float angle = time + i * 2.094f; // 120 градусов
            float offsetX = Mth.cos(angle) * 0.3f;
            float offsetY = Mth.sin(time * 1.5f + i) * 0.15f + 0.1f;
            float offsetZ = Mth.sin(angle) * 0.3f;

            // Пульсация размера и прозрачности
            float pulse = Mth.sin(time * 2 + i) * 0.3f + 0.7f;
            float particleScale = 0.04f * pulse;
            float alpha = 0.5f * pulse;

            poseStack.pushPose();
            poseStack.translate(offsetX, offsetY, offsetZ);
            poseStack.scale(particleScale, particleScale, particleScale);

            // ВАЖНО: используем RenderType.eyes() для свечения
            RenderType glowType = RenderType.eyes(getTextureLocation(entity));
            super.reRender(this.getGeoModel().getBakedModel(this.getGeoModel().getModelResource(entity)),
                    poseStack, bufferSource, entity, glowType,
                    bufferSource.getBuffer(glowType), partialTick,
                    15728880, // Максимальный свет
                    getPackedOverlay(entity, 0),
                    0.3F, 0.5F, 1.0F, alpha); // Яркий голубой цвет

            poseStack.popPose();
        }
    }


    @Override
    protected float getDeathMaxRotation(BlueButterflyEntity entity) {
        return 0.0F; // Не вращается при смерти
    }

    private void renderPollenEffect(BlueButterflyEntity entity, PoseStack poseStack,
                                    MultiBufferSource bufferSource, float partialTick) {

        // Оптимизация: рендерим эффекты только если ентити близко к камере
        Vec3 cameraPos = this.entityRenderDispatcher.camera.getPosition();
        Vec3 entityPos = entity.getPosition(partialTick);
        if (cameraPos.distanceToSqr(entityPos) > 32 * 32) return; // Не рендерить дальше 32 блоков

        float time = (entity.tickCount + partialTick) * 0.1f;

        // Простой эффект пыльцы - 2-3 частицы вокруг бабочки
        for (int i = 0; i < 3; i++) {
            float angle = time + i * 2.094f; // 120 градусов между частицами
            float offsetX = Mth.cos(angle) * 0.3f;
            float offsetY = Mth.sin(time * 2 + i) * 0.2f + 0.1f;
            float offsetZ = Mth.sin(angle) * 0.3f;

            // Пульсация размера частиц
            float particleScale = 0.05f + Mth.sin(time * 3 + i) * 0.02f;

            // Прозрачность
            float alpha = 0.3f + Mth.sin(time * 2 + i) * 0.2f;

            poseStack.pushPose();
            poseStack.translate(offsetX, offsetY, offsetZ);
            poseStack.scale(particleScale, particleScale, particleScale);

            // Рендерим простую голубую частицу
            RenderType particleType = RenderType.entityTranslucent(getTextureLocation(entity));
            super.reRender(this.getGeoModel().getBakedModel(this.getGeoModel().getModelResource(entity)),
                    poseStack, bufferSource, entity, particleType,
                    bufferSource.getBuffer(particleType), partialTick,
                    15728880, getPackedOverlay(entity, 0),
                    0.4F, 0.6F, 1.0F, alpha); // Голубой цвет

            poseStack.popPose();
        }
    }

    @Override
    public ResourceLocation getTextureLocation(BlueButterflyEntity entity) {
        return new ResourceLocation("fantastikmod", "textures/entity/blue_butterfly.png");
    }


}
package net.lisalaf.fantastikmod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lisalaf.fantastikmod.entity.custom.KitsuneLightEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;


@SuppressWarnings("removal")
public class KitsuneLightRenderer extends GeoEntityRenderer<KitsuneLightEntity> {

    public KitsuneLightRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new KitsuneLightModel());
    }

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


    @Override
    public void render(KitsuneLightEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();



        if(entity.isBaby()) {
            poseStack.scale(1.3f, 1.3f, 1.3f);
            float brightness = entity.isBaby() ? 1.2f : 1.0f; // +20% яркости для детёнышей
            super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight); // Детёныш меньше
        } else {
            poseStack.scale(3.0f, 3.0f, 3.0f); // Взрослый нормальный размер
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        //renderEyes(entity, poseStack, bufferSource, partialTick, packedLight);

        if (!hasOptifine()){
            renderEyes(entity, poseStack, bufferSource, partialTick, packedLight);
        }


        poseStack.popPose();
    }
    private void renderEyes(KitsuneLightEntity entity, PoseStack poseStack,
                            MultiBufferSource bufferSource, float partialTick, int packedLight) {
        // Получаем текстуру глаз на основе варианта entity
        ResourceLocation eyeTexture = getEyeTexture(entity);
        RenderType eyeRenderType = RenderType.eyes(eyeTexture);

        // Рендерим свечение глаз
        super.reRender(this.getGeoModel().getBakedModel(this.getGeoModel().getModelResource(entity)),
                poseStack, bufferSource, entity, eyeRenderType,
                bufferSource.getBuffer(eyeRenderType), partialTick,
                packedLight, getPackedOverlay(entity, 0),
                1.0F, 1.0F, 1.0F, 1.0F);
    }

    private ResourceLocation getEyeTexture(KitsuneLightEntity entity) {
        // Используем вариант entity для выбора текстуры глаз
        int eyeVariant = entity.getEyeVariant() % 8; // 0-6 вариантов
        return new ResourceLocation("fantastikmod",
                "textures/entity/kitsune/eyes/kitsune_eyes_" + eyeVariant + ".png");
    }

    @Override
    protected void renderNameTag(KitsuneLightEntity entity, Component displayName, PoseStack poseStack,
                                 MultiBufferSource bufferSource, int packedLight) {
        // Проверяем, что имя действительно должно отображаться
        if (!this.shouldShowName(entity)) {
            return;
        }

        double distance = this.entityRenderDispatcher.distanceToSqr(entity);
        if (distance > 4096.0D) { // Максимальная дистанция для отображения имени
            return;
        }

        poseStack.pushPose();

        // Более разумное масштабирование для имени
        float scale = 0.025f;

        // Для взрослых особей - немного больше, но не в 3 раза
        if (!entity.isBaby()) {
            scale = 0.009f; // Умеренное увеличение вместо 3x
        }

        // Позиционирование имени над entity
        poseStack.translate(0.0D, entity.getBbHeight() + -0.6F, 0.0D);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.scale(-scale, -scale, scale);

        // Настройка матрицы для текста
        Matrix4f matrix4f = poseStack.last().pose();
        float backgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
        int backgroundColor = (int)(backgroundOpacity * 255.0F) << 24;

        Font font = this.getFont();

        // Получаем реальное имя для отображения
        Component nameToRender = entity.getDisplayName();
        float textWidth = (float)(-font.width(nameToRender) / 2);

        // Рендерим фон текста
        font.drawInBatch(nameToRender, textWidth, 0, 0x20FFFFFF, false,
                matrix4f, bufferSource, Font.DisplayMode.SEE_THROUGH,
                backgroundColor, packedLight);

        // Рендерим основной текст
        font.drawInBatch(nameToRender, textWidth, 0, -1, false,
                matrix4f, bufferSource, Font.DisplayMode.NORMAL,
                0, packedLight);

        poseStack.popPose();
    }



}
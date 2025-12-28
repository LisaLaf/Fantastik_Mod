package net.lisalaf.fantastikmod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lisalaf.fantastikmod.entity.custom.MoonDeerEntity;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.renderer.GeoEntityRenderer;


@SuppressWarnings("removal")
public class MoonDeerRenderer extends GeoEntityRenderer<MoonDeerEntity> {

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

    private static final ResourceLocation HORNS_TEXTURE =
            new ResourceLocation("fantastikmod", "textures/entity/moon_deer_horns.png");

    private static final ResourceLocation TAMED_TEXTURE =
            new ResourceLocation("fantastikmod", "textures/entity/mascot_moon_deer.png");

    public MoonDeerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MoonDeerModel());
    }


    @Override
    public void render(MoonDeerEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        if(entity.isBaby()) {
            poseStack.scale(1.0f, 1.0f, 1.0f); // Взрослый нормальный размер
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        if (!hasOptifine()){
            renderHorns(entity, poseStack, bufferSource, partialTick, packedLight);
            renderTamedOverlay(entity, poseStack, bufferSource, partialTick, packedLight);
        }

       // renderHorns(entity, poseStack, bufferSource, partialTick, packedLight);

        // Рендерим текстуру приручения поверх основной текстуры
       // renderTamedOverlay(entity, poseStack, bufferSource, partialTick, packedLight);
    }

    private void renderHorns(MoonDeerEntity entity, PoseStack poseStack,
                             MultiBufferSource bufferSource, float partialTick, int packedLight) {
        RenderType hornsRenderType = RenderType.eyes(HORNS_TEXTURE);

        long dayTime = entity.level().getDayTime();
        int moonPhase = entity.level().getMoonPhase();
        float timeFactor = calculateTimeBrightness(dayTime);
        float moonFactor = calculateMoonBrightness(moonPhase);

        // Комбинируем влияние времени суток и фазы луны
        float combinedBrightness = timeFactor * moonFactor;
        float brightness = 0.1F + 0.9F * combinedBrightness;
        float alpha = 0.2F + 0.8F * combinedBrightness;

        // Цвет тоже может меняться в зависимости от фазы луны
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

        // Создаем плавную волну: максимум ночью, минимум днем
        // Сдвигаем на π/2 чтобы ночь (13000-23000) была на пике
        float angle = (normalizedTime - 0.5417f) * 2.0f * (float) Math.PI; // 13000/24000 ≈ 0.5417
        float sineWave = (float) Math.sin(angle);

        // Преобразуем от -1..1 к 0..1 и усиливаем контраст
        return Math.max(0.0f, (sineWave + 1.0f) * 0.5f);
    }
    private float calculateMoonBrightness(int moonPhase) {
        // Фазы луны в Minecraft (0-7):
        // 0 - полнолуние, 4 - новолуние
        switch (moonPhase) {
            case 0: return 1.0F; // Полнолуние - 100%
            case 1: return 0.8F;
            case 2: return 0.6F;
            case 3: return 0.4F;
            case 4: return 0.1F; // Новолуние - 10%
            case 5: return 0.4F;
            case 6: return 0.6F;
            case 7: return 0.8F;
            default: return 0.5F;
        }
    }

    private float[] getMoonColor(int moonPhase) {
        // Разные цвета свечения в зависимости от фазы луны
        switch (moonPhase) {
            case 0: // Полнолуние - ярко-голубой
                return new float[]{0.4F, 0.7F, 1.0F};
            case 1: case 7: // Почти полнолуние
                return new float[]{0.5F, 0.7F, 0.9F};
            case 2: case 6: // Половина луны
                return new float[]{0.6F, 0.6F, 0.8F};
            case 3: case 5: // Серп
                return new float[]{0.7F, 0.5F, 0.7F};
            case 4: // Новолуние - слабое фиолетовое свечение
                return new float[]{0.8F, 0.4F, 0.6F};
            default:
                return new float[]{0.7F, 0.7F, 1.0F};
        }
    }

    private void renderTamedOverlay(MoonDeerEntity entity, PoseStack poseStack,
                                    MultiBufferSource bufferSource, float partialTick, int packedLight) {
        if (entity.isTamed()) {
            // Основная текстура приручения
            RenderType tamedRenderType = RenderType.entityCutoutNoCull(TAMED_TEXTURE);
            super.reRender(this.getGeoModel().getBakedModel(this.getGeoModel().getModelResource(entity)),
                    poseStack, bufferSource, entity, tamedRenderType,
                    bufferSource.getBuffer(tamedRenderType), partialTick,
                    packedLight, getPackedOverlay(entity, 0),
                    1.0F, 1.0F, 1.0F, 1.0F);

            // Пульсирующее свечение
            float pulse = (float) (Math.sin(entity.tickCount * 0.2f) * 0.2f + 0.8f); // Пульсация от 0.6 до 1.0
            RenderType glowRenderType = RenderType.eyes(TAMED_TEXTURE);
            super.reRender(this.getGeoModel().getBakedModel(this.getGeoModel().getModelResource(entity)),
                    poseStack, bufferSource, entity, glowRenderType,
                    bufferSource.getBuffer(glowRenderType), partialTick,
                    15728880, getPackedOverlay(entity, 0),
                    0.7F * pulse, 0.9F * pulse, 1.0F * pulse, 0.4F * pulse); // Пульсирующее голубое свечение
        }
    }

    @Override
    protected void renderNameTag(MoonDeerEntity entity, Component component, PoseStack poseStack,
                                 MultiBufferSource bufferSource, int packedLight) {
        // Минимальное смещение - только чуть выше
        double yOffset = entity.getBbHeight() + 3.6D; // Минимально выше тела
        double forwardOffset = 0.4D; // Очень маленький сдвиг вперед

        // Получаем текущую позицию
        Vec3 cameraPos = this.entityRenderDispatcher.camera.getPosition();
        Vec3 entityPos = entity.getPosition(1.0F);

        // Вычисляем минимальное смещение вперед
        float yRot = entity.getYRot();
        double forwardX = -Math.sin(Math.toRadians(yRot)) * forwardOffset;
        double forwardZ = Math.cos(Math.toRadians(yRot)) * forwardOffset;

        // Новая позиция для имени
        double x = entityPos.x() + forwardX;
        double y = entityPos.y() + yOffset;
        double z = entityPos.z() + forwardZ;

        // Рендерим имя в новой позиции
        poseStack.pushPose();
        poseStack.translate(x - cameraPos.x(), y - cameraPos.y(), z - cameraPos.z());
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.scale(-0.025F, -0.025F, 0.025F);

        Font font = this.getFont();
        float width = -font.width(component) / 2.0F;

        font.drawInBatch(component, width, 0, -1, false, poseStack.last().pose(),
                bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);

        poseStack.popPose();
    }

}
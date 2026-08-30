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

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        if (!hasOptifine()){
            renderEyes(entity, poseStack, bufferSource, partialTick, packedLight);
            renderMarkings(entity, poseStack, bufferSource, partialTick, packedLight);
        }

        poseStack.popPose();
    }
    private void renderEyes(KitsuneLightEntity entity, PoseStack poseStack,
                            MultiBufferSource bufferSource, float partialTick, int packedLight) {
        ResourceLocation eyeTexture = getEyeTexture(entity);
        RenderType eyeRenderType = RenderType.eyes(eyeTexture);

        super.reRender(this.getGeoModel().getBakedModel(this.getGeoModel().getModelResource(entity)),
                poseStack, bufferSource, entity, eyeRenderType,
                bufferSource.getBuffer(eyeRenderType), partialTick,
                packedLight, getPackedOverlay(entity, 0),
                1.0F, 1.0F, 1.0F, 1.0F);
    }

    private ResourceLocation getEyeTexture(KitsuneLightEntity entity) {
        int eyeVariant = entity.getEyeVariant() % 8; // 0-6 вариантов
        return new ResourceLocation("fantastikmod",
                "textures/entity/kitsune/eyes/kitsune_eyes_" + eyeVariant + ".png");
    }

    @Override
    protected void renderNameTag(KitsuneLightEntity entity, Component displayName, PoseStack poseStack,
                                 MultiBufferSource bufferSource, int packedLight) {
        if (!this.shouldShowName(entity)) {
            return;
        }

        double distance = this.entityRenderDispatcher.distanceToSqr(entity);
        if (distance > 4096.0D) {
            return;
        }

        poseStack.pushPose();

        float scale = 0.025f;

        if (!entity.isBaby()) {
            scale = 0.009f;
        }

        poseStack.translate(0.0D, entity.getBbHeight() + -0.6F, 0.0D);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.scale(-scale, -scale, scale);

        Matrix4f matrix4f = poseStack.last().pose();
        float backgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
        int backgroundColor = (int)(backgroundOpacity * 255.0F) << 24;

        Font font = this.getFont();

        Component nameToRender = entity.getDisplayName();
        float textWidth = (float)(-font.width(nameToRender) / 2);

        font.drawInBatch(nameToRender, textWidth, 0, 0x20FFFFFF, false,
                matrix4f, bufferSource, Font.DisplayMode.SEE_THROUGH,
                backgroundColor, packedLight);

        font.drawInBatch(nameToRender, textWidth, 0, -1, false,
                matrix4f, bufferSource, Font.DisplayMode.NORMAL,
                0, packedLight);

        poseStack.popPose();
    }

    private void renderMarkings(KitsuneLightEntity entity, PoseStack poseStack,
                                MultiBufferSource bufferSource, float partialTick, int packedLight) {
        ResourceLocation markingsTexture = getMarkingsTexture(entity);
        RenderType markingsRenderType = RenderType.entityCutoutNoCull(markingsTexture);

        super.reRender(this.getGeoModel().getBakedModel(this.getGeoModel().getModelResource(entity)),
                poseStack, bufferSource, entity, markingsRenderType,
                bufferSource.getBuffer(markingsRenderType), partialTick,
                packedLight, getPackedOverlay(entity, 0),
                1.0F, 1.0F, 1.0F, 1.0F);
    }

    private ResourceLocation getMarkingsTexture(KitsuneLightEntity entity) {
        int variant = entity.getVariant() % 3;
        return new ResourceLocation("fantastikmod",
                "textures/entity/kitsune/kitsune_markings/kitsune_markings_" + variant + ".png");
    }



}
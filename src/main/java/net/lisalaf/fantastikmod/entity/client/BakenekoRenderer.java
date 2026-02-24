package net.lisalaf.fantastikmod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.lisalaf.fantastikmod.entity.custom.BakenekoEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BakenekoRenderer extends GeoEntityRenderer<BakenekoEntity> {
    public BakenekoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BakenekoModel());
    }

    @Override
    public void render(BakenekoEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        if (entity.isBaby()) {
            poseStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            poseStack.scale(1.0f, 1.0f, 1.0f);
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
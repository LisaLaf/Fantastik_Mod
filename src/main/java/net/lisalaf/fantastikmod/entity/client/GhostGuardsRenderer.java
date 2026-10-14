package net.lisalaf.fantastikmod.entity.client;

import net.lisalaf.fantastikmod.entity.custom.GhostGuardsEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

@SuppressWarnings("removal")
public class GhostGuardsRenderer extends GeoEntityRenderer<GhostGuardsEntity> {

    public GhostGuardsRenderer(EntityRendererProvider.Context context) {
        super(context, new GhostGuardsModel());
    }

    @Override
    public RenderType getRenderType(GhostGuardsEntity animatable, ResourceLocation texture,
                                    @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucentEmissive(texture);
    }
}
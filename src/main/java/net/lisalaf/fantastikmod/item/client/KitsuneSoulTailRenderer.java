package net.lisalaf.fantastikmod.item.client;

import net.lisalaf.fantastikmod.item.custom.KitsuneSoulTailItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

@SuppressWarnings("removal")
public class KitsuneSoulTailRenderer extends GeoArmorRenderer<KitsuneSoulTailItem> {

    public KitsuneSoulTailRenderer() {
        super(new KitsuneSoulTailModel());
    }

    @Override
    public RenderType getRenderType(KitsuneSoulTailItem animatable, ResourceLocation texture,
                                    MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucentEmissive(texture);
    }

}
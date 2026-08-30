package net.lisalaf.fantastikmod.block.custom.renderer;

import net.lisalaf.fantastikmod.block.custom.entity.RoyalBedBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class RoyalBedRenderer extends GeoBlockRenderer<RoyalBedBlockEntity> {

    public RoyalBedRenderer(BlockEntityRendererProvider.Context context) {
        super(new RoyalBedModel());
    }

    public RenderType getRenderType(RoyalBedBlockEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }
}
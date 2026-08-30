package net.lisalaf.fantastikmod.block.custom.renderer;

import net.lisalaf.fantastikmod.block.custom.entity.TapestryBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class TapestryRenderer extends GeoBlockRenderer<TapestryBlockEntity> {

    public TapestryRenderer(BlockEntityRendererProvider.Context context) {
        super(new TapestryModel());
    }
}
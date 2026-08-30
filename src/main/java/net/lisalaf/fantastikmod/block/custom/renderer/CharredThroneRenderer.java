package net.lisalaf.fantastikmod.block.custom.renderer;

import net.lisalaf.fantastikmod.block.custom.entity.CharredThroneBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class CharredThroneRenderer extends GeoBlockRenderer<CharredThroneBlockEntity> {
    public CharredThroneRenderer(BlockEntityRendererProvider.Context context) {
        super(new CharredThroneModel());
    }
}
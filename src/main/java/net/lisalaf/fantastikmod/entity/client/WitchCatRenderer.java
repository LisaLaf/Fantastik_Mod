package net.lisalaf.fantastikmod.entity.client;

import net.lisalaf.fantastikmod.entity.custom.WitchCatEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WitchCatRenderer extends GeoEntityRenderer<WitchCatEntity> {

    public WitchCatRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WitchCatModel());
    }
}
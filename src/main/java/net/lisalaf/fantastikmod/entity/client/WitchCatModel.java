package net.lisalaf.fantastikmod.entity.client;

import net.lisalaf.fantastikmod.entity.custom.WitchCatEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

@SuppressWarnings("removal")
public class WitchCatModel extends GeoModel<WitchCatEntity> {

    @Override
    public ResourceLocation getModelResource(WitchCatEntity entity) {
        return new ResourceLocation("fantastikmod", "geo/witch_cat.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WitchCatEntity entity) {
        return new ResourceLocation("fantastikmod", "textures/entity/witch_cat/witch_cat.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WitchCatEntity entity) {
        return new ResourceLocation("fantastikmod", "animations/witch_cat.animation.json");
    }
}
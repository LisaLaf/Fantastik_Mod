package net.lisalaf.fantastikmod.entity.client;

import net.lisalaf.fantastikmod.entity.custom.BakenekoEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

@SuppressWarnings("removal")
public class BakenekoModel extends GeoModel<BakenekoEntity> {
    @Override
    public ResourceLocation getModelResource(BakenekoEntity entity) {
        return new ResourceLocation("fantastikmod", "geo/bakeneko.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BakenekoEntity entity) {
        return new ResourceLocation("fantastikmod", "textures/entity/bakeneko/bakeneko.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BakenekoEntity entity) {
        return new ResourceLocation("fantastikmod", "animations/bakeneko.animation.json");
    }


}
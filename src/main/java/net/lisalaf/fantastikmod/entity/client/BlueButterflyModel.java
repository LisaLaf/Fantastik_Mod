package net.lisalaf.fantastikmod.entity.client;

import net.lisalaf.fantastikmod.entity.custom.BlueButterflyEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlueButterflyModel extends GeoModel<BlueButterflyEntity> {
    @Override
    public ResourceLocation getModelResource(BlueButterflyEntity entity) {
        return ResourceLocation.fromNamespaceAndPath("fantastikmod", "geo/blue_butterfly.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BlueButterflyEntity entity) {
        return ResourceLocation.fromNamespaceAndPath("fantastikmod", "textures/entity/blue_butterfly.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BlueButterflyEntity entity) {
        return ResourceLocation.fromNamespaceAndPath("fantastikmod", "animations/blue_butterfly.animation.json");
    }
}
package net.lisalaf.fantastikmod.entity.client;

import net.lisalaf.fantastikmod.entity.custom.KitsuneLightEntity;
import net.lisalaf.fantastikmod.entity.custom.MoonDeerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MoonDeerModel extends GeoModel<MoonDeerEntity> {
    @Override
    public ResourceLocation getModelResource(MoonDeerEntity entity) {
        // ИСПРАВЛЕНО: новый метод вместо deprecated
        return ResourceLocation.fromNamespaceAndPath("fantastikmod", "geo/moon_deer.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MoonDeerEntity entity) {
        return ResourceLocation.fromNamespaceAndPath("fantastikmod", "textures/entity/moon_deer.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MoonDeerEntity entity) {
        // ИСПРАВЛЕНО: новый метод вместо deprecated
        return ResourceLocation.fromNamespaceAndPath("fantastikmod", "animations/moon_deer.animation.json");
    }
}
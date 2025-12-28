package net.lisalaf.fantastikmod.entity.client;

import net.lisalaf.fantastikmod.entity.custom.KitsuneLightEntity;
import net.lisalaf.fantastikmod.entity.custom.MoonDeerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

@SuppressWarnings("removal")
public class MoonDeerModel extends GeoModel<MoonDeerEntity> {
    @Override
    public ResourceLocation getModelResource(MoonDeerEntity entity) {
        // ИСПРАВЛЕНО: новый метод вместо deprecated
        return new ResourceLocation("fantastikmod", "geo/moon_deer.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MoonDeerEntity entity) {
        return new ResourceLocation("fantastikmod", "textures/entity/moon_deer.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MoonDeerEntity entity) {
        // ИСПРАВЛЕНО: новый метод вместо deprecated
        return new ResourceLocation("fantastikmod", "animations/moon_deer.animation.json");
    }
}
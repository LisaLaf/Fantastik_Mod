package net.lisalaf.fantastikmod.entity.client;

import net.lisalaf.fantastikmod.entity.custom.IceDragonEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class IceDragonModel extends GeoModel<IceDragonEntity> {

    @Override
    public ResourceLocation getModelResource(IceDragonEntity entity) {
        // УБРАЛИ renderingEyes - модель всегда основная
        if (entity.getGrowthStage() == IceDragonEntity.STAGE_BABY) {
            return ResourceLocation.fromNamespaceAndPath("fantastikmod", "geo/ice_dragon_baby.geo.json");
        }

        String gender = entity.isFemale() ? "female" : "male";
        return ResourceLocation.fromNamespaceAndPath("fantastikmod", "geo/ice_dragon_" + gender + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(IceDragonEntity entity) {
        // ОСНОВНАЯ ТЕКСТУРА
        int variant = entity.getVariant();
        String texturePath = "textures/entity/dragon/ice_dragon_" + variant + ".png";

        return ResourceLocation.fromNamespaceAndPath("fantastikmod", texturePath);
    }

    @Override
    public ResourceLocation getAnimationResource(IceDragonEntity entity) {
        return ResourceLocation.fromNamespaceAndPath("fantastikmod", "animations/ice_dragon.animation.json");
    }



    public ResourceLocation getEyeTexture(IceDragonEntity entity) {
        int eyeVariant = entity.getEyeVariant() % 5;
        return ResourceLocation.fromNamespaceAndPath("fantastikmod",
                "textures/entity/dragon/eyes/ice_dragon_eyes_" + eyeVariant + ".png");
    }

    // УБРАЛИ setRenderingEyes - больше не нужно
}
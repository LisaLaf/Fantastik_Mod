package net.lisalaf.fantastikmod.entity.client;

import net.lisalaf.fantastikmod.entity.custom.IceDragonEntity;
import net.lisalaf.fantastikmod.entity.custom.KitsuneLightEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class KitsuneLightModel extends GeoModel<KitsuneLightEntity> {
    private boolean renderingEyes = false;

    @Override
    public ResourceLocation getModelResource(KitsuneLightEntity entity) {
        // ИСПРАВЛЕНО: новый метод вместо deprecated
        return ResourceLocation.fromNamespaceAndPath("fantastikmod", "geo/kitsune_light.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KitsuneLightEntity entity) {
        // Используем вариант из сущности для получения правильной текстуры
        int variant = entity.getVariant();
        return ResourceLocation.fromNamespaceAndPath("fantastikmod",
                "textures/entity/kitsune/kitsune_light_" + variant + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(KitsuneLightEntity entity) {
        // ИСПРАВЛЕНО: новый метод вместо deprecated
        return ResourceLocation.fromNamespaceAndPath("fantastikmod", "animations/kitsune_light.animation.json");
    }

    // Метод для получения текстуры глаз (для рендерера)
    public ResourceLocation getEyeTexture(KitsuneLightEntity entity) {
        int eyeVariant = entity.getEyeVariant() % 7; // Используй getEyeVariant() вместо getVariant()
        return ResourceLocation.fromNamespaceAndPath("fantastikmod",
                "textures/entity/kitsune/eyes/kitsune_eyes_" + eyeVariant + ".png");
    }

    // Установить режим рендеринга глаз
    public void setRenderingEyes(boolean renderingEyes) {
        this.renderingEyes = renderingEyes;
    }
}
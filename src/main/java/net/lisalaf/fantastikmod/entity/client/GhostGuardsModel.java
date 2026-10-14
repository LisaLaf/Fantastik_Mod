package net.lisalaf.fantastikmod.entity.client;

import net.lisalaf.fantastikmod.entity.custom.GhostGuardsEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

@SuppressWarnings("removal")
public class GhostGuardsModel extends GeoModel<GhostGuardsEntity> {

    @Override
    public ResourceLocation getModelResource(GhostGuardsEntity animatable) {
        return new ResourceLocation("fantastikmod", "geo/entity/ghost/ghost_guards.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GhostGuardsEntity entity) {
        int variant = entity.getTextureVariant() % 4;
        return new ResourceLocation("fantastikmod",
                "textures/entity/ghost/guards/ghost_guards_" + variant + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(GhostGuardsEntity animatable) {
        return new ResourceLocation("fantastikmod", "animations/entity/ghost_guards.animation.json");
    }
}
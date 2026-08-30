package net.lisalaf.fantastikmod.block.custom.renderer;

import net.lisalaf.fantastikmod.fantastikmod;
import net.lisalaf.fantastikmod.block.custom.entity.TapestryBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

@SuppressWarnings("removal")
public class TapestryModel extends GeoModel<TapestryBlockEntity> {

    @Override
    public ResourceLocation getModelResource(TapestryBlockEntity animatable) {
        return new ResourceLocation(fantastikmod.MOD_ID, "geo/block/tapestry.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TapestryBlockEntity animatable) {
        return new ResourceLocation(fantastikmod.MOD_ID, "textures/block/tapestry/" + animatable.getTextureName() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(TapestryBlockEntity animatable) {
        return new ResourceLocation(fantastikmod.MOD_ID, "animations/block/tapestry.animation.json");
    }
}
package net.lisalaf.fantastikmod.block.custom.renderer;

import net.lisalaf.fantastikmod.fantastikmod;
import net.lisalaf.fantastikmod.block.custom.entity.CharredThroneBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

@SuppressWarnings("removal")
public class CharredThroneModel extends GeoModel<CharredThroneBlockEntity> {

    @Override
    public ResourceLocation getModelResource(CharredThroneBlockEntity animatable) {
        return new ResourceLocation(fantastikmod.MOD_ID, "geo/block/charred_throne.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CharredThroneBlockEntity animatable) {
        return new ResourceLocation(fantastikmod.MOD_ID, "textures/block/charred_throne.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CharredThroneBlockEntity animatable) {
        return new ResourceLocation(fantastikmod.MOD_ID, "animations/block/charred_throne.animation.json");
    }
}
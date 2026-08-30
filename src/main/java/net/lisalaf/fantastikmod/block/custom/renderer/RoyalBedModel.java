package net.lisalaf.fantastikmod.block.custom.renderer;

import net.lisalaf.fantastikmod.fantastikmod;
import net.lisalaf.fantastikmod.block.custom.entity.RoyalBedBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

@SuppressWarnings("removal")
public class RoyalBedModel extends GeoModel<RoyalBedBlockEntity> {
    @Override
    public ResourceLocation getModelResource(RoyalBedBlockEntity animatable) {
        return new ResourceLocation(fantastikmod.MOD_ID, "geo/block/royal_bed.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RoyalBedBlockEntity animatable) {
        return new ResourceLocation(fantastikmod.MOD_ID, "textures/block/royal_bed.png");
    }

    @Override
    public ResourceLocation getAnimationResource(RoyalBedBlockEntity animatable) {
        return new ResourceLocation(fantastikmod.MOD_ID, "animations/block/royal_bed.animation.json");
    }
}
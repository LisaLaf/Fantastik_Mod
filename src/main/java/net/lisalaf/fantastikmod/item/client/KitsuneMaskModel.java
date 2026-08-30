package net.lisalaf.fantastikmod.item.client;

import net.lisalaf.fantastikmod.fantastikmod;
import net.lisalaf.fantastikmod.item.custom.KitsuneMaskItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

@SuppressWarnings("removal")
public class KitsuneMaskModel extends GeoModel<KitsuneMaskItem> {

    @Override
    public ResourceLocation getModelResource(KitsuneMaskItem animatable) {
        return new ResourceLocation(fantastikmod.MOD_ID, "geo/armor/kitsune_mask.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KitsuneMaskItem animatable) {
        return new ResourceLocation(fantastikmod.MOD_ID, "textures/models/armor/kitsune_mask_" + animatable.getColorVariant() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(KitsuneMaskItem animatable) {
        return new ResourceLocation(fantastikmod.MOD_ID, "animations/armor/kitsune_mask");
    }
}
package net.lisalaf.fantastikmod.item.client;

import net.lisalaf.fantastikmod.fantastikmod;
import net.lisalaf.fantastikmod.item.custom.KitsuneSoulMaskItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

@SuppressWarnings("removal")
public class KitsuneSoulMaskModel extends GeoModel<KitsuneSoulMaskItem> {

    @Override
    public ResourceLocation getModelResource(KitsuneSoulMaskItem animatable) {
        return new ResourceLocation(fantastikmod.MOD_ID, "geo/armor/kitsune_mask.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KitsuneSoulMaskItem animatable) {
        return new ResourceLocation(fantastikmod.MOD_ID, "textures/models/armor/kitsune_soul_mask_" + animatable.getColorVariant() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(KitsuneSoulMaskItem animatable) {
        return new ResourceLocation(fantastikmod.MOD_ID, "animations/armor/kitsune_mask");
    }
}
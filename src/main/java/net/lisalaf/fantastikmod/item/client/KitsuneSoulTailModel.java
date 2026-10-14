package net.lisalaf.fantastikmod.item.client;

import net.lisalaf.fantastikmod.fantastikmod;
import net.lisalaf.fantastikmod.item.custom.KitsuneSoulTailItem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

@SuppressWarnings("removal")
public class KitsuneSoulTailModel extends GeoModel<KitsuneSoulTailItem> {

    @Override
    public ResourceLocation getModelResource(KitsuneSoulTailItem animatable) {
        return new ResourceLocation(fantastikmod.MOD_ID, "geo/armor/kitsune_soul_tail.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KitsuneSoulTailItem animatable) {
        return new ResourceLocation(fantastikmod.MOD_ID, "textures/models/armor/kitsune_soul_tail.png");
    }

    @Override
    public ResourceLocation getAnimationResource(KitsuneSoulTailItem animatable) {
        return new ResourceLocation(fantastikmod.MOD_ID, "animations/armor/kitsune_soul_tail.animation.json");
    }

}
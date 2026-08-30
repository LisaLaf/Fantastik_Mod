package net.lisalaf.fantastikmod.item.client;

import net.lisalaf.fantastikmod.fantastikmod;
import net.lisalaf.fantastikmod.item.custom.WingsItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

@SuppressWarnings("removal")
public class WingsModel extends GeoModel<WingsItem> {

    @Override
    public ResourceLocation getModelResource(WingsItem animatable) {
        return new ResourceLocation(fantastikmod.MOD_ID, "geo/armor/wings.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WingsItem animatable) {
        return new ResourceLocation(fantastikmod.MOD_ID, "textures/models/armor/wings.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WingsItem animatable) {
        return new ResourceLocation(fantastikmod.MOD_ID, "animations/armor/wings.animation.json");
    }
}
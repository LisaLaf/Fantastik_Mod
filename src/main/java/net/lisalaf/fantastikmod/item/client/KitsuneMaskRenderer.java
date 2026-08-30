package net.lisalaf.fantastikmod.item.client;

import net.lisalaf.fantastikmod.item.custom.KitsuneMaskItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class KitsuneMaskRenderer extends GeoArmorRenderer<KitsuneMaskItem> {

    public KitsuneMaskRenderer() {
        super(new KitsuneMaskModel());
    }
}
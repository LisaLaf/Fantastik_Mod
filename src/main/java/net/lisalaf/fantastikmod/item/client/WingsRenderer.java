package net.lisalaf.fantastikmod.item.client;

import net.lisalaf.fantastikmod.item.custom.WingsItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class WingsRenderer extends GeoArmorRenderer<WingsItem> {

    public WingsRenderer() {
        super(new WingsModel());
    }
}
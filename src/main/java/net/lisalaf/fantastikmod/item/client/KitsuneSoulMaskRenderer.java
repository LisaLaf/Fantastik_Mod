package net.lisalaf.fantastikmod.item.client;

import net.lisalaf.fantastikmod.item.custom.KitsuneSoulMaskItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

@SuppressWarnings("removal")
public class KitsuneSoulMaskRenderer extends GeoArmorRenderer<KitsuneSoulMaskItem> {

    public KitsuneSoulMaskRenderer() {
        super(new KitsuneSoulMaskModel());
    }

    public void prepForRender(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
        super.prepForRender(entity, stack, slot, original);
    }
}
package net.lisalaf.fantastikmod.event;

import net.lisalaf.fantastikmod.entity.ModEntities;
import net.lisalaf.fantastikmod.entity.custom.BlueButterflyEntity;
import net.lisalaf.fantastikmod.entity.custom.IceDragonEntity;
import net.lisalaf.fantastikmod.entity.custom.KitsuneLightEntity;
import net.lisalaf.fantastikmod.entity.custom.MoonDeerEntity;
import net.lisalaf.fantastikmod.fantastikmod;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = fantastikmod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.KITSUNE_LIGHT.get(), KitsuneLightEntity.createAttributes().build());
        event.put(ModEntities.ICE_DRAGON.get(), IceDragonEntity.createAttributes().build());
        event.put(ModEntities.MOON_DEER.get(), MoonDeerEntity.createAttributes().build());
        event.put(ModEntities.BLUE_BUTTERFLY.get(), BlueButterflyEntity.createAttributes().build());
    }
}
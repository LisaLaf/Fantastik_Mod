package net.lisalaf.fantastikmod.event;

import net.lisalaf.fantastikmod.entity.client.BakenekoRenderer;
import net.lisalaf.fantastikmod.entity.client.IceDragonRenderer;
import net.lisalaf.fantastikmod.entity.client.KitsuneLightRenderer;
import net.lisalaf.fantastikmod.entity.ModEntities;
import net.lisalaf.fantastikmod.entity.client.MoonDeerRenderer;
import net.lisalaf.fantastikmod.entity.client.BlueButterflyRenderer;
import net.lisalaf.fantastikmod.fantastikmod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = fantastikmod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.KITSUNE_LIGHT.get(), KitsuneLightRenderer::new);
        event.registerEntityRenderer(ModEntities.ICE_DRAGON.get(), IceDragonRenderer::new);
        event.registerEntityRenderer(ModEntities.MOON_DEER.get(), MoonDeerRenderer::new);
        event.registerEntityRenderer(ModEntities.BLUE_BUTTERFLY.get(), BlueButterflyRenderer::new);
        event.registerEntityRenderer(ModEntities.BAKENEKO.get(), BakenekoRenderer::new);
    }
}
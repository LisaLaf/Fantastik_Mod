package net.lisalaf.fantastikmod.event;

import net.lisalaf.fantastikmod.block.ModBlockEntities;
import net.lisalaf.fantastikmod.entity.client.BakenekoRenderer;
import net.lisalaf.fantastikmod.entity.client.IceDragonRenderer;
import net.lisalaf.fantastikmod.entity.client.KitsuneLightRenderer;
import net.lisalaf.fantastikmod.entity.client. WitchCatRenderer;
import net.lisalaf.fantastikmod.entity.client. SeatEntityRenderer;
import net.lisalaf.fantastikmod.entity.ModEntities;
import net.lisalaf.fantastikmod.entity.client.MoonDeerRenderer;
import net.lisalaf.fantastikmod.entity.client.BlueButterflyRenderer;
import net.lisalaf.fantastikmod.fantastikmod;
import net.lisalaf.fantastikmod.item.client.FireDragonScalesOverlay;
import net.lisalaf.fantastikmod.item.client.KitsuneMaskOverlay;
import net.lisalaf.fantastikmod.item.custom.WingsItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.lisalaf.fantastikmod.block.custom.renderer.TapestryRenderer;
import net.lisalaf.fantastikmod.block.custom.renderer.RoyalBedRenderer;
import net.lisalaf.fantastikmod.block.custom.renderer.CharredThroneRenderer;

@Mod.EventBusSubscriber(modid = fantastikmod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.KITSUNE_LIGHT.get(), KitsuneLightRenderer::new);
        event.registerEntityRenderer(ModEntities.ICE_DRAGON.get(), IceDragonRenderer::new);
        event.registerEntityRenderer(ModEntities.MOON_DEER.get(), MoonDeerRenderer::new);
        event.registerEntityRenderer(ModEntities.BLUE_BUTTERFLY.get(), BlueButterflyRenderer::new);
        event.registerEntityRenderer(ModEntities.BAKENEKO.get(), BakenekoRenderer::new);
        event.registerEntityRenderer(ModEntities.WITCH_CAT.get(), WitchCatRenderer::new);
        event.registerEntityRenderer(ModEntities.SEAT.get(), SeatEntityRenderer::new);

        event.registerBlockEntityRenderer(ModBlockEntities.TAPESTRY.get(), TapestryRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.ROYAL_BED.get(), RoyalBedRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CHARRED_THRONE.get(), CharredThroneRenderer::new);
    }
    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("kitsune_mask_overlay", new KitsuneMaskOverlay());

        event.registerAboveAll("fire_dragon_scales_overlay", new FireDragonScalesOverlay());

    }
}
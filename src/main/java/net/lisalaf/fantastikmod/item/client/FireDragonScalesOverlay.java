package net.lisalaf.fantastikmod.item.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.lisalaf.fantastikmod.effect.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

@SuppressWarnings("removal")
public class FireDragonScalesOverlay implements IGuiOverlay {

    private static final ResourceLocation OVERLAY = new ResourceLocation("fantastikmod", "textures/misc/fire_dragon_scales_overlay.png");

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (!player.hasEffect(ModEffects.FIRE_DRAGON_SCALES.get())) return;

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.4F);
        guiGraphics.blit(OVERLAY, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
    }
}
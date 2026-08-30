package net.lisalaf.fantastikmod.item.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.lisalaf.fantastikmod.fantastikmod;
import net.lisalaf.fantastikmod.item.custom.KitsuneSoulMaskItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

@SuppressWarnings("removal")
public class KitsuneMaskOverlay implements IGuiOverlay {

    private static final ResourceLocation MASK_OVERLAY = new ResourceLocation(fantastikmod.MOD_ID, "textures/misc/kitsune_mask_overlay.png");

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        if (!(helmet.getItem() instanceof KitsuneSoulMaskItem)) return;

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.9F);
        guiGraphics.blit(MASK_OVERLAY, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
    }
}
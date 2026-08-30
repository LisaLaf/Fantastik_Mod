package net.lisalaf.fantastikmod.dialog;

import net.lisalaf.fantastikmod.dialog.mobs.BakenekoDialog;
import net.lisalaf.fantastikmod.entity.custom.BakenekoEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DialogClientHelper {

    @OnlyIn(Dist.CLIENT)
    public static void openDialog(Entity entity, Dialog dialog) {
        Minecraft.getInstance().execute(() -> {
            Minecraft.getInstance().setScreen(new DialogScreen(entity, dialog));
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static void openBakenekoDialog(BakenekoEntity entity, Player player) {
        Minecraft.getInstance().execute(() -> {
            Minecraft.getInstance().setScreen(new DialogScreen(entity, new BakenekoDialog(entity, player)));
        });
    }
}
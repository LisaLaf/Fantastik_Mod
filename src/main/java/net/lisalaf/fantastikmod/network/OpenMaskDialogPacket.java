package net.lisalaf.fantastikmod.network;

import net.lisalaf.fantastikmod.dialog.DialogClientHelper;
import net.lisalaf.fantastikmod.dialog.mobs.kitsune.kitsune1tail.Kitsune1TailSoulMaskDialog;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenMaskDialogPacket {

    private final int kitsuneEntityId;

    public OpenMaskDialogPacket(int kitsuneEntityId) {
        this.kitsuneEntityId = kitsuneEntityId;
    }

    public static void encode(OpenMaskDialogPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.kitsuneEntityId);
    }

    public static OpenMaskDialogPacket decode(FriendlyByteBuf buf) {
        return new OpenMaskDialogPacket(buf.readInt());
    }

    public static void handle(OpenMaskDialogPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                if (Minecraft.getInstance().level == null) return;
                Entity entity = Minecraft.getInstance().level.getEntity(msg.kitsuneEntityId);
                if (entity == null) return;
                DialogClientHelper.openDialog(entity, new Kitsune1TailSoulMaskDialog());
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
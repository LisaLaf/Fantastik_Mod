package net.lisalaf.fantastikmod.dialog.mobs;

import net.lisalaf.fantastikmod.dialog.Dialog;
import net.lisalaf.fantastikmod.dialog.DialogNode;
import net.lisalaf.fantastikmod.entity.custom.BakenekoEntity;
import net.lisalaf.fantastikmod.entity.phrases.BakenekoPhrases;
import net.lisalaf.fantastikmod.fantastikmod;
import net.lisalaf.fantastikmod.network.DialogPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class BakenekoDialog extends Dialog {

    private final BakenekoEntity bakeneko;
    private final Player player;

    public BakenekoDialog(BakenekoEntity bakeneko, Player player) {
        this.bakeneko = bakeneko;
        this.player = player;
        setupDialog();
    }

    @Override
    protected void setupDialog() {
        // Начальный узел
        addNode("start", new DialogNode(
                Component.translatable("dialog.bakeneko.greeting"), "start"
        ).addOption(Component.translatable("dialog.player.ask_bakeneko_who"), "response_who")
                .addOption(Component.translatable("dialog.player.ask_bakeneko_business"), "response_business")
                .addOption(Component.translatable("dialog.player.ask_bakeneko_follow"), "response_follow")
                .addOption(Component.translatable("dialog.player.ask_bakeneko_steal"), "response_steal")
                .addOption(Component.translatable("dialog.player.goodbye"), "end"));

        // 1.0 Кто ты?
        addNode("response_who", new DialogNode(
                Component.translatable("dialog.bakeneko.answer_who"), "response_who"
        ).addOption(Component.translatable("dialog.player.ask_bakeneko_who_1"), "response_who_1")
                .addOption(Component.translatable("dialog.player.ask_bakeneko_who_2"), "response_who_2")
                .addOption(Component.translatable("dialog.player.back"), "start"));

        addNode("response_who_1", new DialogNode(
                Component.translatable("dialog.bakeneko.answer_who_1"), "response_who_1"
        ).addOption(Component.translatable("dialog.player.back"), "start"));

        addNode("response_who_2", new DialogNode(
                Component.translatable("dialog.bakeneko.answer_who_2"), "response_who_2"
        ).addOption(Component.translatable("dialog.player.back"), "start"));

        // 2.0 Каких дел?
        addNode("response_business", new DialogNode(
                Component.translatable("dialog.bakeneko.answer_business"), "response_business"
        ).addOption(Component.translatable("dialog.player.ask_bakeneko_business_1"), "response_business_1")
                .addOption(Component.translatable("dialog.player.ask_bakeneko_business_2"), "response_business_2")
                .addOption(Component.translatable("dialog.player.ask_bakeneko_business_3"), "end"));

        addNode("response_business_1", new DialogNode(
                Component.translatable("dialog.bakeneko.answer_business_1"), "response_business_1"
        ).addOption(Component.translatable("dialog.player.back"), "start"));

        addNode("response_business_2", new DialogNode(
                Component.translatable("dialog.bakeneko.answer_business_2"), "response_business_2"
        ).addOption(Component.translatable("dialog.player.back"), "start"));

        // 3.0 Зачем преследуешь?
        addNode("response_follow", new DialogNode(
                Component.translatable("dialog.bakeneko.answer_follow"), "response_follow"
        ).addOption(Component.translatable("dialog.player.ask_bakeneko_follow_1"), "response_follow_1")
                .addOption(Component.translatable("dialog.player.ask_bakeneko_follow_2"), "response_follow_2"));

        addNode("response_follow_1", new DialogNode(
                Component.translatable("dialog.bakeneko.answer_follow_1"), "response_follow_1"
        ).addOption(Component.translatable("dialog.player.back"), "start"));

        addNode("response_follow_2", new DialogNode(
                Component.translatable("dialog.bakeneko.answer_follow_2"), "response_follow_2"
        ).addOption(Component.translatable("dialog.player.back"), "start"));

        // 4.0 Кража
        addNode("response_steal", new DialogNode(
                Component.translatable("dialog.bakeneko.answer_steal"), "response_steal"
        ).addOption(Component.translatable("dialog.player.ask_bakeneko_steal_1"), "response_steal_1")
                .addOption(Component.translatable("dialog.player.ask_bakeneko_steal_2"), "response_steal_2")
                .addOption(Component.translatable("dialog.player.ask_bakeneko_steal_3"), "response_steal_3"));

        addNode("response_steal_1", new DialogNode(
                Component.translatable("dialog.bakeneko.answer_steal_1"), "response_steal_1"
        ).addOption(Component.translatable("dialog.player.back"), "start"));

        addNode("response_steal_2", new DialogNode(
                Component.translatable("dialog.bakeneko.answer_steal_2"), "response_steal_2"
        ).addOption(Component.translatable("dialog.player.back"), "start"));

        addNode("response_steal_3", new DialogNode(
                Component.translatable("dialog.bakeneko.answer_steal_3"), "response_steal_3"
        ).addOption(Component.translatable("dialog.player.back"), "start"));

        addNode("end", new DialogNode(
                Component.translatable("dialog.bakeneko.farewell"), "end"
        ));
    }

    @Override
    public void selectOption(int optionIndex) {
        DialogNode node = nodes.get(currentDialogId);
        if (node == null || optionIndex >= node.getOptions().size()) {
            super.selectOption(optionIndex);
            return;
        }

        String toNode = node.getOptions().get(optionIndex).getNextNodeId();

        if (player.level().isClientSide) {
            fantastikmod.sendToServer(new DialogPacket(bakeneko.getId(), optionIndex, "bakeneko", toNode));
        }

        if ("response_follow_2".equals(toNode)
                || "response_business_2".equals(toNode)
                || "response_steal_1".equals(toNode)) {
            if (player.level().isClientSide) {
                Minecraft.getInstance().setScreen(null);
            }
            return;
        }

        super.selectOption(optionIndex);
    }
}
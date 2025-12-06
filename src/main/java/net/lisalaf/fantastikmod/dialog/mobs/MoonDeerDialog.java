package net.lisalaf.fantastikmod.dialog.mobs;

import net.lisalaf.fantastikmod.dialog.Dialog;
import net.lisalaf.fantastikmod.dialog.DialogNode;
import net.minecraft.network.chat.Component;

public class MoonDeerDialog extends Dialog {

    @Override
    protected void setupDialog() {
        // === НАЧАЛО ДИАЛОГА ===
        addNode("start", new DialogNode(
                Component.translatable("dialog.moon_deer.greeting")
        ).addOption(Component.translatable("dialog.player.ask_outside_world"), "outside_world")
                .addOption(Component.translatable("dialog.player.ask_feelings"), "feelings")
                .addOption(Component.translatable("dialog.player.goodbye"), "end"));

        // === МИР ЗА ПРЕДЕЛАМИ ===
        addNode("outside_world", new DialogNode(
                Component.translatable("dialog.moon_deer.outside_world")
        ).addOption(Component.translatable("dialog.player.ask_feelings"), "feelings")
                .addOption(Component.translatable("dialog.player.back"), "start"));

        // === САМОЧУВСТВИЕ ===
        addNode("feelings", new DialogNode(
                Component.translatable("dialog.moon_deer.feelings")
        ).addOption(Component.translatable("dialog.player.ask_outside_world"), "outside_world")
                .addOption(Component.translatable("dialog.player.back"), "start"));

        // === КОНЕЦ ДИАЛОГА ===
        addNode("end", new DialogNode(
                Component.translatable("dialog.moon_deer.farewell")
        ));
    }
}
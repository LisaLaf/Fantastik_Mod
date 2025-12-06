package net.lisalaf.fantastikmod.dialog.mobs;

import net.lisalaf.fantastikmod.dialog.Dialog;
import net.lisalaf.fantastikmod.dialog.DialogNode;
import net.minecraft.network.chat.Component;

public class WildMoonDeerDialog extends Dialog {

    @Override
    protected void setupDialog() {
        // === НАЧАЛО ДИАЛОГА ===
        addNode("start", new DialogNode(
                Component.translatable("dialog.wild_moon_deer.greeting")
        ).addOption(Component.translatable("dialog.player.ask_who"), "who_are_you")
                .addOption(Component.translatable("dialog.player.ask_place"), "about_place")
                .addOption(Component.translatable("dialog.player.goodbye"), "end"));

        // === КТО ТЫ? ===
        addNode("who_are_you", new DialogNode(
                Component.translatable("dialog.wild_moon_deer.who_are_you")
        ).addOption(Component.translatable("dialog.player.ask_bored"), "ask_bored")
                .addOption(Component.translatable("dialog.player.back"), "start"));

        // === ЭТО МЕСТО ===
        addNode("about_place", new DialogNode(
                Component.translatable("dialog.wild_moon_deer.about_place")
        ).addOption(Component.translatable("dialog.player.ask_who_haiti"), "who_haiti")
                .addOption(Component.translatable("dialog.player.ask_leave"), "ask_leave")
                .addOption(Component.translatable("dialog.player.back"), "start"));

        // === КТО ТАКОЙ ХАТИ? ===
        addNode("who_haiti", new DialogNode(
                Component.translatable("dialog.wild_moon_deer.who_haiti")
        ).addOption(Component.translatable("dialog.player.ask_brothers"), "about_brothers")
                .addOption(Component.translatable("dialog.player.ask_if_eat"), "if_eat_moon")
                .addOption(Component.translatable("dialog.player.ask_where_haiti"), "where_haiti")
                .addOption(Component.translatable("dialog.player.back"), "about_place"));

        // === БРАТЬЯ ===
        addNode("about_brothers", new DialogNode(
                Component.translatable("dialog.wild_moon_deer.about_brothers")
        ).addOption(Component.translatable("dialog.player.ask_if_eat"), "if_eat_moon")
                .addOption(Component.translatable("dialog.player.ask_where_haiti"), "where_haiti")
                .addOption(Component.translatable("dialog.player.back"), "who_haiti"));

        // === ЕСЛИ СЪЕСТ ЛУНУ ===
        addNode("if_eat_moon", new DialogNode(
                Component.translatable("dialog.wild_moon_deer.if_eat_moon")
        ).addOption(Component.translatable("dialog.player.ask_dangerous"), "is_dangerous")
                .addOption(Component.translatable("dialog.player.ask_where_haiti"), "where_haiti")
                .addOption(Component.translatable("dialog.player.ask_join"), "ask_join")
                .addOption(Component.translatable("dialog.player.back"), "who_haiti"));

        // === ОПАСЕН ЛИ ОН? ===
        addNode("is_dangerous", new DialogNode(
                Component.translatable("dialog.wild_moon_deer.is_dangerous")
        ).addOption(Component.translatable("dialog.player.ask_how_know"), "how_know")
                .addOption(Component.translatable("dialog.player.back"), "if_eat_moon"));

        // === КАК УЗНАТЬ? ===
        addNode("how_know", new DialogNode(
                Component.translatable("dialog.wild_moon_deer.how_know")
        ).addOption(Component.translatable("dialog.player.ask_join"), "ask_join")
                .addOption(Component.translatable("dialog.player.back"), "is_dangerous"));

        // === ГДЕ НАЙТИ ХАТИ? ===
        addNode("where_haiti", new DialogNode(
                Component.translatable("dialog.wild_moon_deer.where_haiti")
        ).addOption(Component.translatable("dialog.player.ask_join"), "ask_join")
                .addOption(Component.translatable("dialog.player.back"), "who_haiti"));

        // === НЕ СКУЧНО ЛИ? ===
        addNode("ask_bored", new DialogNode(
                Component.translatable("dialog.wild_moon_deer.ask_bored")
        ).addOption(Component.translatable("dialog.player.ask_join"), "ask_join")
                .addOption(Component.translatable("dialog.player.back"), "who_are_you"));

        // === ХОЧЕШЬ УЙТИ? ===
        addNode("ask_leave", new DialogNode(
                Component.translatable("dialog.wild_moon_deer.ask_leave")
        ).addOption(Component.translatable("dialog.player.ask_join"), "ask_join")
                .addOption(Component.translatable("dialog.player.back"), "about_place"));

        // === ПРЕДЛОЖЕНИЕ ПОЙТИ С ИГРОКОМ ===
        addNode("ask_join", new DialogNode(
                Component.translatable("dialog.wild_moon_deer.ask_join")
        ).addOption(Component.translatable("dialog.player.understand"), "end")
                .addOption(Component.translatable("dialog.player.ask_how"), "how_to_tame"));

        // === КАК ПРИРУЧИТЬ ===
        addNode("how_to_tame", new DialogNode(
                Component.translatable("dialog.wild_moon_deer.how_to_tame")
        ).addOption(Component.translatable("dialog.player.thanks"), "end"));

        // === КОНЕЦ ДИАЛОГА ===
        addNode("end", new DialogNode(
                Component.translatable("dialog.wild_moon_deer.farewell")
        ));
    }
}
package net.lisalaf.fantastikmod.dialog.mobs;

import net.lisalaf.fantastikmod.dialog.Dialog;
import net.lisalaf.fantastikmod.dialog.DialogNode;
import net.minecraft.network.chat.Component;

public class WildKitsuneDialog extends Dialog {

    @Override
    protected void setupDialog() {
        // Начальный узел
        addNode("start", new DialogNode(
                Component.translatable("dialog.wild_kitsune.greeting"), "start"
        ).addOption(Component.translatable("dialog.player.ask_who_wild"), "who_are_you")
                .addOption(Component.translatable("dialog.player.ask_can_talk"), "can_talk")
                .addOption(Component.translatable("dialog.player.ask_activities_wild"), "activities")
                .addOption(Component.translatable("dialog.player.goodbye"), "end"));

        // Узел "Кто ты?"
        addNode("who_are_you", new DialogNode(
                Component.translatable("dialog.wild_kitsune.who_are_you"), "who_are_you"
        ).addOption(Component.translatable("dialog.player.ask_can_talk"), "can_talk")
                .addOption(Component.translatable("dialog.player.ask_activities_wild"), "activities")
                .addOption(Component.translatable("dialog.player.ask_follow"), "ask_follow")
                .addOption(Component.translatable("dialog.player.back"), "start"));

        // Узел "Ты умеешь говорить?"
        addNode("can_talk", new DialogNode(
                Component.translatable("dialog.wild_kitsune.can_talk"), "can_talk"
        ).addOption(Component.translatable("dialog.player.ask_who_wild"), "who_are_you")
                .addOption(Component.translatable("dialog.player.ask_activities_wild"), "activities")
                .addOption(Component.translatable("dialog.player.ask_follow"), "ask_follow")
                .addOption(Component.translatable("dialog.player.back"), "start"));

        // Узел "Что ты здесь делаешь?"
        addNode("activities", new DialogNode(
                Component.translatable("dialog.wild_kitsune.activities"), "activities"
        ).addOption(Component.translatable("dialog.player.ask_monsters"), "ask_monsters")
                .addOption(Component.translatable("dialog.player.ask_follow"), "ask_follow")
                .addOption(Component.translatable("dialog.player.back"), "start"));

        // Узел "Ты и монстров?"
        addNode("ask_monsters", new DialogNode(
                Component.translatable("dialog.wild_kitsune.ask_monsters"), "ask_monsters"
        ).addOption(Component.translatable("dialog.player.no_thanks"), "test_fire") // "Спасибо... Лучше не надо" ведет на ответ про угрозы
                .addOption(Component.translatable("dialog.player.ask_follow"), "ask_follow") // "Хочешь последовать за мной?" ведет на обычный ответ
                .addOption(Component.translatable("dialog.player.back"), "activities"));

// Узел "Вот так! В следующий раз думай..." (переименован из test_fire)
        addNode("test_fire", new DialogNode(
                Component.translatable("dialog.wild_kitsune.test_fire"), "test_fire"
        ).addOption(Component.translatable("dialog.player.ask_follow"), "ask_follow")
                .addOption(Component.translatable("dialog.player.back"), "start"));

        // Узел "Хочешь последовать за мной?"
        addNode("ask_follow", new DialogNode(
                Component.translatable("dialog.wild_kitsune.ask_follow"), "ask_follow"
        ).addOption(Component.translatable("dialog.player.ask_why_follow"), "why_follow") // ТОЛЬКО этот вариант
                .addOption(Component.translatable("dialog.player.back"), "start"));

        // Узел "Со мной в путешествие"
        addNode("why_follow", new DialogNode(
                Component.translatable("dialog.wild_kitsune.why_follow"), "why_follow"
        ).addOption(Component.translatable("dialog.player.ask_tofu_serious"), "tofu_serious")
                .addOption(Component.translatable("dialog.player.ask_how_much_tofu"), "how_much_tofu")
                .addOption(Component.translatable("dialog.player.back"), "start"));

        // Узел "Тофу? Серьёзно?"
        addNode("tofu_serious", new DialogNode(
                Component.translatable("dialog.wild_kitsune.tofu_serious"), "tofu_serious"
        ).addOption(Component.translatable("dialog.player.ask_how_much_tofu"), "how_much_tofu")
                .addOption(Component.translatable("dialog.player.back"), "start"));

        // Узел "И сколько тебе надо"
        addNode("how_much_tofu", new DialogNode(
                Component.translatable("dialog.wild_kitsune.how_much_tofu"), "how_much_tofu"
        ).addOption(Component.translatable("dialog.player.too_much"), "too_much")
                .addOption(Component.translatable("dialog.player.ok_bring"), "ok_bring")
                .addOption(Component.translatable("dialog.player.back"), "start"));

        // Узел "Не многова-то ли?"
        addNode("too_much", new DialogNode(
                Component.translatable("dialog.wild_kitsune.too_much"), "too_much"
        ).addOption(Component.translatable("dialog.player.ok_bring"), "ok_bring")
                .addOption(Component.translatable("dialog.player.back"), "start"));

        // Узел "Ладно, я принесу"
        addNode("ok_bring", new DialogNode(
                Component.translatable("dialog.wild_kitsune.ok_bring"), "ok_bring"
        ).addOption(Component.translatable("dialog.player.back"), "start"));

        // Завершающий узел
        addNode("end", new DialogNode(
                Component.translatable("dialog.wild_kitsune.farewell"), "end"
        ));
    }
}
package net.lisalaf.fantastikmod.dialog.mobs.kitsune.kitsune1tail;

import net.lisalaf.fantastikmod.dialog.Dialog;
import net.lisalaf.fantastikmod.dialog.DialogNode;
import net.minecraft.network.chat.Component;

public class Kitsune1TailSoulMaskDialog extends Dialog {

    @Override
    protected void setupDialog() {

        addNode("start", new DialogNode(
                Component.translatable("dialog.kitsune.kitsune1tail.soulmask.greeting"), "start"
        ).addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.answer1"), "answer_what_mask")
                .addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.answer2"), "answer_how_know")
                .addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.answer3"), "answer_why_ask"));

        //1 ветка: "Какую маску?"
        addNode("answer_what_mask", new DialogNode(
                Component.translatable("dialog.kitsune.kitsune1tail.soulmask.answer_what_mask"), "answer_what_mask"
        ).addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.what_in_it"), "what_in_it")
                .addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.how_know_2"), "answer_how_know"));

        //2 ветка: "Как ты узнала?"
        addNode("answer_how_know", new DialogNode(
                Component.translatable("dialog.kitsune.kitsune1tail.soulmask.answer_how_know"), "answer_how_know"
        ).addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.amazing"), "amazing")
                .addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.nothing_special"), "nothing_special"));

        //3 ветка: "Почему ты спрашиваешь?"
        addNode("answer_why_ask", new DialogNode(
                Component.translatable("dialog.kitsune.kitsune1tail.soulmask.answer_why_ask"), "answer_why_ask"
        ).addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.what_can_happen"), "what_can_happen")
                .addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.normal_looking"), "normal_looking"));

        //Продолжение 1 ветки
        addNode("what_in_it", new DialogNode(
                Component.translatable("dialog.kitsune.kitsune1tail.soulmask.what_in_it"), "what_in_it"
        ).addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.say_directly"), "say_directly")
                .addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.what_exactly"), "say_directly"));

        //Продолжение 2 ветки
        addNode("amazing", new DialogNode(
                Component.translatable("dialog.kitsune.kitsune1tail.soulmask.amazing"), "amazing"
        ).addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.why_need"), "why_need")
                .addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.take_it"), "give_mask"));

        addNode("nothing_special", new DialogNode(
                Component.translatable("dialog.kitsune.kitsune1tail.soulmask.nothing_special"), "nothing_special"
        ).addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.dont_insult"), "dont_insult")
                .addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.what_does_it_do"), "what_can_happen"));

        //Продолжение 3 ветки
        addNode("what_can_happen", new DialogNode(
                Component.translatable("dialog.kitsune.kitsune1tail.soulmask.what_can_happen"), "what_can_happen"
        ).addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.why_need_2"), "why_need")
                .addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.take_it_2"), "give_mask"));

        addNode("normal_looking", new DialogNode(
                Component.translatable("dialog.kitsune.kitsune1tail.soulmask.normal_looking"), "normal_looking"
        ).addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.witches_cursed"), "witches_cursed")
                .addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.really_check"), "really_check"));

        // Финал 1 ветки
        addNode("say_directly", new DialogNode(
                Component.translatable("dialog.kitsune.kitsune1tail.soulmask.say_directly"), "say_directly"
        ).addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.what_curse"), "curse_details")
                .addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.take_it_3"), "give_mask")
                .addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.keep_it"), "refuse_mask"));

        //Финал 2 ветки
        addNode("why_need", new DialogNode(
                Component.translatable("dialog.kitsune.kitsune1tail.soulmask.why_need"), "why_need"
        ).addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.take_it_4"), "give_mask")
                .addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.keep_it_2"), "refuse_mask"));

        addNode("dont_insult", new DialogNode(
                Component.translatable("dialog.kitsune.kitsune1tail.soulmask.dont_insult"), "dont_insult"
        ).addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.take_it_5"), "give_mask")
                .addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.keep_it_3"), "refuse_mask"));

        // Финал 3 ветки
        addNode("witches_cursed", new DialogNode(
                Component.translatable("dialog.kitsune.kitsune1tail.soulmask.witches_cursed"), "witches_cursed"
        ).addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.why_need_3"), "why_need")
                .addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.take_it_6"), "give_mask"));

        addNode("really_check", new DialogNode(
                Component.translatable("dialog.kitsune.kitsune1tail.soulmask.really_check"), "really_check"
        ).addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.why_need_4"), "why_need")
                .addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.take_it_7"), "give_mask"));

        //Проклятие
        addNode("curse_details", new DialogNode(
                Component.translatable("dialog.kitsune.kitsune1tail.soulmask.curse_details"), "curse_details"
        ).addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.take_it_8"), "give_mask")
                .addOption(Component.translatable("dialog.player.kitsune1tail.soulmask.keep_it_4"), "refuse_mask"));

        addNode("give_mask", new DialogNode(
                Component.translatable("dialog.kitsune.kitsune1tail.soulmask.give_mask"), "give_mask"
        ));

        addNode("refuse_mask", new DialogNode(
                Component.translatable("dialog.kitsune.kitsune1tail.soulmask.refuse_mask"), "refuse_mask"
        ));
    }
}
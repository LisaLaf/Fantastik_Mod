package net.lisalaf.fantastikmod.dialog.mobs;

import net.lisalaf.fantastikmod.dialog.Dialog;
import net.lisalaf.fantastikmod.dialog.DialogNode;
import net.lisalaf.fantastikmod.dialog.DialogOption;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class KitsuneDialog extends Dialog {

    private String currentLegend = "";
    private String currentStory = "";
    private boolean legendFixed = false;
    private boolean storyFixed = false;

    @Override
    protected void setupDialog() {
        // Начальный узел
        addNode("start", new DialogNode(
                Component.translatable("dialog.kitsune.greeting"), "start"
        ).addOption(Component.translatable("dialog.player.ask_missed"), "ask_missed")
                .addOption(Component.translatable("dialog.player.ask_activities"), "ask_activities")
                .addOption(Component.translatable("dialog.player.ask_legend"), "ask_legend")
                .addOption(Component.translatable("dialog.player.ask_personal"), "ask_personal")
                .addOption(Component.translatable("dialog.player.goodbye"), "end"));

        addNode("ask_missed", new DialogNode(
                Component.translatable("dialog.kitsune.ask_missed"), "ask_missed"
        ).addOption(Component.translatable("dialog.player.ask_activities"), "ask_activities")
                .addOption(Component.translatable("dialog.player.ask_legend"), "ask_legend")
                .addOption(Component.translatable("dialog.player.ask_personal"), "ask_personal")
                .addOption(Component.translatable("dialog.player.back"), "start"));

        addNode("ask_activities", new DialogNode(
                Component.translatable("dialog.kitsune.ask_activities"), "ask_activities"
        ).addOption(Component.translatable("dialog.player.ask_missed"), "ask_missed")
                .addOption(Component.translatable("dialog.player.ask_legend"), "ask_legend")
                .addOption(Component.translatable("dialog.player.ask_personal"), "ask_personal")
                .addOption(Component.translatable("dialog.player.back"), "start"));

        // Узел выбора легенды
        addNode("ask_legend", new DialogNode(
                Component.translatable("dialog.kitsune.ask_legend"), "ask_legend"
        ).addOption(Component.translatable("dialog.player.yes_legend"), "show_legend")
                .addOption(Component.translatable("dialog.player.no_legend"), "start")
                .addOption(Component.translatable("dialog.player.back"), "start"));

        // Узел выбора истории
        addNode("ask_personal", new DialogNode(
                Component.translatable("dialog.kitsune.ask_personal"), "ask_personal"
        ).addOption(Component.translatable("dialog.player.yes_personal"), "show_story")
                .addOption(Component.translatable("dialog.player.no_personal"), "start")
                .addOption(Component.translatable("dialog.player.back"), "start"));

        // УЗЕЛ ПОКАЗА ЛЕГЕНДЫ - ФИКСИРУЕМ ВЫБОР
        addNode("show_legend", new DialogNode(Component.empty(), "show_legend") {
            @Override
            public Component getNpcText() {
                if (!legendFixed) {
                    currentLegend = getRandomLegendType();
                    legendFixed = true;
                    System.out.println("ФИКСИРУЕМ легенду: " + currentLegend);
                }
                return Component.translatable("dialog.kitsune." + currentLegend);
            }

            @Override
            public List<DialogOption> getOptions() {
                List<DialogOption> options = new ArrayList<>();

                // ТОЛЬКО вопросы для зафиксированной легенды
                switch (currentLegend) {
                    case "legend1":
                        options.add(new DialogOption(Component.translatable("dialog.player.ask_legend1"), "legend1_response"));
                        break;
                    case "legend2":
                        options.add(new DialogOption(Component.translatable("dialog.player.ask_legend2"), "legend2_response"));
                        break;
                    case "legend3":
                        options.add(new DialogOption(Component.translatable("dialog.player.ask_legend3"), "legend3_response"));
                        break;
                    case "legend4":
                        options.add(new DialogOption(Component.translatable("dialog.player.ask_legend4"), "legend4_response"));
                        break;
                    case "legend5":
                        options.add(new DialogOption(Component.translatable("dialog.player.ask_legend5"), "legend5_response"));
                        break;
                    case "legend6":
                        options.add(new DialogOption(Component.translatable("dialog.player.ask_legend6"), "legend6_response"));
                        break;
                }

                options.add(new DialogOption(Component.translatable("dialog.player.back"), "start"));
                return options;
            }
        });

        // УЗЕЛ ПОКАЗА ИСТОРИИ - ФИКСИРУЕМ ВЫБОР
        addNode("show_story", new DialogNode(Component.empty(), "show_story") {
            @Override
            public Component getNpcText() {
                if (!storyFixed) {
                    currentStory = getRandomStoryType();
                    storyFixed = true;
                    System.out.println("ФИКСИРУЕМ историю: " + currentStory);
                }
                return Component.translatable("dialog.kitsune." + currentStory);
            }

            @Override
            public List<DialogOption> getOptions() {
                List<DialogOption> options = new ArrayList<>();

                // ТОЛЬКО вопросы для зафиксированной истории
                switch (currentStory) {
                    case "personal1":
                        options.add(new DialogOption(Component.translatable("dialog.player.ask_story1"), "story1_response"));
                        break;
                    case "personal2":
                        options.add(new DialogOption(Component.translatable("dialog.player.ask_story2"), "story2_response"));
                        break;
                    case "personal3":
                        options.add(new DialogOption(Component.translatable("dialog.player.ask_story3"), "story3_response"));
                        break;
                    case "personal4":
                        options.add(new DialogOption(Component.translatable("dialog.player.ask_story4"), "story4_response"));
                        break;
                    case "personal5":
                        options.add(new DialogOption(Component.translatable("dialog.player.ask_story5"), "story5_response"));
                        break;
                    case "personal6":
                        options.add(new DialogOption(Component.translatable("dialog.player.ask_story6"), "story6_response"));
                        break;
                }

                options.add(new DialogOption(Component.translatable("dialog.player.back"), "start"));
                return options;
            }
        });

        // ОТВЕТЫ НА ЛЕГЕНДЫ
        addNode("legend1_response", new DialogNode(
                Component.translatable("dialog.kitsune.legend1_response"), "legend1_response"
        ).addOption(Component.translatable("dialog.player.back"), "start"));

        addNode("legend2_response", new DialogNode(
                Component.translatable("dialog.kitsune.legend2_response"), "legend2_response"
        ).addOption(Component.translatable("dialog.player.back"), "start"));

        addNode("legend3_response", new DialogNode(
                Component.translatable("dialog.kitsune.legend3_response"), "legend3_response"
        ).addOption(Component.translatable("dialog.player.back"), "start"));

        addNode("legend4_response", new DialogNode(
                Component.translatable("dialog.kitsune.legend4_response"), "legend4_response"
        ).addOption(Component.translatable("dialog.player.legend4_followup"), "legend4_followup")
                .addOption(Component.translatable("dialog.player.back"), "start"));

        addNode("legend4_followup", new DialogNode(
                Component.translatable("dialog.kitsune.legend4_followup"), "legend4_followup"
        ).addOption(Component.translatable("dialog.player.back"), "start"));

        addNode("legend5_response", new DialogNode(
                Component.translatable("dialog.kitsune.legend5_response"), "legend5_response"
        ).addOption(Component.translatable("dialog.player.back"), "start"));

        addNode("legend6_response", new DialogNode(
                Component.translatable("dialog.kitsune.legend6_response"), "legend6_response"
        ).addOption(Component.translatable("dialog.player.legend6_serious"), "legend6_serious")
                .addOption(Component.translatable("dialog.player.legend6_silent"), "legend6_silent")
                .addOption(Component.translatable("dialog.player.back"), "start"));

        addNode("legend6_serious", new DialogNode(
                Component.translatable("dialog.kitsune.legend6_serious"), "legend6_serious"
        ).addOption(Component.translatable("dialog.player.back"), "start"));

        addNode("legend6_silent", new DialogNode(
                Component.translatable("dialog.kitsune.legend6_silent"), "legend6_silent"
        ).addOption(Component.translatable("dialog.player.back"), "start"));

        // ОТВЕТЫ НА ИСТОРИИ
        addNode("story1_response", new DialogNode(
                Component.translatable("dialog.kitsune.story1_response"), "story1_response"
        ).addOption(Component.translatable("dialog.player.back"), "start"));

        addNode("story2_response", new DialogNode(
                Component.translatable("dialog.kitsune.story2_response"), "story2_response"
        ).addOption(Component.translatable("dialog.player.back"), "start"));

        addNode("story3_response", new DialogNode(
                Component.translatable("dialog.kitsune.story3_response"), "story3_response"
        ).addOption(Component.translatable("dialog.player.back"), "start"));

        addNode("story4_response", new DialogNode(
                Component.translatable("dialog.kitsune.story4_response"), "story4_response"
        ).addOption(Component.translatable("dialog.player.back"), "start"));

        addNode("story5_response", new DialogNode(
                Component.translatable("dialog.kitsune.story5_response"), "story5_response"
        ).addOption(Component.translatable("dialog.player.back"), "start"));

        addNode("story6_response", new DialogNode(
                Component.translatable("dialog.kitsune.story6_response"), "story6_response"
        ).addOption(Component.translatable("dialog.player.back"), "start"));

        addNode("end", new DialogNode(
                Component.translatable("dialog.kitsune.farewell"), "end"
        ));
    }

    private String getRandomLegendType() {
        String[] legendTypes = {"legend1", "legend2", "legend3", "legend4", "legend5", "legend6"};
        int index = this.random.nextInt(legendTypes.length);
        System.out.println("Случайная легенда выбрана: " + legendTypes[index] + " (индекс: " + index + ")");
        return legendTypes[index];
    }

    private String getRandomStoryType() {
        String[] storyTypes = {"personal1", "personal2", "personal3", "personal4", "personal5", "personal6"};
        int index = this.random.nextInt(storyTypes.length);
        System.out.println("Случайная история выбрана: " + storyTypes[index] + " (индекс: " + index + ")");
        return storyTypes[index];
    }

    @Override
    public void reset() {
        super.reset();
        // Сбрасываем при полном перезапуске диалога
        currentLegend = "";
        currentStory = "";
        legendFixed = false;
        storyFixed = false;
        System.out.println("ДИАЛОГ СБРОШЕН - все флаги очищены");
    }

    @Override
    public void selectOption(int optionIndex) {
        System.out.println("Выбрана опция: " + optionIndex + ", текущий узел: " + currentDialogId);
        super.selectOption(optionIndex);
    }
}
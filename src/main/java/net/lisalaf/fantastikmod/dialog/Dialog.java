package net.lisalaf.fantastikmod.dialog;

import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Dialog {
    protected final Map<String, DialogNode> nodes = new HashMap<>();
    protected DialogNode currentNode;
    protected String currentDialogId;
    protected Random random;

    public Dialog() {
        this.random = new Random();
        setupDialog();
        if (currentNode == null && !nodes.isEmpty()) {
            currentNode = nodes.get("start");
            currentDialogId = "start";
        }
    }

    protected void setupDialog() {
    }

    protected void addNode(String id, DialogNode node) {
        nodes.put(id, node);
        if (currentNode == null) {
            currentNode = node;
            currentDialogId = id;
        }
    }

    public DialogNode getCurrentNode() {
        return currentNode;
    }

    public void selectOption(int optionIndex) {
        if (currentNode != null && optionIndex < currentNode.getOptions().size()) {
            DialogOption selectedOption = currentNode.getOptions().get(optionIndex);
            currentDialogId = selectedOption.getNextNodeId();
            currentNode = nodes.get(selectedOption.getNextNodeId());
        }
    }

    public boolean isFinished() {
        return currentNode == null || currentNode.getOptions().isEmpty();
    }

    public void reset() {
        currentNode = nodes.get("start");
        currentDialogId = "start";
    }

    public Component getRandomStory() {
        String[] storyKeys = {
                "dialog.kitsune.story1", "dialog.kitsune.story2", "dialog.kitsune.story3",
                "dialog.kitsune.story4", "dialog.kitsune.story5", "dialog.kitsune.story6"
        };
        // Уберите объявление локальной переменной, используйте поле класса
        String randomKey = storyKeys[this.random.nextInt(storyKeys.length)];
        return Component.translatable(randomKey);
    }

    public boolean canStart() {
        return nodes.containsKey("start") && nodes.get("start") != null;
    }
}
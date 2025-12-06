package net.lisalaf.fantastikmod.dialog;

import net.minecraft.network.chat.Component;

public class DialogOption {
    private final Component text;      // Текст варианта (что видит игрок)
    private final String nextNodeId;   // ID следующего узла диалога
    private final boolean available;   // Доступен ли этот вариант

    public DialogOption(Component text, String nextNodeId) {
        this.text = text;
        this.nextNodeId = nextNodeId;
        this.available = true;
    }

    public DialogOption(Component text, String nextNodeId, boolean available) {
        this.text = text;
        this.nextNodeId = nextNodeId;
        this.available = available;
    }

    // Геттеры
    public Component getText() { return text; }
    public String getNextNodeId() { return nextNodeId; }
    public boolean isAvailable() { return available; }
}
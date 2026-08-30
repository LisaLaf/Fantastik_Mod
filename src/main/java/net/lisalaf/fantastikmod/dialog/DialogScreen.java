package net.lisalaf.fantastikmod.dialog;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import net.lisalaf.fantastikmod.dialog.components.MoodTabButton;
import net.lisalaf.fantastikmod.dialog.mood.MobMood;
import net.lisalaf.fantastikmod.dialog.mood.MoodSystem;
import net.lisalaf.fantastikmod.dialog.quest.Quest;
import net.lisalaf.fantastikmod.dialog.quest.QuestSystem;
import net.lisalaf.fantastikmod.dialog.quest.QuestTabButton;
import net.lisalaf.fantastikmod.entity.custom.KitsuneLightEntity;
import net.lisalaf.fantastikmod.entity.custom.MoonDeerEntity;
import net.lisalaf.fantastikmod.fantastikmod;
import net.lisalaf.fantastikmod.network.QuestPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.lisalaf.fantastikmod.dialog.quest.ClientQuestData;

@SuppressWarnings("removal")
public class DialogScreen extends Screen {

    private static final ResourceLocation DEFAULT_DIALOG_BG = new ResourceLocation("fantastikmod", "textures/gui/dialog.png");
    private static final ResourceLocation KITSUNE_DIALOG_BG = new ResourceLocation("fantastikmod", "textures/gui/kitsune_dialog.png");

    private static final int CONTENT_WIDTH = 350;
    private static final int CONTENT_HEIGHT = 200;

    private final Entity entity;
    private final Dialog dialog;
    private boolean questCreationChecked = false;

    private DialogButton acceptQuestButton;
    private DialogButton declineQuestButton;
    private DialogButton completeQuestButton;

    private int contentX, contentY;

    private MoodTabButton[] tabButtons;
    private int currentTab = 0;
    private MoodSystem moodSystem;

    private DialogButton[] optionButtons;
    private int selectedButtonIndex = -1;

    private QuestTabButton questTabButton;
    private DialogButton cancelQuestButton;

    public DialogScreen(Entity entity, Dialog dialog) {
        super(Component.empty());
        this.entity = entity;
        this.dialog = dialog;
        if (entity instanceof MobMood mobMood) {
            this.moodSystem = mobMood.getMoodSystem();
        }
    }

    @Override
    protected void init() {
        super.init();

        this.contentX = (this.width - CONTENT_WIDTH) / 2;
        this.contentY = (this.height - CONTENT_HEIGHT) / 2;

        int tabWidth = 28;
        int tabHeight = 30;
        int tabsX = contentX + CONTENT_WIDTH - tabWidth + 22;
        int tabsY = contentY + 16;

        tabButtons = new MoodTabButton[2];
        tabButtons[0] = new MoodTabButton(tabsX, tabsY, tabWidth, tabHeight, "💬", 0, currentTab == 0, button -> {
            currentTab = 0;
            updateTabs();
            resetQuestCreationCheck();
            resetQuestButtons();
        });

        questTabButton = new QuestTabButton(tabsX, tabsY + tabHeight + 5, tabWidth, tabHeight, 2, currentTab == 2, button -> {
            currentTab = 2;
            updateTabs();
            resetQuestButtons();
        });

        addRenderableWidget(questTabButton);
        for (int i = 0; i < 2; i++) {
            if (tabButtons[i] != null) addRenderableWidget(tabButtons[i]);
        }

        updateOptionButtons();
        updateTabs();

        resetQuestButtons();
        questCreationChecked = false;
    }

    private void updateTabs() {
        for (int i = 0; i < 2; i++) if (tabButtons[i] != null) tabButtons[i].setActive(currentTab == i);
        if (questTabButton != null) questTabButton.setActive(currentTab == 2);
    }

    private void updateOptionButtons() {
        if (optionButtons != null) for (DialogButton b : optionButtons) if (b != null) removeWidget(b);

        DialogNode node = dialog.getCurrentNode();
        if (node != null && !node.getOptions().isEmpty()) {
            int n = node.getOptions().size();
            optionButtons = new DialogButton[n];
            int margin = 6, bh = 28, maxW = CONTENT_WIDTH / 2 - 40;
            if (n > 3) { bh = 24; maxW = CONTENT_WIDTH / 2 - 30; }
            int sy = contentY + 50, maxH = CONTENT_HEIGHT - 80, perH = maxH / n;
            if (perH < bh + margin) bh = Math.max(20, perH - margin);

            for (int i = 0; i < n; i++) {
                DialogOption opt = node.getOptions().get(i);
                int bx = contentX + 20, by = sy + i * (bh + margin), fi = i;
                optionButtons[i] = new DialogButton(bx, by, maxW, bh, opt.getText(), i, b -> {
                    selectedButtonIndex = fi;
                    dialog.selectOption(fi);
                    if (dialog.isFinished()) minecraft.setScreen(null);
                    else { selectedButtonIndex = -1; updateOptionButtons(); }
                });
                addRenderableWidget(optionButtons[i]);
            }
        } else optionButtons = new DialogButton[0];
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g);
        renderFullScreenBackground(g);
        if (currentTab == 0) {
            renderNpcText(g); renderEntityName(g); renderEntityModel(g, pt);
            renderDialogButtons(g, mx, my, pt); renderTabButtons(g, mx, my, pt);
        } else if (currentTab == 2) renderQuestScreen(g);

        if (optionButtons != null) for (DialogButton b : optionButtons) if (b != null) b.visible = (currentTab == 0);
        if (acceptQuestButton != null) acceptQuestButton.visible = (currentTab == 2);
        if (declineQuestButton != null) declineQuestButton.visible = (currentTab == 2);
        if (completeQuestButton != null) completeQuestButton.visible = (currentTab == 2);
        if (cancelQuestButton != null) cancelQuestButton.visible = (currentTab == 2);
        super.render(g, mx, my, pt);
    }

    private void renderFullScreenBackground(GuiGraphics g) {
        ResourceLocation bg = getDialogBackground();
        g.blit(bg, 0, 0, 0, 0, this.width, this.height, this.width, this.height);
    }

    private ResourceLocation getDialogBackground() {
        return entity instanceof KitsuneLightEntity ? KITSUNE_DIALOG_BG : DEFAULT_DIALOG_BG;
    }

    private void renderNpcText(GuiGraphics g) {
        DialogNode node = dialog.getCurrentNode();
        if (node == null) return;
        int tx = contentX + CONTENT_WIDTH / 2 + 10, ty = contentY + 40, tw = CONTENT_WIDTH / 2 - 30;
        var lines = font.split(node.getNpcText(), tw);
        int lh = font.lineHeight + 2, th = lines.size() * lh;
        g.fill(tx - 4, ty - 2, tx + tw + 4, ty + th + 2, 0x99000000);
        for (int i = 0; i < lines.size(); i++) g.drawString(font, lines.get(i), tx, ty + i * lh, 0xFFFFFF, false);
    }

    private void renderEntityName(GuiGraphics g) {
        Component name = entity.getDisplayName();
        int nw = font.width(name), nx = contentX + (CONTENT_WIDTH - nw) / 2, ny = contentY + 10;
        g.fill(nx - 4, ny - 2, nx + nw + 4, ny + 10, 0x99000000);
        g.drawString(font, name, nx, ny, 0xFFFFFF, true);
    }

    private void renderEntityModel(GuiGraphics g, float pt) {
        int mx = contentX + CONTENT_WIDTH - 140, my = contentY + CONTENT_HEIGHT - 100;
        try {
            PoseStack ps = g.pose(); ps.pushPose();
            ps.translate(mx + 70, my + 100, 150); ps.scale(40, 40, 40);
            ps.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(180));
            ps.mulPose(com.mojang.math.Axis.YP.rotationDegrees(210));
            ps.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-10));
            Lighting.setupForEntityInInventory();
            EntityRenderDispatcher d = Minecraft.getInstance().getEntityRenderDispatcher();
            MultiBufferSource.BufferSource bs = Minecraft.getInstance().renderBuffers().bufferSource();
            d.render(entity, 0, 0, 0, 0, pt, ps, bs, LightTexture.FULL_BRIGHT);
            bs.endBatch(); Lighting.setupFor3DItems(); ps.popPose();
        } catch (Exception ignored) {}
    }

    private void renderDialogButtons(GuiGraphics g, int mx, int my, float pt) {
        if (optionButtons != null) for (DialogButton b : optionButtons) if (b != null && b.visible) b.render(g, mx, my, pt);
    }

    private void renderTabButtons(GuiGraphics g, int mx, int my, float pt) {
        for (int i = 0; i < 2; i++) if (tabButtons[i] != null) tabButtons[i].render(g, mx, my, pt);
        if (questTabButton != null) questTabButton.render(g, mx, my, pt);
    }

    private void renderQuestScreen(GuiGraphics g) {
        int mx = (int)(minecraft.mouseHandler.xpos() * minecraft.getWindow().getGuiScaledWidth() / minecraft.getWindow().getScreenWidth());
        int my = (int)(minecraft.mouseHandler.ypos() * minecraft.getWindow().getGuiScaledHeight() / minecraft.getWindow().getScreenHeight());
        ClientQuestData.ClientQuestInfo questInfo = ClientQuestData.getQuestData(entity.getUUID());

        g.fill(contentX, contentY, contentX + CONTENT_WIDTH, contentY + CONTENT_HEIGHT, 0xB3000000);
        g.drawCenteredString(font, Component.translatable("gui.quest.title"), contentX + CONTENT_WIDTH / 2, contentY + 15, 0xFFFFFF);

        if (questInfo == null) {
            g.drawCenteredString(font, Component.translatable("gui.quest.none"), contentX + CONTENT_WIDTH / 2, contentY + 60, 0xAAAAAA);

            if (acceptQuestButton == null) {
                int cx = contentX + CONTENT_WIDTH / 2;
                acceptQuestButton = new DialogButton(cx - 45, contentY + 100, 90, 24,
                        Component.translatable("gui.quest.request"), 0, b -> {
                    fantastikmod.sendToServer(new QuestPacket(entity.getId(), 0));
                    resetQuestButtons();
                    questCreationChecked = false;
                });
                addRenderableWidget(acceptQuestButton);
            }
        } else if (!questInfo.isActive) {
            renderQuestInfo(g, questInfo);

            if (acceptQuestButton == null) {
                int cx = contentX + CONTENT_WIDTH / 2;
                acceptQuestButton = new DialogButton(cx - 100, contentY + 155, 90, 24,
                        Component.translatable("gui.quest.accept"), 0, b -> {
                    fantastikmod.sendToServer(new QuestPacket(entity.getId(), 0));
                    resetQuestButtons();
                });
                addRenderableWidget(acceptQuestButton);

                declineQuestButton = new DialogButton(cx + 10, contentY + 155, 90, 24,
                        Component.translatable("gui.quest.decline"), 1, b -> {
                    fantastikmod.sendToServer(new QuestPacket(entity.getId(), 2));
                    ClientQuestData.clearData();
                    resetQuestButtons();
                });
                addRenderableWidget(declineQuestButton);
            }
        } else {
            renderQuestInfo(g, questInfo);
            boolean canComplete = minecraft.player != null &&
                    minecraft.player.getInventory().countItem(questInfo.requiredItem) >= questInfo.requiredAmount;

            if (canComplete) {
                if (completeQuestButton == null) {
                    int cx = contentX + CONTENT_WIDTH / 2;
                    completeQuestButton = new DialogButton(cx - 45, contentY + 155, 90, 24,
                            Component.translatable("gui.quest.complete"), 2, b -> {
                        fantastikmod.sendToServer(new QuestPacket(entity.getId(), 1));
                        ClientQuestData.clearData();
                        resetQuestButtons();
                    });
                    addRenderableWidget(completeQuestButton);
                }
            } else {
                if (cancelQuestButton == null) {
                    int cx = contentX + CONTENT_WIDTH / 2;
                    long tl = questInfo.timeLeft;
                    Component cancelText = Component.translatable("gui.quest.cancel");
                    if (tl > 0) {
                        long m = tl / 60000, s = (tl % 60000) / 1000;
                        cancelText = cancelText.copy().append(" (" + String.format("%d:%02d", m, s) + ")");
                    }
                    cancelQuestButton = new DialogButton(cx - 45, contentY + 155, 90, 24, cancelText, 3, b -> {
                        fantastikmod.sendToServer(new QuestPacket(entity.getId(), 2));
                        ClientQuestData.clearData();
                        resetQuestButtons();
                    });
                    addRenderableWidget(cancelQuestButton);
                }
            }
        }
    }
    private void renderQuestInfo(GuiGraphics g, ClientQuestData.ClientQuestInfo questInfo) {
        int cx = contentX + CONTENT_WIDTH / 2;
        g.drawCenteredString(font, Component.literal(questInfo.nameKey), cx, contentY + 30, 0xFFFFFF);
        var descLines = font.split(Component.literal(questInfo.descKey), CONTENT_WIDTH - 40);
        for (int i = 0; i < Math.min(descLines.size(), 2); i++) {
            g.drawCenteredString(font, descLines.get(i), cx, contentY + 45 + i * 12, 0xAAAAAA);
        }

        g.fill(contentX + 30, contentY + 75, contentX + CONTENT_WIDTH - 30, contentY + 76, 0x55FFFFFF);
        int lx = contentX + 60, rx = contentX + CONTENT_WIDTH - 80, iy = contentY + 90;
        g.drawString(font, Component.translatable("gui.quest.requires"), lx - 40, iy - 15, 0xFFFFFF);
        g.drawString(font, Component.translatable("gui.quest.reward"), rx - 30, iy - 15, 0xFFFFFF);

        if (questInfo.requiredItem != null) {
            renderItemInFrame(g, questInfo.requiredItem, questInfo.requiredAmount, lx, iy, false);
        }
        if (questInfo.rewardItem != null) {
            renderItemInFrame(g, questInfo.rewardItem, questInfo.rewardAmount, rx, iy, true);
        }
        if (questInfo.isActive && minecraft.player != null) {
            int cc = minecraft.player.getInventory().countItem(questInfo.requiredItem);
            int rq = questInfo.requiredAmount;
            int tc = cc >= rq ? 0xFF00FF00 : 0xFFFFFF;
            g.drawCenteredString(font, Component.literal("Progress: " + cc + "/" + rq), cx, contentY + 135, tc);
        }
    }

    private void resetQuestCreationCheck() { questCreationChecked = false; }

    private void renderQuestButtons(GuiGraphics g, int mx, int my, Quest quest) {
        int cx = contentX + CONTENT_WIDTH / 2, by = contentY + 160;
        if (quest != null && !quest.isActive()) {
            if (acceptQuestButton == null) {
                acceptQuestButton = new DialogButton(cx - 100, by, 90, 24, Component.translatable("gui.quest.accept"), 0, b -> {
                    fantastikmod.sendToServer(new QuestPacket(entity.getId(), 0));
                    System.out.println("CLIENT: Sent accept quest packet for entity " + entity.getId());
                    resetQuestButtons();
                });
                addRenderableWidget(acceptQuestButton);
            }
            if (declineQuestButton == null) {
                declineQuestButton = new DialogButton(cx + 10, by, 90, 24, Component.translatable("gui.quest.decline"), 1, b -> {
                    fantastikmod.sendToServer(new QuestPacket(entity.getId(), 2));
                    System.out.println("CLIENT: Sent decline quest packet for entity " + entity.getId());
                    resetQuestButtons();
                    resetQuestCreationCheck();
                });
                addRenderableWidget(declineQuestButton);
            }
        }
    }

    private void renderCompleteButton(GuiGraphics g, int mx, int my, Quest quest) {
        int cx = contentX + CONTENT_WIDTH / 2, by = contentY + 180;
        if (quest != null && quest.isActive() && quest.canComplete() && minecraft.player != null && minecraft.player.getInventory().countItem(quest.getRequiredItem()) >= quest.getRequiredAmount()) {
            if (completeQuestButton == null) {
                completeQuestButton = new DialogButton(cx - 45, by, 90, 24, Component.translatable("gui.quest.complete"), 2, b -> {
                    fantastikmod.sendToServer(new QuestPacket(entity.getId(), 1));
                    resetQuestButtons();
                });
                addRenderableWidget(completeQuestButton);
            }
        }
    }

    private void renderActiveQuestInfo(GuiGraphics g, int mx, int my, Quest quest) {
        int cx = contentX + CONTENT_WIDTH / 2, by = contentY + 180;
        if (quest != null && quest.isActive()) {
            long tl = quest.getTimeLeft();
            if (cancelQuestButton == null) {
                cancelQuestButton = new DialogButton(cx - 45, by, 90, 24, Component.translatable("gui.quest.cancel"), 3, b -> {
                    fantastikmod.sendToServer(new QuestPacket(entity.getId(), 2));
                    resetQuestButtons();
                    resetQuestCreationCheck();
                });
                addRenderableWidget(cancelQuestButton);
            }
            if (tl > 0) { long m = tl / 60000, s = (tl % 60000) / 1000; cancelQuestButton.setMessage(Component.translatable("gui.quest.cancel").copy().append(" (" + String.format("%d:%02d", m, s) + ")")); }
            else cancelQuestButton.setMessage(Component.translatable("gui.quest.cancel"));
            if (cancelQuestButton != null) cancelQuestButton.render(g, mx, my, 0);
        }
    }

    private void renderActiveQuest(GuiGraphics g, Quest quest) {
        int cx = contentX + CONTENT_WIDTH / 2;
        g.drawCenteredString(font, quest.getName(), cx, contentY + 30, 0xFFFFFF);
        var dl = font.split(quest.getDescription(), CONTENT_WIDTH - 40);
        for (int i = 0; i < Math.min(dl.size(), 2); i++) g.drawCenteredString(font, dl.get(i), cx, contentY + 50 + i * 10, 0xAAAAAA);
        g.fill(contentX + 30, contentY + 90, contentX + CONTENT_WIDTH - 30, contentY + 91, 0x55FFFFFF);
        int lx = contentX + 50, rx = contentX + CONTENT_WIDTH - 80, iy = contentY + 110;
        g.drawString(font, Component.translatable("gui.quest.requires"), lx - 40, iy - 15, 0xFFFFFF);
        g.drawString(font, Component.translatable("gui.quest.reward"), rx - 30, iy - 15, 0xFFFFFF);
        renderItemInFrame(g, quest.getRequiredItem(), quest.getRequiredAmount(), lx, iy, false);
        renderItemInFrame(g, quest.getRewardItem(), quest.getRewardAmount(), rx, iy, true);
        if (quest.isActive()) {
            int cc = quest.getCurrentCount(), rq = quest.getRequiredAmount(), tc = cc >= rq ? 0xFF00FF00 : 0xFFFFFF;
            g.drawCenteredString(font, Component.translatable("gui.quest.collected", cc + "/" + rq), cx, contentY + 150, tc);
        }
    }

    private void resetQuestButtons() {
        if (acceptQuestButton != null) { removeWidget(acceptQuestButton); acceptQuestButton = null; }
        if (declineQuestButton != null) { removeWidget(declineQuestButton); declineQuestButton = null; }
        if (completeQuestButton != null) { removeWidget(completeQuestButton); completeQuestButton = null; }
        if (cancelQuestButton != null) { removeWidget(cancelQuestButton); cancelQuestButton = null; }
    }

    private void renderItemInFrame(GuiGraphics g, Item item, int count, int x, int y, boolean isReward) {
        int fs = 40, fx = x - fs/2, fy = y - 5;
        int fc = isReward ? 0xFFD4A017 : 0xFF780D13, ic = 0xFF2A1B0E, ac = isReward ? 0xFFFFD700 : 0xFFA0151C;
        g.fill(fx, fy, fx + fs, fy + fs, fc); g.fill(fx + 2, fy + 2, fx + fs - 2, fy + fs - 2, ic);
        for (int i = 0; i < 4; i++) {
            g.fill(fx + i, fy, fx + i + 1, fy + 1, ac); g.fill(fx, fy + i, fx + 1, fy + i + 1, ac);
            g.fill(fx + fs - 1 - i, fy, fx + fs - i, fy + 1, ac); g.fill(fx + fs - 1, fy + i, fx + fs, fy + i + 1, ac);
            g.fill(fx + i, fy + fs - 1, fx + i + 1, fy + fs, ac); g.fill(fx, fy + fs - 1 - i, fx + 1, fy + fs - i, ac);
            g.fill(fx + fs - 1 - i, fy + fs - 1, fx + fs - i, fy + fs, ac); g.fill(fx + fs - 1, fy + fs - 1 - i, fx + fs, fy + fs - i, ac);
        }
        try {
            ItemStack is = new ItemStack(item, count); PoseStack ps = g.pose(); ps.pushPose();
            ps.translate(fx + fs/2, fy + fs/2, 0); ps.scale(1.5f, 1.5f, 1.5f); g.renderItem(is, -8, -8); ps.popPose();
            if (count > 1) { String ct = "x" + count; int tw = font.width(ct), tx = fx + fs - tw - 2, ty = fy + fs - 9; g.fill(tx - 1, ty - 1, tx + tw + 1, ty + 9, 0x80000000); g.drawString(font, ct, tx, ty, 0xFFFFFF, false); }
        } catch (Exception e) { g.drawCenteredString(font, Component.literal("?"), fx + fs/2, fy + fs/2 - 4, 0xFFFFFF); }
        String in = item.getDescription().getString(); if (in.length() > 12) in = in.substring(0, 10) + "...";
        g.drawCenteredString(font, Component.literal(in), fx + fs/2, fy + fs + 5, 0xAAAAAA);
    }

    @Override public boolean keyPressed(int k, int s, int m) { if (k == 69) { minecraft.setScreen(null); return true; } return super.keyPressed(k, s, m); }
    @Override public boolean isPauseScreen() { return false; }
    @Override public void onClose() { super.onClose(); cleanup(); }
    @Override public void removed() { super.removed(); cleanup(); }

    private void cleanup() { if (dialog != null) dialog.reset(); selectedButtonIndex = -1; resetQuestButtons(); resetQuestCreationCheck(); questCreationChecked = false; }

    private class DialogButton extends Button {
        private final int buttonIndex;
        public DialogButton(int x, int y, int w, int h, Component m, int i, OnPress p) { super(x, y, w, h, m, p, DEFAULT_NARRATION); this.buttonIndex = i; }

        @Override
        public void renderWidget(GuiGraphics g, int mx, int my, float pt) {
            boolean sel = buttonIndex == selectedButtonIndex, hov = isHovered();
            int bg = 0xFF220000, bd = sel ? 0xFFF57A16 : 0xFF780D13, ga = sel ? 0xFFFFC940 : 0xFFE04C02, tc = sel ? 0xFFFFF98A : 0xFFFFFF;
            g.fill(getX(), getY(), getX() + width, getY() + height, bg);
            g.fill(getX(), getY(), getX() + width, getY() + 1, bd); g.fill(getX(), getY() + height - 1, getX() + width, getY() + height, bd);
            g.fill(getX(), getY(), getX() + 1, getY() + height, bd); g.fill(getX() + width - 1, getY(), getX() + width, getY() + height, bd);
            if (sel || hov) {
                int io = 2;
                for (int x = getX() + io; x < getX() + width - io; x++) g.fill(x, getY() + io, x + 1, getY() + io + 1, calcGrad((float)(x - getX() - io) / (width - 2*io)));
                for (int x = getX() + io; x < getX() + width - io; x++) g.fill(x, getY() + height - io - 1, x + 1, getY() + height - io, calcGrad((float)(x - getX() - io) / (width - 2*io)));
                for (int y = getY() + io; y < getY() + height - io; y++) g.fill(getX() + io, y, getX() + io + 1, y + 1, calcGrad((float)(y - getY() - io) / (height - 2*io)));
                for (int y = getY() + io; y < getY() + height - io; y++) g.fill(getX() + width - io - 1, y, getX() + width - io, y + 1, calcGrad((float)(y - getY() - io) / (height - 2*io)));
            }
            for (int i = 0; i < 3; i++) {
                g.fill(getX() + i, getY(), getX() + i + 1, getY() + 1, ga); g.fill(getX(), getY() + i, getX() + 1, getY() + i + 1, ga);
                g.fill(getX() + width - 1 - i, getY(), getX() + width - i, getY() + 1, ga); g.fill(getX() + width - 1, getY() + i, getX() + width, getY() + i + 1, ga);
                g.fill(getX() + i, getY() + height - 1, getX() + i + 1, getY() + height, ga); g.fill(getX(), getY() + height - 1 - i, getX() + 1, getY() + height - i, ga);
                g.fill(getX() + width - 1 - i, getY() + height - 1, getX() + width - i, getY() + height, ga); g.fill(getX() + width - 1, getY() + height - 1 - i, getX() + width, getY() + height - i, ga);
            }
            if (sel) {
                g.fill(getX() + 1, getY() + 1, getX() + 2, getY() + 2, 0xFFFFF98A); g.fill(getX() + width - 2, getY() + 1, getX() + width - 1, getY() + 2, 0xFFFFF98A);
                g.fill(getX() + 1, getY() + height - 2, getX() + 2, getY() + height - 1, 0xFFFFF98A); g.fill(getX() + width - 2, getY() + height - 2, getX() + width - 1, getY() + height - 1, 0xFFFFF98A);
            }
            int tw = font.width(getMessage()), aw = width - 10;
            if (tw <= aw) g.drawCenteredString(font, getMessage(), getX() + width/2, getY() + (height - 8)/2, tc);
            else {
                var ls = font.split(getMessage(), aw); int lh = font.lineHeight, th = ls.size() * (lh + 1), sy = getY() + (height - th)/2;
                for (int i = 0; i < ls.size(); i++) g.drawCenteredString(font, ls.get(i), getX() + width/2, sy + i*(lh+1), tc);
            }
        }

        private int calcGrad(float p) {
            if (p < 0.25f) { float l = p/0.25f; return 0xFF000000 | ((int)(0xE0*(1-l) + 0xF5*l))<<16 | ((int)(0x4C*(1-l) + 0x7A*l))<<8 | (int)(0x02*(1-l) + 0x16*l); }
            else if (p < 0.5f) { float l = (p-0.25f)/0.25f; return 0xFF000000 | ((int)(0xF5*(1-l) + 0xFF*l))<<16 | ((int)(0x7A*(1-l) + 0xC9*l))<<8 | (int)(0x16*(1-l) + 0x40*l); }
            else if (p < 0.75f) { float l = (p-0.5f)/0.25f; return 0xFF000000 | ((int)(0xFF*(1-l) + 0xFF*l))<<16 | ((int)(0xC9*(1-l) + 0xF9*l))<<8 | (int)(0x40*(1-l) + 0x8A*l); }
            else { float l = (p-0.75f)/0.25f; return 0xFF000000 | ((int)(0xFF*(1-l) + 0xE0*l))<<16 | ((int)(0xF9*(1-l) + 0x4C*l))<<8 | (int)(0x8A*(1-l) + 0x02*l); }
        }
    }
}
package net.lisalaf.fantastikmod.dialog.quest;

import net.lisalaf.fantastikmod.entity.custom.KitsuneLightEntity;
import net.lisalaf.fantastikmod.item.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import java.util.List;

public class KitsuneQuest extends Quest {
    private final KitsuneLightEntity kitsune;
    private final boolean isForTamed;
    private final QuestType questType;

    // private String targetMob;
    // private int killCount = 0;

    public enum QuestType {
        COLLECT_ITEMS
        // KILL_MOBS
    }

    public KitsuneQuest(KitsuneLightEntity kitsune, boolean isForTamed) {
        this(kitsune, isForTamed, QuestType.COLLECT_ITEMS);
    }

    public KitsuneQuest(KitsuneLightEntity kitsune, boolean isForTamed, QuestType questType) {
        super(
                "kitsune_quest_" + System.currentTimeMillis(),
                getRandomNameKey(isForTamed, questType),
                getRandomDescriptionKey(isForTamed, questType),
                getRandomRequiredItem(isForTamed, questType),
                getRandomRequiredAmount(isForTamed, questType),
                getRandomRewardItem(isForTamed, questType),
                getRandomRewardAmount(isForTamed, questType),
                getRandomDuration(isForTamed, questType)
        );
        this.kitsune = kitsune;
        this.isForTamed = isForTamed;
        this.questType = questType;
    }

    @Override
    protected void updateCustomProgress(Player player, Level level) {
    }

    @Override
    public boolean canStart() {
        if (!isForTamed && kitsune.isAngry()) {
            return false;
        }

        return !isActive && !isCompleted;
    }

    @Override
    public void onStart() {
        Player owner = (Player) kitsune.getOwner();
        if (owner != null) {
            owner.sendSystemMessage(Component.translatable(
                    isForTamed ? "quest.kitsune.tamed.start" : "quest.kitsune.wild.start"
            ));
        }
    }

    @Override
    public void onComplete() {
        Player playerToReward = getPlayerToReward();

        if (playerToReward != null) {
            int itemsToRemove = requiredAmount;
            for (int i = 0; i < playerToReward.getInventory().getContainerSize(); i++) {
                ItemStack stack = playerToReward.getInventory().getItem(i);
                if (stack.getItem() == requiredItem) {
                    int removeCount = Math.min(stack.getCount(), itemsToRemove);
                    stack.shrink(removeCount);
                    itemsToRemove -= removeCount;

                    if (stack.isEmpty()) {
                        playerToReward.getInventory().setItem(i, ItemStack.EMPTY);
                    }

                    if (itemsToRemove <= 0) break;
                }
            }

            ItemStack rewardStack = new ItemStack(rewardItem, rewardAmount);
            if (playerToReward.getInventory().add(rewardStack)) {
                playerToReward.sendSystemMessage(Component.translatable(
                        isForTamed ? "quest.kitsune.tamed.complete" : "quest.kitsune.wild.complete"
                ));
            } else {
                playerToReward.drop(rewardStack, false);
                playerToReward.sendSystemMessage(Component.translatable(
                        isForTamed ? "quest.kitsune.tamed.complete" : "quest.kitsune.wild.complete"
                ));
            }
        }
    }

    @Override
    public void onFail() {
        Player owner = (Player) kitsune.getOwner();
        if (owner != null) {
            owner.sendSystemMessage(Component.translatable("quest.kitsune.failed"));
        }
    }

    @Override
    public boolean canComplete() {
        if (!isActive()) return false;

        Player playerToCheck = getPlayerToCheck();
        if (playerToCheck == null) return false;

        return playerToCheck.getInventory().countItem(requiredItem) >= requiredAmount;
    }

    @Override
    public int getCurrentCount() {
        Player playerToCheck = getPlayerToCheck();
        return playerToCheck != null ?
                playerToCheck.getInventory().countItem(requiredItem) : 0;
    }

    // Геттеры
    public QuestType getQuestType() { return questType; }
    // public String getTargetMob() { return targetMob; }
    // public int getKillCount() { return killCount; }
    // public void incrementKillCount() { killCount++; }

    private Player getPlayerToCheck() {
        if (isForTamed) {
            return (Player) kitsune.getOwner();
        } else {
            List<Player> players = kitsune.level().getEntitiesOfClass(Player.class,
                    kitsune.getBoundingBox().inflate(10.0));
            return !players.isEmpty() ? players.get(0) : null;
        }
    }

    private Player getPlayerToReward() {
        return getPlayerToCheck();
    }

    private static String getRandomNameKey(boolean forTamed, QuestType questType) {
        String[] names = forTamed ?
                new String[]{"quest.kitsune.tamed.name1", "quest.kitsune.tamed.name2"} :
                new String[]{"quest.kitsune.wild.name1", "quest.kitsune.wild.name2"};
        return names[(int)(Math.random() * names.length)];
    }

    private static String getRandomDescriptionKey(boolean forTamed, QuestType questType) {
        return forTamed ? "quest.kitsune.tamed.desc" : "quest.kitsune.wild.desc";
    }

    private static Item getRandomRequiredItem(boolean forTamed, QuestType questType) {
        if (forTamed) {
            Item[] tamedItems = {
                    ModItems.TOFU.get(), Items.CHICKEN, Items.SWEET_BERRIES,
                    Items.RABBIT, Items.BEEF, Items.PORKCHOP, Items.MUTTON,
                    Items.WHEAT, Items.CARROT, Items.POTATO, Items.BEETROOT
            };
            return tamedItems[(int)(Math.random() * tamedItems.length)];
        }

        Item[] wildItems = {
                ModItems.TOFU.get(), Items.SALMON, Items.GLOW_BERRIES,
                Items.CHICKEN, Items.RABBIT, Items.BEEF, Items.PORKCHOP,
                Items.COD, Items.APPLE, Items.MELON_SLICE, Items.PUMPKIN_PIE
        };
        return wildItems[(int)(Math.random() * wildItems.length)];
    }

    private static int getRandomRequiredAmount(boolean forTamed, QuestType questType) {
        if (forTamed) {
            return 1 + (int)(Math.random() * 3);
        } else {
            return 2 + (int)(Math.random() * 3);
        }
    }

    private static Item getRandomRewardItem(boolean forTamed, QuestType questType) {
        if (forTamed) {

            Item[] tamedRewards = {
                    Items.BONE,
                    Items.ROTTEN_FLESH,
                    Items.STRING,
                    Items.GUNPOWDER,
                    Items.SPIDER_EYE,
                    Items.ARROW,
                    Items.IRON_INGOT,
                    Items.GOLD_NUGGET,
                    Items.COAL,
                    Items.REDSTONE,
                    Items.ENDER_EYE
            };
            return tamedRewards[(int)(Math.random() * tamedRewards.length)];
        }

        Item[] wildRewards = {
                Items.IRON_INGOT,
                Items.GOLD_NUGGET,
                Items.EXPERIENCE_BOTTLE,
                Items.COAL,
                Items.REDSTONE,
                Items.LAPIS_LAZULI,
                Items.COPPER_INGOT,
                Items.EMERALD
        };
        return wildRewards[(int)(Math.random() * wildRewards.length)];
    }

    private static int getRandomRewardAmount(boolean forTamed, QuestType questType) {
        Item item = getRandomRewardItem(forTamed, questType);

        if (forTamed) {
            if (item == Items.BONE || item == Items.ROTTEN_FLESH || item == Items.STRING) {
                return 1 + (int)(Math.random() * 3);
            }
            if (item == Items.ARROW) {
                return 3 + (int)(Math.random() * 6);
            }
            if (item == Items.GUNPOWDER || item == Items.SPIDER_EYE ||
                    item == Items.GOLD_NUGGET || item == Items.COAL ||
                    item == Items.REDSTONE || item == Items.LAPIS_LAZULI) {
                return 1 + (int)(Math.random() * 3);
            }
            return 1;
        }

        if (item == Items.COAL || item == Items.REDSTONE || item == Items.LAPIS_LAZULI) {
            return 2 + (int)(Math.random() * 4);
        }
        if (item == Items.GOLD_NUGGET) {
            return 2 + (int)(Math.random() * 4);
        }
        if (item == Items.COPPER_INGOT) {
            return 1 + (int)(Math.random() * 3);
        }
        return 1;
    }

    private static long getRandomDuration(boolean forTamed, QuestType questType) {
        long minSeconds, maxSeconds;

        if (forTamed) {
            minSeconds = 60;
            maxSeconds = 180;
        } else {
            minSeconds = 120;
            maxSeconds = 240;
        }

        long seconds = minSeconds + (long)(Math.random() * (maxSeconds - minSeconds + 1));
        return seconds * 1000;
    }

    // ВРЕМЕННО УБРАНО - будет добавлено позже
    /*

    private static String getRandomTargetMob() {
        String[] mobs = {"zombie", "skeleton", "creeper"};
        return mobs[(int)(Math.random() * mobs.length)];
    }

    public void checkMobKill(String mobId, Player player) {
        if (questType == QuestType.KILL_MOBS && isActive) {
            if (mobId != null && mobId.equals(targetMob)) {
                incrementKillCount();

                if (killCount % 3 == 0 || killCount == requiredAmount) {
                    player.sendSystemMessage(Component.translatable("quest.kitsune.kill.progress",
                            killCount, requiredAmount, getMobName()));
                }

                if (killCount >= requiredAmount) {
                    player.sendSystemMessage(Component.translatable("quest.kitsune.kill.ready"));
                }
            }
        }
    }

    private String getMobName() {
        switch (targetMob) {
            case "zombie": return "Зомби";
            case "skeleton": return "Скелет";
            case "creeper": return "Крипер";
            default: return targetMob;
        }
    }
    */
}
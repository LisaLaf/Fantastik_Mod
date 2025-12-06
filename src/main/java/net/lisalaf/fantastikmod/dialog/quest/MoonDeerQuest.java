package net.lisalaf.fantastikmod.dialog.quest;

import net.lisalaf.fantastikmod.entity.custom.MoonDeerEntity;
import net.lisalaf.fantastikmod.item.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import java.util.List;

public class MoonDeerQuest extends Quest {
    private final MoonDeerEntity moonDeer;
    private final boolean isForTamed;

    public MoonDeerQuest(MoonDeerEntity moonDeer, boolean isForTamed) {
        super(
                "moondeer_quest_" + System.currentTimeMillis(),
                getRandomNameKey(isForTamed),
                getRandomDescriptionKey(isForTamed),
                getRandomRequiredItem(isForTamed),
                getRandomRequiredAmount(isForTamed),
                getRandomRewardItem(isForTamed),
                getRandomRewardAmount(isForTamed),
                getRandomDuration(isForTamed)
        );
        this.moonDeer = moonDeer;
        this.isForTamed = isForTamed;
    }

    @Override
    protected void updateCustomProgress(Player player, Level level) {
    }

    @Override
    public boolean canStart() {
        if (!isForTamed && moonDeer.hurtTime > 0) {
            return false;
        }

        return !isActive && !isCompleted;
    }

    @Override
    public void onStart() {
        Player playerToReward = getPlayerToReward();
        if (playerToReward != null) {
            playerToReward.sendSystemMessage(Component.translatable(
                    isForTamed ? "quest.moondeer.tamed.start" : "quest.moondeer.wild.start"
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
                        isForTamed ? "quest.moondeer.tamed.complete" : "quest.moondeer.wild.complete"
                ));
            } else {
                playerToReward.drop(rewardStack, false);
                playerToReward.sendSystemMessage(Component.translatable(
                        isForTamed ? "quest.moondeer.tamed.complete" : "quest.moondeer.wild.complete"
                ));
            }
        }
    }

    @Override
    public void onFail() {
        Player playerToReward = getPlayerToReward();
        if (playerToReward != null) {
            playerToReward.sendSystemMessage(Component.translatable("quest.moondeer.failed"));
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


    private Player getPlayerToCheck() {
        if (isForTamed) {
            LivingEntity owner = moonDeer.getOwner();
            if (owner instanceof Player player) {
                return player;
            }
            List<Player> players = moonDeer.level().getEntitiesOfClass(Player.class,
                    moonDeer.getBoundingBox().inflate(16.0));
            return !players.isEmpty() ? players.get(0) : null;
        } else {
            List<Player> players = moonDeer.level().getEntitiesOfClass(Player.class,
                    moonDeer.getBoundingBox().inflate(8.0));
            return !players.isEmpty() ? players.get(0) : null;
        }
    }

    private Player getPlayerToReward() {
        return getPlayerToCheck();
    }


    private static String getRandomNameKey(boolean forTamed) {
        return "quest.moondeer." + (forTamed ? "tamed" : "wild") + ".name";
    }

    private static String getRandomDescriptionKey(boolean forTamed) {
        return "quest.moondeer." + (forTamed ? "tamed" : "wild") + ".desc";
    }

    private static Item getRandomRequiredItem(boolean forTamed) {
        if (forTamed) {
            Item[] tamedItems = {
                    Items.WHEAT, Items.CARROT, Items.POTATO, Items.BEETROOT,
                    Items.APPLE, Items.MELON_SLICE, Items.PUMPKIN_PIE,
                    Items.BREAD, Items.COOKIE, Items.PUMPKIN, Items.SUGAR_CANE
            };
            return tamedItems[(int)(Math.random() * tamedItems.length)];
        } else {
            Item[] wildItems = {
                    ModItems.MOON_CROWBERRY.get(),
                    Items.BONE,
                    Items.ROTTEN_FLESH,
                    Items.STRING,
                    Items.GUNPOWDER,
                    Items.ARROW,
                    Items.SLIME_BALL
            };
            return wildItems[(int)(Math.random() * wildItems.length)];
        }
    }

    private static int getRandomRequiredAmount(boolean forTamed) {
        if (forTamed) {
            return 3 + (int)(Math.random() * 6);
        } else {
            return 1 + (int)(Math.random() * 4);
        }
    }

    private static Item getRandomRewardItem(boolean forTamed) {
        if (forTamed) {
            // Для прирученных - изумруд или серебряные/золотые осколки
            Item[] tamedRewards = {
                    Items.EMERALD,                    // Изумруд 1
                    Items.IRON_INGOT,                 // Железный слиток 1-2
                    Items.GOLD_NUGGET,                // Самородок золота 3-6
                    ModItems.SILVER_INGOT.get()       // Серебряный слиток 1-3
            };
            return tamedRewards[(int)(Math.random() * tamedRewards.length)];
        } else {
            // Для диких - лунный самоцвет, изумруд или алмаз
            Item[] wildRewards = {
                    ModItems.GEM_MOON.get(),          // Лунный самоцвет 1
                    Items.DIAMOND,                    // Алмаз 1
                    Items.EMERALD,                    // Изумруд 1-2
                    ModItems.SILVER_INGOT.get()       // Серебряный слиток 1
            };
            return wildRewards[(int)(Math.random() * wildRewards.length)];
        }
    }

    private static int getRandomRewardAmount(boolean forTamed) {
        Item item = getRandomRewardItem(forTamed);

        if (forTamed) {
            // Награды прирученных
            if (item == Items.GOLD_NUGGET) {
                return 3 + (int)(Math.random() * 4); // 3-6 осколков
            }
            if (item == ModItems.SILVER_INGOT.get()) {
                return 1 + (int)(Math.random() * 3); // 1-3 слитка
            }
            if (item == Items.IRON_INGOT) {
                return 1 + (int)(Math.random() * 2); // 1-2 слитка
            }
            return 1;
        } else {
            if (item == Items.EMERALD) {
                return 1 + (int)(Math.random() * 2);
            }
            return 1;
        }
    }

    private static long getRandomDuration(boolean forTamed) { // УБРАЛ QuestType
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
}
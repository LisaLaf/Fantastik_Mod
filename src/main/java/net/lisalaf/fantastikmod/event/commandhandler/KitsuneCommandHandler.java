package net.lisalaf.fantastikmod.event.commandhandler;

import net.lisalaf.fantastikmod.entity.custom.KitsuneLightEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.lisalaf.fantastikmod.fantastikmod;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mod.EventBusSubscriber(modid = fantastikmod.MOD_ID)
public class KitsuneCommandHandler {
    private static final ScheduledExecutorService SCHEDULER =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "KitsuneCommandHandler");
                t.setDaemon(true); // Чтобы не мешал завершению
                return t;
            });
    private static int talkIndex = 0;
    private static int stopIndex = 0;
    private static int followIndex = 0;
    private static int confusedIndex = 0;

    @SubscribeEvent
    public static void onChatMessage(ServerChatEvent event) {
        String message = event.getMessage().getString().trim();
        Player player = event.getPlayer();

        String[] words = message.split("\\s+", 2);
        if (words.length < 2) return;

        String targetName = words[0];
        String command = words[1].toLowerCase();

        AABB area = player.getBoundingBox().inflate(15);
        List<KitsuneLightEntity> kitsunes = player.level().getEntitiesOfClass(
                KitsuneLightEntity.class, area,
                k -> k.isTamed() &&
                        k.getCustomName() != null &&
                        k.getCustomName().getString().equals(targetName)
        );

        if (kitsunes.isEmpty()) return;

        if (kitsunes.size() > 1) {
            for (int i = 0; i < kitsunes.size(); i++) {
                final KitsuneLightEntity k = kitsunes.get(i);
                final long delay = i * 2;
                SCHEDULER.schedule(() -> {
                    if (k.isAlive() && player != null) {
                        k.sayToPlayer(player, getConfusedPhrase());
                    }
                }, delay, TimeUnit.SECONDS);
            }
            return;
        }

        KitsuneLightEntity kitsune = kitsunes.get(0);

        if (!kitsune.getOwnerUUID().equals(player.getUUID())) {
            kitsune.sayToPlayer(player, getRandomNotOwnerPhrase(player));
            return;
        }


        switch (command) {
            case "стоп":
            case "stop":
                if (kitsune.isSleeping()) {
                    kitsune.sayToPlayer(player, getRandomSleepPhrase());
                    return;
                }
                kitsune.setAIMode(KitsuneLightEntity.AI_SIT);
                kitsune.startSitAnimation();
                kitsune.sayToPlayer(player, getRandomStopPhrase(player));
                break;

            case "жди":
            case "wait":
                if (kitsune.isSleeping()) {
                    kitsune.sayToPlayer(player, getRandomSleepPhrase());
                    return;
                }
                kitsune.setAIMode(KitsuneLightEntity.AI_SIT);
                kitsune.startSitAnimation();
                kitsune.sayToPlayer(player, getRandomWaitPhrase(player));
                break;

            case "проснись":
            case "просыпайся":
            case "проснися":
            case "вставай":
            case "подъём":
            case "wake":
            case "wake up":
            case "get up":
                if (!kitsune.isSleeping()) {
                    kitsune.sayToPlayer(player, getSleepPhrase());
                    return;
                }
                kitsune.setWakeAnimPlaying(true);
                kitsune.setSleeping(false);
                kitsune.setSleepAnimPlaying(false);
                kitsune.startStandAnimation();
                kitsune.sayToPlayer(player, getSleepPhrase());
                break;

            case "подойди ко мне":
            case "подойди":
            case "иди сюда":
            case "иди ко мне":
            case "сюда":
            case "за мной":
            case "следуй за мной":
            case "пошли":
            case "идём":
            case "двигай":
            case "come here":
            case "follow":
            case "follow me":
            case "let's go":
            case "move":
                if (kitsune.isSleeping()) {
                    kitsune.sayToPlayer(player, getRandomSleepPhrase());
                    return;
                }
                kitsune.setSitting(false);
                kitsune.setStandAnimPlaying(false);
                kitsune.setSitAnimPlaying(false);
                kitsune.setAIMode(KitsuneLightEntity.AI_FOLLOW);
                if (kitsune.isSitting()) kitsune.startStandAnimation();
                kitsune.sayToPlayer(player, getRandomFollowPhrase(player));
                break;

            case "ко мне":
            case "come":
            case "сидеть":
            case "sit":
            case "лежать":
            case "lie":
            case "голос":
            case "speak dog":
            case "дай лапу":
            case "paw":
            case "фас":
            case "attack dog":
            case "апорт":
            case "fetch":
            case "место":
            case "place":
                if (kitsune.isSleeping()) {
                    kitsune.sayToPlayer(player, getSleepPhrase());
                    return;
                }
                kitsune.sayToPlayer(player, handleDogCommand(player));
                break;

            case "помоги":
            case "помоги мне":
            case "спасай":
            case "на помощь":
            case "защити":
            case "help":
            case "help me":
            case "save me":
            case "protect":
                if (kitsune.isSleeping()) {
                    kitsune.sayToPlayer(player, getSleepPhrase());
                    return;
                }
                kitsune.setSitting(false);
                kitsune.setStandAnimPlaying(false);
                kitsune.setSitAnimPlaying(false);
                kitsune.setAIMode(KitsuneLightEntity.AI_FOLLOW);
                kitsune.sayToPlayer(player, getHelpPhrase());
                break;

            case "пожалуйста помоги":
            case "пожалуйста спаси":
            case "помоги пожалуйста":
            case "спаси пожалуйста":
            case "please help":
            case "please save me":
            case "help please":
                if (kitsune.isSleeping()) {
                    kitsune.sayToPlayer(player, getSleepPhrase());
                    return;
                }
                BlockPos safePos = findSafePosition(player);
                if (safePos != null) {
                    spawnRescueParticles(player);
                    player.teleportTo(safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5);
                    spawnRescueParticles(player);
                    kitsune.sayToPlayer(player, getRescuePhrase());
                } else {
                    kitsune.sayToPlayer(player, getNoSafePlacePhrase());
                }
                break;

            case "ты где":
            case "ты где?":
            case "ау":
            case "ау!":
            case "куда пропала":
            case "куда ушла":
            case "куда делась":
            case "потерялась":
            case "where are you":
            case "where are you?":
            case "where did you go":
            case "are you lost":
                List<KitsuneLightEntity> allNamed = new java.util.ArrayList<>();
                for (net.minecraft.world.level.Level serverLevel : player.getServer().getAllLevels()) {
                    allNamed.addAll(serverLevel.getEntitiesOfClass(
                            KitsuneLightEntity.class,
                            new AABB(
                                    player.getX() - 500, player.getY() - 256, player.getZ() - 500,
                                    player.getX() + 500, player.getY() + 256, player.getZ() + 500
                            ),
                            k -> k.isTamed() &&
                                    k.getOwnerUUID() != null &&
                                    k.getOwnerUUID().equals(player.getUUID()) &&
                                    k.getCustomName() != null &&
                                    k.getCustomName().getString().equalsIgnoreCase(targetName)
                    ));
                }

                if (allNamed.isEmpty()) return;
                if (allNamed.size() > 1) {
                    for (int i = 0; i < allNamed.size(); i++) {
                        final KitsuneLightEntity k = allNamed.get(i);
                        final long delay = i * 2;
                        SCHEDULER.schedule(() -> {
                            if (k.isAlive() && player != null) {
                                BlockPos nearPlayer = findPositionNearPlayer(player);
                                if (nearPlayer != null) {
                                    spawnRescueParticlesForEntity(k);
                                    k.teleportTo(nearPlayer.getX() + 0.5, nearPlayer.getY(), nearPlayer.getZ() + 0.5);
                                    spawnRescueParticlesForEntity(k);
                                }
                                k.sayToPlayer(player, getHerePhrase());
                            }
                        }, delay, TimeUnit.SECONDS);
                    }
                    return;
                }
                if (kitsune.isSleeping()) {
                    kitsune.sayToPlayer(player, getSleepPhrase());
                    return;
                }
                BlockPos nearPlayer = findPositionNearPlayer(player);
                if (nearPlayer != null) {
                    spawnRescueParticlesForEntity(kitsune);
                    kitsune.teleportTo(nearPlayer.getX() + 0.5, nearPlayer.getY(), nearPlayer.getZ() + 0.5);
                    spawnRescueParticlesForEntity(kitsune);
                }
                kitsune.sayToPlayer(player, getHerePhrase());
                break;

            case "говори":
            case "speak":
                if (kitsune.isSleeping()) {
                    kitsune.sayToPlayer(player, getRandomSleepPhrase());
                    return;
                }
                kitsune.sayToPlayer(player, getNextTalkPhrase());
                SCHEDULER.schedule(() -> {
                    if (kitsune.isAlive() && player != null) {
                        kitsune.sayToPlayer(player, getNextTalkPhrase());
                    }
                }, 2, TimeUnit.SECONDS);

                SCHEDULER.schedule(() -> {
                    if (kitsune.isAlive() && player != null) {
                        kitsune.sayToPlayer(player, getNextTalkPhrase());
                    }
                }, 4, TimeUnit.SECONDS);
                break;

            default:
                break;
        }
    }

    private static BlockPos findSafePosition(Player player) {
        Level level = player.level();
        BlockPos playerPos = player.blockPosition();

        for (int r = 5; r <= 30; r += 5) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    BlockPos checkPos = playerPos.offset(dx, 0, dz);
                    for (int dy = -2; dy <= 4; dy++) {
                        BlockPos pos = checkPos.offset(0, dy, 0);
                        BlockState below = level.getBlockState(pos.below());
                        BlockState at = level.getBlockState(pos);
                        BlockState above = level.getBlockState(pos.above());
                        if (below.isSolid() &&
                                !below.is(Blocks.LAVA) &&
                                !below.is(Blocks.WATER) &&
                                at.isAir() &&
                                above.isAir()) {
                            return pos;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static void spawnRescueParticles(Player player) {
        Level level = player.level();
        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 30; i++) {
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        player.getX() + (level.random.nextDouble() - 0.5) * 2,
                        player.getY() + level.random.nextDouble() * 2,
                        player.getZ() + (level.random.nextDouble() - 0.5) * 2,
                        1, 0, 0.05, 0, 0.05);
                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                        player.getX() + (level.random.nextDouble() - 0.5) * 2,
                        player.getY() + level.random.nextDouble() * 2,
                        player.getZ() + (level.random.nextDouble() - 0.5) * 2,
                        1, 0, 0.02, 0, 0.02);
            }
        }
    }
    private static BlockPos findPositionNearPlayer(Player player) {
        Level level = player.level();
        BlockPos playerPos = player.blockPosition();

        for (int r = 1; r <= 3; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    BlockPos checkPos = playerPos.offset(dx, 0, dz);
                    if (level.getBlockState(checkPos).isAir() &&
                            level.getBlockState(checkPos.above()).isAir() &&
                            level.getBlockState(checkPos.below()).isSolid()) {
                        return checkPos;
                    }
                }
            }
        }
        return playerPos;
    }

    private static void spawnRescueParticlesForEntity(KitsuneLightEntity entity) {
        Level level = entity.level();
        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 30; i++) {
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        entity.getX() + (level.random.nextDouble() - 0.5) * 2,
                        entity.getY() + level.random.nextDouble() * 2,
                        entity.getZ() + (level.random.nextDouble() - 0.5) * 2,
                        1, 0, 0.05, 0, 0.05);
                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                        entity.getX() + (level.random.nextDouble() - 0.5) * 2,
                        entity.getY() + level.random.nextDouble() * 2,
                        entity.getZ() + (level.random.nextDouble() - 0.5) * 2,
                        1, 0, 0.02, 0, 0.02);
            }
        }
    }

    private static Component getHerePhrase() {
        return Component.translatable("chat.fantastikmod.kitsune.command.here." + (int)(Math.random() * 4));
    }

    private static Component getHereMultiplePhrase() {
        return Component.translatable("chat.fantastikmod.kitsune.command.here_multiple." + (int)(Math.random() * 3));
    }

    private static Component getRescuePhrase() {
        return Component.translatable("chat.fantastikmod.kitsune.command.rescue." + (int)(Math.random() * 3));
    }

    private static Component getNoSafePlacePhrase() {
        return Component.translatable("chat.fantastikmod.kitsune.command.no_safe." + (int)(Math.random() * 2));
    }

    private static Component getHelpPhrase() {
        return Component.translatable("chat.fantastikmod.kitsune.command.help." + (int)(Math.random() * 4));
    }
    private static Component getSleepPhrase() {
        return Component.translatable("chat.fantastikmod.kitsune.sleep." + (int)(Math.random() * 3));
    }
    private static Component handleDogCommand(Player player) {
        return Component.translatable("chat.fantastikmod.kitsune.command.dog." + (int)(Math.random() * 8));
    }
    private static Component getRandomSleepPhrase() {
        return Component.translatable("chat.fantastikmod.kitsune.command.sleeping." + (int)(Math.random() * 3));
    }

    private static Component getRandomStopPhrase(Player player) {
        return Component.translatable("chat.fantastikmod.kitsune.command.stop." + (int)(Math.random() * 4));
    }

    private static Component getRandomWaitPhrase(Player player) {
        return Component.translatable("chat.fantastikmod.kitsune.command.wait." + (int)(Math.random() * 3));
    }

    private static Component getRandomFollowPhrase(Player player) {
        return Component.translatable("chat.fantastikmod.kitsune.command.follow." + (int)(Math.random() * 4));
    }

    private static Component getNextTalkPhrase() {
        Component msg = Component.translatable("chat.fantastikmod.kitsune.command.talk." + talkIndex);
        talkIndex = (talkIndex + 1) % 4;
        return msg;
    }

    private static Component getConfusedPhrase() {
        Component msg = Component.translatable("chat.fantastikmod.kitsune.command.confused." + confusedIndex);
        confusedIndex = (confusedIndex + 1) % 4;
        return msg;
    }

    private static Component getRandomNotOwnerPhrase(Player player) {
        return Component.translatable("chat.fantastikmod.kitsune.command.not_owner." + (int)(Math.random() * 5));
    }
}
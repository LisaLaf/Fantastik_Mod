package net.lisalaf.fantastikmod.item.custom;

import net.lisalaf.fantastikmod.entity.ModEntities;
import net.lisalaf.fantastikmod.entity.custom.KitsuneSoulPossessedEntity;
import net.lisalaf.fantastikmod.item.client.KitsuneSoulMaskRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

@SuppressWarnings("removal")
public class KitsuneSoulMaskItem extends ArmorItem implements GeoItem {

    public static final String TAG_CHARGE = "soulCharge";
    public static final String TAG_LAST_WHISPER = "lastWhisperTime";
    public static final String TAG_LAST_CONTROL_LOSS = "lastControlLossTime";
    public static final String TAG_CONTROL_LOSS_END = "controlLossEnd";
    public static final String TAG_LAST_ATTACK = "lastAttackTime";
    public static final String TAG_LAST_ACTION = "lastAutoActionTime";
    public static final String TAG_DEATH_TIMER_START = "deathTimerStart";
    public static final String TAG_DEATH_DONE = "deathDone";
    public static final String TAG_LOCK_TYPE = "lockType";
    public static final String TAG_LAST_HUNT_ATTACK = "lastHuntAttack";

    public static final int MAX_CHARGE = 200;
    public static final int WHISPER_THRESHOLD = 50;
    public static final int CONTROL_LOSS_THRESHOLD = 150;
    public static final int AUTO_ACTION_THRESHOLD = 200;

    public static final int POINTS_PER_KILL = 7;
    public static final int POINTS_PER_ATTACK = 3;

    private static final int ATTACK_COOLDOWN_TICKS = 20;
    private static final long DEATH_DELAY_MS = 2 * 60 * 1000L;

    private static final int ATTACK_MIN_CHARGE = 20;

    private static final String[] WHISPER_KEYS = {
            "mask.kitsune.whisper.giggle",
            "mask.kitsune.whisper.mumble"
    };

    private static final int LOCK_TYPE_SLOW = 0;
    private static final int LOCK_TYPE_LUNGE = 1;
    private static final int LOCK_TYPE_HOLD = 2;
    private static final int LOCK_TYPE_MUTE = 3;

    private static final long HUNT_ATTACK_COOLDOWN_MS = 3000L;
    private static final double HUNT_SPEED = 0.35;
    private static final double HUNT_REACH = 3.5;
    private static final double HUNT_SEARCH_RADIUS = 32.0;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final int colorVariant;
    private final Random random = new Random();

    public KitsuneSoulMaskItem(ArmorMaterial material, Properties properties, int colorVariant) {
        super(material, Type.HELMET, properties);
        this.colorVariant = colorVariant;
    }

    public int getColorVariant() { return colorVariant; }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public void initializeClient(Consumer<net.minecraftforge.client.extensions.common.IClientItemExtensions> consumer) {
        consumer.accept(new net.minecraftforge.client.extensions.common.IClientItemExtensions() {
            private KitsuneSoulMaskRenderer renderer;
            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack,
                                                          EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (renderer == null) renderer = new KitsuneSoulMaskRenderer();
                renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return renderer;
            }
        });
    }

    @Override
    public boolean isFoil(ItemStack stack) { return true; }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        CompoundTag tag = stack.getOrCreateTag();
        int charge = tag.getInt(TAG_CHARGE);
        if (charge >= AUTO_ACTION_THRESHOLD) {
            ItemStack headSlot = player.getItemBySlot(EquipmentSlot.HEAD);
            if (!(headSlot.getItem() instanceof KitsuneSoulMaskItem)) {
                player.setItemSlot(EquipmentSlot.HEAD, stack.copy());

                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(
                            Component.translatable("mask.kitsune.cannot_remove")
                                    .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));
                    serverPlayer.playNotifySound(SoundEvents.ENDERMAN_TELEPORT,
                            SoundSource.PLAYERS, 0.6f, 1.4f);
                }
            }
        }
        if (!(player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof KitsuneSoulMaskItem)) {
            return;
        }

        applyEffects(player);

        if (level.isClientSide) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        long gameTime = serverLevel.getGameTime();
        charge = tag.getInt(TAG_CHARGE);

        long lastAttack = tag.getLong(TAG_LAST_ATTACK);
        if (player.isShiftKeyDown()
                && charge >= ATTACK_MIN_CHARGE
                && gameTime - lastAttack >= ATTACK_COOLDOWN_TICKS) {
            doAttack(serverPlayer, serverLevel, stack);
            tag.putLong(TAG_LAST_ATTACK, gameTime);

            if (charge < MAX_CHARGE) {
                charge = Math.min(MAX_CHARGE, charge + POINTS_PER_ATTACK);
                tag.putInt(TAG_CHARGE, charge);
            }
        }

        if (charge >= WHISPER_THRESHOLD) {
            long lastWhisper = tag.getLong(TAG_LAST_WHISPER);
            long interval = 200 + random.nextInt(400);
            if (gameTime - lastWhisper >= interval) {
                sendWhisper(serverPlayer);
                tag.putLong(TAG_LAST_WHISPER, gameTime);
            }
        }

        if (charge >= CONTROL_LOSS_THRESHOLD) {
            handleControlLoss(serverPlayer, tag);
        }
        if (charge >= AUTO_ACTION_THRESHOLD) {
            if (tag.getBoolean(TAG_DEATH_DONE)) return;

            long deathStart = tag.getLong(TAG_DEATH_TIMER_START);
            if (deathStart == 0) {
                tag.putLong(TAG_DEATH_TIMER_START, System.currentTimeMillis());
                broadcastToPlayer(serverPlayer, "mask.kitsune.possessed");
            } else if (System.currentTimeMillis() - deathStart >= DEATH_DELAY_MS) {
                tag.putBoolean(TAG_DEATH_DONE, true);
                killPlayerBySpirit(serverPlayer, serverLevel);
                return;
            }
            performHunt(serverPlayer, serverLevel, tag);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if (level.isClientSide) return;
        if (!(entity instanceof ServerPlayer player)) return;

        CompoundTag tag = stack.getOrCreateTag();
        int charge = tag.getInt(TAG_CHARGE);

        if (charge >= AUTO_ACTION_THRESHOLD) {
            ItemStack headSlot = player.getItemBySlot(EquipmentSlot.HEAD);
            if (!(headSlot.getItem() instanceof KitsuneSoulMaskItem)) {
                ItemStack maskToEquip = stack.copy();
                maskToEquip.setCount(1);
                stack.shrink(1);
                player.setItemSlot(EquipmentSlot.HEAD, maskToEquip);

                player.sendSystemMessage(
                        Component.translatable("mask.kitsune.cannot_remove")
                                .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));
                player.playNotifySound(SoundEvents.ENDERMAN_TELEPORT,
                        SoundSource.PLAYERS, 0.6f, 1.4f);
            }
        }
    }
    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player) {
        CompoundTag tag = item.getOrCreateTag();
        if (tag.getInt(TAG_CHARGE) >= AUTO_ACTION_THRESHOLD) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(
                        Component.translatable("mask.kitsune.cannot_drop")
                                .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));
            }
            return false;
        }
        return super.onDroppedByPlayer(item, player);
    }

    private void applyEffects(Player player) {
        if (!player.hasEffect(MobEffects.NIGHT_VISION))
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 100, 0, false, false, true));
        if (!player.hasEffect(MobEffects.MOVEMENT_SPEED))
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 10, 0, false, false, true));
        if (!player.hasEffect(MobEffects.DAMAGE_BOOST))
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 10, 0, false, false, true));
        if (!player.hasEffect(MobEffects.JUMP))
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, 10, 0, false, false, true));
    }

    private void sendWhisper(ServerPlayer player) {
        String key = WHISPER_KEYS[random.nextInt(WHISPER_KEYS.length)];
        player.sendSystemMessage(Component.translatable(key)
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        player.playNotifySound(SoundEvents.ENDERMAN_AMBIENT, SoundSource.PLAYERS, 0.3f, 1.8f);
    }

    private void handleControlLoss(ServerPlayer player, CompoundTag tag) {
        long lossEnd = tag.getLong(TAG_CONTROL_LOSS_END);
        long now = System.currentTimeMillis();

        if (lossEnd > now) {
            maintainControlLoss(player, tag, tag.getInt(TAG_LOCK_TYPE));
            return;
        }
        releaseControl(player, tag);

        long lastLoss = tag.getLong(TAG_LAST_CONTROL_LOSS);
        boolean firstTime = lastLoss == 0;
        long cooldown = 8_000L + (long)(random.nextDouble() * 12_000L); // 8–20 сек

        if (firstTime || now - lastLoss >= cooldown) {
            int type = random.nextInt(4);
            long duration;
            switch (type) {
                case LOCK_TYPE_SLOW -> duration = 3_000L + (long)(random.nextDouble() * 5_000L); // 3–8 сек
                case LOCK_TYPE_LUNGE -> duration = 600L + (long)(random.nextDouble() * 900L);    // 0.6–1.5 сек
                case LOCK_TYPE_HOLD -> duration = 2_000L + (long)(random.nextDouble() * 3_000L); // 2–5 сек
                case LOCK_TYPE_MUTE -> duration = 3_000L + (long)(random.nextDouble() * 4_000L); // 3–7 сек
                default -> duration = 3_000L;
            }
            tag.putInt(TAG_LOCK_TYPE, type);
            tag.putLong(TAG_CONTROL_LOSS_END, now + duration);
            tag.putLong(TAG_LAST_CONTROL_LOSS, now);
            applyControlLock(player, type);
        }
    }

    private void applyControlLock(ServerPlayer player, int type) {
        switch (type) {
            case LOCK_TYPE_LUNGE -> {
                double angle = random.nextDouble() * Math.PI * 2;
                double dx = Math.cos(angle) * 1.2;
                double dz = Math.sin(angle) * 1.2;
                player.setDeltaMovement(dx, 0.3, dz);
                player.hasImpulse = true;
                player.hurtMarked = true;
                player.playNotifySound(SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.6f, 1.4f);
            }
            case LOCK_TYPE_HOLD -> {
                if (!player.isPassenger()) {
                    Marker marker = EntityType.MARKER.create(player.level());
                    if (marker != null) {
                        marker.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
                        marker.setInvisible(true);
                        marker.setInvulnerable(true);
                        marker.noPhysics = true;
                        player.level().addFreshEntity(marker);
                        player.startRiding(marker, true);
                    }
                }
            }
            case LOCK_TYPE_MUTE -> {
                if (player.isUsingItem()) player.stopUsingItem();
                player.setSprinting(false);
            }
            default -> {}
        }
    }

    private void maintainControlLoss(ServerPlayer player, CompoundTag tag, int type) {
        switch (type) {
            case LOCK_TYPE_SLOW -> {
                player.setSprinting(false);
                if (player.isUsingItem()) player.stopUsingItem();
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 2, false, false, false));
            }
            case LOCK_TYPE_LUNGE -> {
                Vec3 v = player.getDeltaMovement();
                if (v.horizontalDistanceSqr() < 0.05) {
                    double angle = random.nextDouble() * Math.PI * 2;
                    player.setDeltaMovement(Math.cos(angle) * 1.0, 0.2, Math.sin(angle) * 1.0);
                    player.hurtMarked = true;
                }
            }
            case LOCK_TYPE_HOLD -> {
                if (!player.isPassenger() || !(player.getVehicle() instanceof Marker)) {
                    applyControlLock(player, LOCK_TYPE_HOLD);
                }
                Vec3 v = player.getDeltaMovement();
                player.setDeltaMovement(0, v.y, 0);
            }
            case LOCK_TYPE_MUTE -> {
                if (player.isUsingItem()) player.stopUsingItem();
                player.setSprinting(false);
            }
        }
    }

    private void releaseControl(ServerPlayer player, CompoundTag tag) {
        tag.putLong(TAG_CONTROL_LOSS_END, 0);
        if (player.isPassenger() && player.getVehicle() instanceof Marker marker) {
            player.stopRiding();
            marker.discard();
        }
        player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
    }

    private void startControlLoss(ServerPlayer player) {
        if (player.isPassenger()) return;
        Marker marker = EntityType.MARKER.create(player.level());
        if (marker == null) return;
        marker.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
        marker.setInvisible(true);
        marker.setInvulnerable(true);
        marker.noPhysics = true;
        player.level().addFreshEntity(marker);
        player.startRiding(marker, true);
    }

    private void maintainControlLoss(ServerPlayer player) {
        if (!player.isPassenger() || !(player.getVehicle() instanceof Marker)) {
            startControlLoss(player);
        }

        Vec3 v = player.getDeltaMovement();
        player.setDeltaMovement(0, v.y, 0);

        player.setSprinting(false);

        if (player.isUsingItem()) {
            player.stopUsingItem();
        }
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 10, false, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, 20, 128, false, false, false));
    }

    private void stopControlLoss(ServerPlayer player, CompoundTag tag) {
        tag.putLong(TAG_CONTROL_LOSS_END, 0);
        if (player.isPassenger() && player.getVehicle() instanceof Marker marker) {
            player.stopRiding();
            marker.discard();
        }
    }

    private void performRandomAutoAction(ServerPlayer player, ServerLevel level) {
        switch (random.nextInt(4)) {
            case 0 -> autoEatRawFood(player);
            case 1 -> autoKillAnimal(player, level);
            case 2 -> autoRun(player);
            case 3 -> autoIgniteTarget(player, level);
        }
    }

    private void autoEatRawFood(ServerPlayer player) {
        var inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (s.isEmpty()) continue;
            if (s.is(Items.ROTTEN_FLESH) || s.is(Items.CHICKEN) || s.is(Items.BEEF)
                    || s.is(Items.PORKCHOP) || s.is(Items.MUTTON) || s.is(Items.RABBIT)
                    || s.is(Items.COD) || s.is(Items.SALMON)) {
                FoodData food = player.getFoodData();
                food.setFoodLevel(Math.min(20, food.getFoodLevel() + 2));
                food.setSaturation(Math.min(food.getFoodLevel(), food.getSaturationLevel() + 1));
                s.shrink(1);
                player.playNotifySound(SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 0.8f, 1.0f);
                player.sendSystemMessage(Component.translatable("mask.kitsune.ate_raw")
                        .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                return;
            }
        }
    }

    private void autoKillAnimal(ServerPlayer player, ServerLevel level) {
        List<Animal> animals = level.getEntitiesOfClass(Animal.class, player.getBoundingBox().inflate(10));
        if (animals.isEmpty()) return;
        Animal target = animals.get(random.nextInt(animals.size()));
        target.hurt(player.damageSources().playerAttack(player), 1000f);
        player.playNotifySound(SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.8f, 1.0f);
        player.sendSystemMessage(Component.translatable("mask.kitsune.hand_moved")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    private void autoRun(ServerPlayer player) {
        double angle = random.nextDouble() * Math.PI * 2;
        double dx = Math.cos(angle) * 0.5;
        double dz = Math.sin(angle) * 0.5;
        player.setDeltaMovement(dx, player.getDeltaMovement().y, dz);
        player.hasImpulse = true;
        player.hurtMarked = true;
    }

    private void autoIgniteTarget(ServerPlayer player, ServerLevel level) {
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(8),
                e -> e != player && e.isAlive() && (e instanceof Monster || e instanceof Animal));

        if (targets.isEmpty()) return;
        LivingEntity target = targets.get(random.nextInt(targets.size()));

        if (random.nextFloat() < 0.4f) {
            target.setSecondsOnFire(5);
            level.sendParticles(ParticleTypes.FLAME, target.getX(), target.getY() + 1, target.getZ(),
                    20, 0.3, 0.3, 0.3, 0.05);
            player.playNotifySound(SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.8f, 1.0f);
        }
    }

    private void doAttack(ServerPlayer player, Level level, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        int charge = tag.getInt(TAG_CHARGE);
        if (charge < ATTACK_MIN_CHARGE) return;

        float damage = Math.min(10f, charge / 20.0f);
        float radius = 3.0f + damage;

        AABB area = player.getBoundingBox().inflate(radius);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area,
                e -> e != player && e.isAlive());

        for (LivingEntity target : targets) {
            target.hurt(player.damageSources().magic(), damage);
            target.setSecondsOnFire(Math.max(1, (int)(damage * 2)));
        }

        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 50; i++) {
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        player.getX() + (level.random.nextDouble() - 0.5) * radius * 2,
                        player.getY() + 1.5 + level.random.nextDouble() * 2,
                        player.getZ() + (level.random.nextDouble() - 0.5) * radius * 2,
                        1, 0, 0, 0, 0.1);
            }
        }
    }

    private void killPlayerBySpirit(ServerPlayer player, ServerLevel level) {
        MinecraftServer server = level.getServer();
        server.getPlayerList().broadcastSystemMessage(
                Component.translatable("death.fantastikmod.kitsune_spirit")
                        .withStyle(ChatFormatting.DARK_PURPLE),
                false
        );

        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        if (head.getItem() instanceof KitsuneSoulMaskItem) {
            player.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        }
        KitsuneSoulPossessedEntity mob = ModEntities.KITSUNE_SOUL_POSSESSED.get().create(level);
        if (mob != null) {
            mob.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
            mob.setupFromPlayer(player);
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (!stack.isEmpty()) {
                    mob.setItemSlot(EquipmentSlot.MAINHAND, stack.copy());
                    break;
                }
            }

            level.addFreshEntity(mob);
        }

        player.setHealth(0.0f);
    }

    private void broadcastToPlayer(ServerPlayer player, String translationKey) {
        player.sendSystemMessage(Component.translatable(translationKey)
                .withStyle(ChatFormatting.DARK_PURPLE));
        player.playNotifySound(SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 0.3f, 1.5f);
    }

    private void performHunt(ServerPlayer player, ServerLevel level, CompoundTag tag) {
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(HUNT_SEARCH_RADIUS),
                e -> e != player && e.isAlive());

        if (targets.isEmpty()) {
            if (random.nextInt(20) == 0) {
                double angle = random.nextDouble() * Math.PI * 2;
                player.setDeltaMovement(Math.cos(angle) * 0.4, player.getDeltaMovement().y, Math.sin(angle) * 0.4);
                player.hurtMarked = true;
            }
            return;
        }
        LivingEntity target = targets.get(0);
        double bestDist = player.distanceToSqr(target);
        for (LivingEntity e : targets) {
            double d = player.distanceToSqr(e);
            if (d < bestDist) { bestDist = d; target = e; }
        }

        double dist = Math.sqrt(bestDist);
        Vec3 dir = new Vec3(target.getX() - player.getX(), 0, target.getZ() - player.getZ()).normalize();
        float yaw = (float)(Math.toDegrees(Math.atan2(-dir.x, dir.z)));
        player.setYRot(yaw);
        player.setYHeadRot(yaw);
        player.yRotO = yaw;
        player.yHeadRotO = yaw;

        if (dist > HUNT_REACH) {
            player.setDeltaMovement(dir.x * HUNT_SPEED, player.getDeltaMovement().y, dir.z * HUNT_SPEED);
            player.hasImpulse = true;
            player.hurtMarked = true;
            player.setSprinting(true);
        } else {
            long lastHuntAttack = tag.getLong(TAG_LAST_HUNT_ATTACK);
            long now = System.currentTimeMillis();
            if (now - lastHuntAttack >= HUNT_ATTACK_COOLDOWN_MS) {
                tag.putLong(TAG_LAST_HUNT_ATTACK, now);
                doAttack(player, level, player.getItemBySlot(EquipmentSlot.HEAD));
            }
        }
    }
}
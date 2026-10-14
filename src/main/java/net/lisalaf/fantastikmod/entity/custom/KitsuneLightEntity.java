package net.lisalaf.fantastikmod.entity.custom;

import net.lisalaf.fantastikmod.dialog.*;
import net.lisalaf.fantastikmod.entity.ai.KitsuneLightGoal;
import net.lisalaf.fantastikmod.entity.ai.kitsune.KitsuneExplodeCreepersGoal;
import net.lisalaf.fantastikmod.entity.ai.kitsune.KitsuneTofuBlessingGoal;
import net.lisalaf.fantastikmod.entity.phrases.KitsunePhrases;
import net.lisalaf.fantastikmod.item.ModItems;
import net.lisalaf.fantastikmod.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.lisalaf.fantastikmod.dialog.DialogScreen;
import net.lisalaf.fantastikmod.dialog.mobs.KitsuneDialog;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class KitsuneLightEntity extends Animal implements GeoEntity{

    private int sitCooldown = 0;
    private int growTime = 0;

    private static final int MIN_TAMING_TOFU = 25;
    private static final int MAX_TAMING_TOFU = 40;

    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(KitsuneLightEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_EYE_VARIANT = SynchedEntityData.defineId(KitsuneLightEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_HIT_COUNT = SynchedEntityData.defineId(KitsuneLightEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_ANGRY = SynchedEntityData.defineId(KitsuneLightEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_LAUGHING = SynchedEntityData.defineId(KitsuneLightEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_TAMING_PROGRESS = SynchedEntityData.defineId(KitsuneLightEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_TAMED = SynchedEntityData.defineId(KitsuneLightEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_AI_MODE = SynchedEntityData.defineId(KitsuneLightEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_SITTING = SynchedEntityData.defineId(KitsuneLightEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_SLEEPING = SynchedEntityData.defineId(KitsuneLightEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_YAWNING = SynchedEntityData.defineId(KitsuneLightEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SIT_ANIM_PLAYING = SynchedEntityData.defineId(KitsuneLightEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_STAND_ANIM_PLAYING = SynchedEntityData.defineId(KitsuneLightEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SLEEP_ANIM_PLAYING = SynchedEntityData.defineId(KitsuneLightEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_WAKE_ANIM_PLAYING = SynchedEntityData.defineId(KitsuneLightEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_OFFENDED = SynchedEntityData.defineId(KitsuneLightEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.defineId(KitsuneLightEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> DATA_MOOD = SynchedEntityData.defineId(KitsuneLightEntity.class, EntityDataSerializers.INT);

    public static final int AI_FOLLOW = 0;
    public static final int AI_SIT = 1;
    public static final int AI_WANDER = 2;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int laughCooldown = 0;
    private int sitTimer = 0;
    private int sleepTimer = 0;
    private int yawnTimer = 0;
    private int animationTimer = 0;
    private int dodgeCooldown = 0;
    private int foxFireCooldown = 0;
    private int magicCooldown = 0;
    private int regenerationCooldown = 0;
    private int runningFromCreeperTicks = 0;
    private KitsuneTofuBlessingGoal tofuBlessingGoal;

    private UUID ownerUUID;





    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, 0);
        this.entityData.define(DATA_EYE_VARIANT, 0);
        this.entityData.define(DATA_HIT_COUNT, 0);
        this.entityData.define(DATA_IS_ANGRY, false);
        this.entityData.define(DATA_IS_LAUGHING, false);
        this.entityData.define(DATA_TAMING_PROGRESS, 0);
        this.entityData.define(DATA_TAMED, false);
        this.entityData.define(DATA_AI_MODE, AI_FOLLOW);
        this.entityData.define(DATA_IS_SITTING, false);
        this.entityData.define(DATA_IS_SLEEPING, false);
        this.entityData.define(DATA_IS_YAWNING, false);
        this.entityData.define(DATA_SIT_ANIM_PLAYING, false);
        this.entityData.define(DATA_STAND_ANIM_PLAYING, false);
        this.entityData.define(DATA_SLEEP_ANIM_PLAYING, false);
        this.entityData.define(DATA_WAKE_ANIM_PLAYING, false);
        this.entityData.define(DATA_IS_OFFENDED, false);
        this.entityData.define(DATA_OWNER_UUID, Optional.empty());
        this.entityData.define(DATA_MOOD, 50);
    }
    @Override
    public void tick() {
        CompoundTag data = this.getPersistentData();
        if (!this.level().isClientSide && data.getBoolean("maskDisappearing")) {
            if (!this.isNoAi()) this.setNoAi(true);
            if (!this.isInvulnerable()) this.setInvulnerable(true);
            handleMaskReturn();
        }

        super.tick();


        if (!this.level().isClientSide && this.tickCount % 200 == 0) {
        }

        updateTimers();
        handleAnimations();

        if (!this.level().isClientSide) {
            handleBehaviors();
            handleAbilities();
        }
        if (!this.level().isClientSide && this.isTamed() &&
                this.getAIMode() == AI_FOLLOW && this.tickCount % 100 == 0) {

            LivingEntity owner = this.getOwner();
            if (owner != null && this.distanceTo(owner) > 16.0F) {
                tryToTeleportToOwner(owner);
            }
        }

        if (!canMove()) {
            this.getNavigation().stop();
            this.setDeltaMovement(0, this.getDeltaMovement().y, 0);

            if (!isTamed()) {
                this.setTarget(null);
                this.setAggressive(false);
            }
        }
    }
    private void handleMaskReturn() {
        CompoundTag data = this.getPersistentData();
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        UUID playerUUID = data.hasUUID("maskReturnPlayer") ? data.getUUID("maskReturnPlayer") : null;
        if (playerUUID == null) {
            data.putBoolean("maskDisappearing", false);
            return;
        }

        ServerPlayer player = serverLevel.getServer().getPlayerList().getPlayer(playerUUID);
        if (player == null) return;

        long waitUntil = data.getLong("maskWaitUntil");
        long now = serverLevel.getGameTime();

        if (now >= waitUntil) {
            returnToPlayer(serverLevel, player, data);
            return;
        }

        if (serverLevel.getGameTime() % 40 == 0) {
            double dist = this.distanceToSqr(player);
            if (dist > 36.0) {
                this.teleportTo(player.getX(), player.getY(), player.getZ());
            }
        }
    }

    private void returnToPlayer(ServerLevel level, ServerPlayer player, CompoundTag data) {
        data.putBoolean("maskDisappearing", false);

        this.removeEffect(MobEffects.INVISIBILITY);

        this.setInvisible(false);
        this.setNoAi(false);
        this.setInvulnerable(false);

        this.teleportTo(player.getX(), player.getY(), player.getZ());
        this.getNavigation().stop();

        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                this.getX(), this.getY() + 0.5, this.getZ(),
                60, 0.4, 0.6, 0.4, 0.05);
        level.sendParticles(ParticleTypes.SMOKE,
                this.getX(), this.getY() + 0.5, this.getZ(),
                40, 0.4, 0.6, 0.4, 0.02);
        level.playSound(null, this.blockPosition(),
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL, 1.0f, 1.2f);

        ItemStack shard = new ItemStack(ModItems.ECHO_SHARD.get(), 1);
        if (!player.getInventory().add(shard)) {
            player.drop(shard, false);
        }

        player.sendSystemMessage(Component.translatable("chat.fantastikmod.kitsune.mask.returned")
                .withStyle(ChatFormatting.AQUA));
    }
    private void updateTimers() {
        if (laughCooldown > 0) laughCooldown--;
        if (dodgeCooldown > 0) dodgeCooldown--;
        if (foxFireCooldown > 0) foxFireCooldown--;
        if (magicCooldown > 0) magicCooldown--;
        if (regenerationCooldown > 0) regenerationCooldown--;
        if (runningFromCreeperTicks > 0) runningFromCreeperTicks--;
        if (animationTimer > 0) animationTimer--;
        if (sitCooldown > 0) sitCooldown--;

        if (this.tickCount % 600 == 0 && this.isAngry()) {
            this.setAngry(false);
            this.setHitCount(0);
        }
    }

    private void handleAnimations() {
        if (animationTimer <= 0) {
            if (isSitAnimPlaying()) {
                setSitAnimPlaying(false);
                setSitting(true);
            }
            if (isStandAnimPlaying()) {
                setStandAnimPlaying(false);
                setSitting(false);
            }
            if (isSleepAnimPlaying()) {
                setSleepAnimPlaying(false);
                setSleeping(true);
            }
            if (isWakeAnimPlaying()) {
                setWakeAnimPlaying(false);
                setSleeping(false);
            }
        }

        if (isYawning() && yawnTimer > 0) {
            yawnTimer--;
            if (yawnTimer <= 0) setYawning(false);
        }

        if (this.laughCooldown > 0) {
            if (this.laughCooldown <= 0) {
                this.setLaughing(false);
            }
            if (this.laughCooldown % 10 == 0 && this.level().isClientSide) {
                this.spawnLaughParticles();
            }
        }
    }

    private void handleBehaviors() {
        if ((isSitting() && !isStandAnimPlaying()) || (isSleeping() && !isWakeAnimPlaying()) ||
                isSitAnimPlaying() || isStandAnimPlaying() || isSleepAnimPlaying() || isWakeAnimPlaying()) {
            this.getNavigation().stop();
            this.setDeltaMovement(0, this.getDeltaMovement().y, 0);

            if (!isTamed()) {
                this.setTarget(null);
            }
        }

        if (!isTamed()) {
            handleWildBehavior();
        } else {
            handleTamedBehavior();
        }

        if (isSitting() && !isStandAnimPlaying()) {
            if (sitTimer > 0) sitTimer--;
            if (sitTimer <= 0 && !isTamed()) {
                startStandAnimation();
            }
            if (yawnTimer <= 0 && this.random.nextInt(500) == 0 && !isYawning()) {
                setYawning(true);
                yawnTimer = 70;
            }
        }
        if (isSleeping() && !isWakeAnimPlaying()) {
            if (sleepTimer > 0) sleepTimer--;
            if (sleepTimer <= 0) {
                startWakeAnimation();
            }
        }

        if (animationTimer <= 0) {
            if (isSitAnimPlaying()) {
                setSitAnimPlaying(false);
                setSitting(true);
            }
            if (isStandAnimPlaying()) {
                setStandAnimPlaying(false);
                setSitting(false);
            }
            if (isSleepAnimPlaying()) {
                setSleepAnimPlaying(false);
                setSleeping(true);
            }
            if (isWakeAnimPlaying()) {
                setWakeAnimPlaying(false);
                setSleeping(false);
            }
        }

        if (isYawning() && yawnTimer > 0) {
            yawnTimer--;
            if (yawnTimer <= 0) setYawning(false);
        }

        if (isLaughing() && laughCooldown > 0) {
            laughCooldown--;
            if (laughCooldown <= 0) {
                setLaughing(false);
            }

            if (laughCooldown % 10 == 0 && this.level().isClientSide) {
                this.spawnLaughParticles();
            }
        }
    }



    private void handleWildBehavior() {
        if (!isSitting() && !isSleeping() && !isStandAnimPlaying() && !isWakeAnimPlaying() &&
                !isSitAnimPlaying() && !isSleepAnimPlaying() && sitCooldown <= 0) {

            if (this.random.nextInt(1200) == 0 && this.onGround() && this.getTarget() == null) {
                boolean hasNearbyThreats = !this.level().getEntitiesOfClass(Player.class,
                        this.getBoundingBox().inflate(8.0D)).isEmpty();

                if (!hasNearbyThreats) {
                    if (this.level().isNight() && this.random.nextBoolean()) {
                        startSleepAnimation();
                    } else {
                        if (this.random.nextInt(7) == 0) {
                            startSitAnimation();
                            sitCooldown = 600;
                        }
                    }
                }
            }
        }
    }


    private void handleTamedBehavior() {
        if (isOffended()) {
            return;
        }
        if ((getAIMode() == AI_FOLLOW || getAIMode() == AI_WANDER) &&
                (isSleeping() || isSitting()) &&
                !isStandAnimPlaying() && !isWakeAnimPlaying()) {

            if (isSleeping()) {
                startWakeAnimation();
            } else if (isSitting()) {
                startStandAnimation();
            }
            return;
        }

        long dayTime = this.level().getDayTime() % 24000;
        boolean isMorning = dayTime >= 0 && dayTime < 12000;

        if (isSleeping() && isMorning && !isWakeAnimPlaying()) {
            startWakeAnimation();
            return;
        }

        if (getAIMode() == AI_SIT && !isSitting() && !isSleeping() &&
                !isSitAnimPlaying() && !isStandAnimPlaying()) {
            startSitAnimation();
            return;
        }

        if (getAIMode() == AI_SIT && isSitting() && !isSleeping() &&
                this.level().isNight() && this.random.nextInt(200) == 0 &&
                !isSleepAnimPlaying() && !isWakeAnimPlaying()) {
            startSleepAnimation();
        }
    }

    private void handleAbilities() {
        if (regenerationCooldown <= 0 && this.getHealth() < this.getMaxHealth()) {
            this.heal(1.0F);
            regenerationCooldown = 100;
        }

        if (magicCooldown <= 0) {
            useMagicAbilities();
            magicCooldown = 200;
        }

        if (foxFireCooldown <= 0 && isAngry() && getTarget() != null) {
            useFoxFire();
        }

        if (this.tickCount % 200 == 0 && this.random.nextInt(3) == 0 && !isAngry()) {
            playIdleSound();
        }
        if (isAngry() && this.tickCount % 100 == 0 && this.random.nextInt(2) == 0) {
            playAngrySound();
        }
    }

    public void startSitAnimation() {
        if (!isSitAnimPlaying() && !isStandAnimPlaying() && !isSitting()) {
            setSleeping(false);
            setSitAnimPlaying(true);
            animationTimer = 20;
            getNavigation().stop();
        }
    }

    public void startStandAnimation() {
        if (isSitting()) {
            setSleeping(false);
            setStandAnimPlaying(true);
            setSitting(false);
            animationTimer = 20;
            getNavigation().stop();
        }
    }

    public void startSleepAnimation() {
        if (isSitting() && !isSleeping() && !isSleepAnimPlaying() && !isWakeAnimPlaying()) {
            setSleepAnimPlaying(true);
            animationTimer = 25;
            getNavigation().stop();
        }
    }

    public void startWakeAnimation() {
        if (isSleeping()) {
            setWakeAnimPlaying(true);
            setSleeping(false);
            animationTimer = 25;
            getNavigation().stop();
        }
    }

    public void startSleep() {
        if (!isSleeping() && !isSleepAnimPlaying()) {
            if (isSitting() && !isSitAnimPlaying() && !isStandAnimPlaying()) {
                startSleepAnimation();
            } else if (!isSitting() && !isSitAnimPlaying()) {
                startSitAnimation();
                sleepTimer = 1200 + random.nextInt(1200);
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND && itemstack.isEmpty()) {
            if (isSleeping() || isSleepAnimPlaying() || isWakeAnimPlaying()) {
                if (!this.level().isClientSide) {
                    player.displayClientMessage(Component.translatable("message.fantastikmod.kitsune.asleep"), true);
                }
                return InteractionResult.FAIL;
            }

            boolean canTalk = true;
            if (level().isClientSide) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    try {
                        Dialog dialog = DialogSystem.getDialog(this);
                        if (dialog != null && dialog.canStart()) {
                            DialogClientHelper.openDialog(this, dialog);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }

            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if (isTamed() && getOwnerUUID() != null && player.getUUID().equals(getOwnerUUID())) {

            if (hand == InteractionHand.MAIN_HAND &&
                    (itemstack.isEmpty() || itemstack.getItem() == Items.STICK)) {

                if (isSleeping() || isSleepAnimPlaying() || isWakeAnimPlaying()) {
                    if (!this.level().isClientSide) {
                        player.displayClientMessage(Component.translatable("message.fantastikmod.kitsune.asleep_hint"), true);
                    }
                    return InteractionResult.FAIL;
                }
                if (itemstack.getItem() == Items.STICK && !player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                cycleAIMode();
                player.displayClientMessage(Component.literal(getAIModeMessage(player)), true);

                if (getAIMode() != AI_SIT && (isSitting() || isSitAnimPlaying())) {
                    if (isSitAnimPlaying()) {
                        setSitAnimPlaying(false);
                    }
                    startStandAnimation();
                }
                else if (getAIMode() == AI_SIT && !isSitting() && !isSitAnimPlaying()) {
                    startSitAnimation();
                }

                return InteractionResult.sidedSuccess(level().isClientSide);
            }
        }
        else if (itemstack.is(ModItems.TOFU.get()) && !this.isTamed() && !this.isBaby()) {
            return handleTaming(player, itemstack);
        } else if (itemstack.is(ModItems.TOFU.get()) && this.isTamed()) {
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            if (this.random.nextInt(8) == 0) {
                sendTofuMessage(player);
            }
            if (tofuBlessingGoal != null) {
                tofuBlessingGoal.activate();
            }
            spawnTamingParticles();
            return InteractionResult.sidedSuccess(level().isClientSide);
        } else if (isFood(itemstack) && this.isBaby()) {
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            if (this.random.nextInt(3) == 0) {
                growTime += 1200;
            }
            if (this.getHealth() < this.getMaxHealth()) {
                this.heal(2.0F);
            }
            spawnTamingParticles();
            return InteractionResult.sidedSuccess(level().isClientSide);
        } else if (isFood(itemstack) && this.isTamed() && this.getHealth() < this.getMaxHealth()) {
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            this.heal(4.0F);
            spawnTamingParticles();
            return InteractionResult.sidedSuccess(level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    private boolean isOwner(Player player) {
        try {
            UUID ownerUUID = getOwnerUUID();
            return ownerUUID != null && ownerUUID.equals(player.getUUID());
        } catch (Exception e) {
            return false;
        }
    }


    private void cycleAIMode() {
        int newMode = (getAIMode() + 1) % 3;
        setAIMode(newMode);
    }


    private String getAIModeMessage(Player player) {
        switch (getAIMode()) {
            case AI_FOLLOW -> { return Component.translatable("message.fantastikmod.kitsune.mode.follow").getString(); }
            case AI_SIT -> { return Component.translatable("message.fantastikmod.kitsune.mode.sit").getString(); }
            case AI_WANDER -> { return Component.translatable("message.fantastikmod.kitsune.mode.wander").getString(); }
            default -> { return Component.translatable("message.fantastikmod.kitsune.mode.unknown").getString(); }
        }
    }

    private InteractionResult handleTaming(Player player, ItemStack itemstack) {
        if (!player.getAbilities().instabuild) {
            itemstack.shrink(1);
        }

        int newProgress = getTamingProgress() + 1;
        setTamingProgress(newProgress);
        spawnTamingParticles();

        if ((newProgress >= MIN_TAMING_TOFU && newProgress <= MAX_TAMING_TOFU &&
                random.nextInt(MAX_TAMING_TOFU - newProgress + 1) == 0) ||
                newProgress > MAX_TAMING_TOFU) {
            tame(player);
            return InteractionResult.sidedSuccess(level().isClientSide);
        }

        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    private void tame(Player player) {
        setTamed(true);
        setTamingProgress(0);
        setAngry(false);
        setHitCount(0);
        setOwnerUUID(player.getUUID());


        if (this.getCustomName() == null || this.getCustomName().getString().isEmpty()) {
            setRandomName();
        }

        sendTamingMessage(player);
        spawnTamingSuccessParticles();
        playSound(ModSounds.KITSUNE_IDLE1.get(), 1.0F, 1.0F);

        if (!level().isClientSide) {
            this.setTamed(true);
        }
    }

    private void sendTamingMessage(Player player) {
        if (!level().isClientSide) {
            Component message = getTamingMessage(player);
            player.sendSystemMessage(message);
        }
    }


    @Override
    public void die(DamageSource cause) {
        if (!this.level().isClientSide && isTamed()) {
            LivingEntity owner = getOwner();
            if (owner instanceof Player player) {
                Component message = getDeathMessage(player);
                player.sendSystemMessage(message);
            }
        }
        super.die(cause);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<KitsuneLightEntity> event) {
        if (isSleeping()) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("sleep_idle"));
            return PlayState.CONTINUE;
        }

        if (isYawning() && isSitting()) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("sit_yawns"));
            return PlayState.CONTINUE;
        }

        if (isLaughing()) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("funny"));
            return PlayState.CONTINUE;
        }

        if (isDeadOrDying()) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("death"));
            return PlayState.CONTINUE;
        }

        if (swinging) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("attack"));
            return PlayState.CONTINUE;
        }

        if (isSitting()) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("sit_idle"));
            return PlayState.CONTINUE;
        }

        if (walkAnimation.isMoving() && canMove()) {
            if (isAngry() || isRunningFromCreeper() || getDeltaMovement().horizontalDistanceSqr() > 0.015D) {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("run"));
            } else {
                event.getController().setAnimation(RawAnimation.begin().thenPlay("walk"));
            }
            return PlayState.CONTINUE;
        }

        event.getController().setAnimation(RawAnimation.begin().thenPlay("idle"));
        return PlayState.CONTINUE;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new KitsuneLightGoal.KitsuneEscapeWaterGoal(this, 1.2D));
        this.goalSelector.addGoal(1, new KitsuneLightGoal.AvoidCreeperGoal(this, 1.0D, 1.8D));
        this.goalSelector.addGoal(2, new KitsuneLightGoal.KitsuneEscapeGoal(this, 1.8D));

        this.tofuBlessingGoal = new KitsuneTofuBlessingGoal(this);
        this.goalSelector.addGoal(2, tofuBlessingGoal);

        if (!this.isBaby()) {
            this.goalSelector.addGoal(3, new KitsuneLightGoal.KitsuneAttackGoal(this, 1.2D, true));
            this.goalSelector.addGoal(4, new KitsuneLightGoal.DefendVillagersGoal(this));
            this.goalSelector.addGoal(5, new KitsuneLightGoal.ProtectPlayersGoal(this));
        }

        this.goalSelector.addGoal(6, new KitsuneLightGoal.InteractWithVillagersGoal(this));
        this.goalSelector.addGoal(9, new KitsuneLightGoal.SitGoal(this));
        this.goalSelector.addGoal(8, new KitsuneLightGoal.SleepGoal(this));
        this.goalSelector.addGoal(7, new KitsuneExplodeCreepersGoal(this));
        this.goalSelector.addGoal(9, new KitsuneLightGoal.FollowOwnerGoal(this, 1.0D, 3.0F, 10.0F));
        this.goalSelector.addGoal(8, new KitsuneLightGoal.WanderGoal(this, 1.0D));
        this.goalSelector.addGoal(11, new TemptGoal(this, 1.20, Ingredient.of(ModItems.TOFU.get()), false));
        this.goalSelector.addGoal(12, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(13, new KitsuneLightGoal.SocialBehaviorGoal(this));


        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D) {
            @Override
            public boolean canUse() {
                return super.canUse() && random.nextInt(3) == 0;
            }
        });

        this.goalSelector.addGoal(15, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(16, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(15, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(15, new LookAtPlayerGoal(this, Animal.class, 6.0F));

        if (!this.isBaby()) {
            this.targetSelector.addGoal(1, new KitsuneLightGoal.KitsuneHurtByTargetGoal(this));
            this.targetSelector.addGoal(2, new KitsuneLightGoal.DefendOwnerGoal(this));
            this.targetSelector.addGoal(3, new KitsuneLightGoal.KitsuneDefendVillagersTargetGoal(this));
        }
    }

    public int getAIMode() { return entityData.get(DATA_AI_MODE); }
    public void setAIMode(int mode) { entityData.set(DATA_AI_MODE, mode); }

    public boolean isSitting() { return entityData.get(DATA_IS_SITTING); }
    public void setSitting(boolean sitting) {
        entityData.set(DATA_IS_SITTING, sitting);
        if (sitting && !isOffended()) {
            sitTimer = 60 + random.nextInt(120);
        } else if (!sitting) {
            sitTimer = 0;
        }
    }

    public boolean isSleeping() { return entityData.get(DATA_IS_SLEEPING); }
    public void setSleeping(boolean sleeping) {
        entityData.set(DATA_IS_SLEEPING, sleeping);
        if (sleeping) {
            sleepTimer = 400 + random.nextInt(400);
        } else {
            sleepTimer = 0;
        }
    }


    public boolean isYawning() { return entityData.get(DATA_IS_YAWNING); }
    public void setYawning(boolean yawning) { entityData.set(DATA_IS_YAWNING, yawning); }

    public boolean isSitAnimPlaying() { return entityData.get(DATA_SIT_ANIM_PLAYING); }
    public void setSitAnimPlaying(boolean playing) { entityData.set(DATA_SIT_ANIM_PLAYING, playing); }

    public boolean isStandAnimPlaying() { return entityData.get(DATA_STAND_ANIM_PLAYING); }
    public void setStandAnimPlaying(boolean playing) { entityData.set(DATA_STAND_ANIM_PLAYING, playing); }

    public boolean isSleepAnimPlaying() { return entityData.get(DATA_SLEEP_ANIM_PLAYING); }
    public void setSleepAnimPlaying(boolean playing) { entityData.set(DATA_SLEEP_ANIM_PLAYING, playing); }

    public boolean isWakeAnimPlaying() { return entityData.get(DATA_WAKE_ANIM_PLAYING); }
    public void setWakeAnimPlaying(boolean playing) { entityData.set(DATA_WAKE_ANIM_PLAYING, playing); }

    public int getHitCount() { return entityData.get(DATA_HIT_COUNT); }
    public void setHitCount(int count) { entityData.set(DATA_HIT_COUNT, count); }

    public boolean isAngry() { return entityData.get(DATA_IS_ANGRY); }
    public void setAngry(boolean angry) { entityData.set(DATA_IS_ANGRY, angry); }

    public boolean isLaughing() { return entityData.get(DATA_IS_LAUGHING); }
    public void setLaughing(boolean laughing) { entityData.set(DATA_IS_LAUGHING, laughing); }

    public int getVariant() { return entityData.get(DATA_VARIANT); }
    public void setVariant(int variant) { entityData.set(DATA_VARIANT, variant); }

    public int getEyeVariant() { return entityData.get(DATA_EYE_VARIANT); }
    public void setEyeVariant(int eyeVariant) { entityData.set(DATA_EYE_VARIANT, eyeVariant); }

    public int getTamingProgress() { return entityData.get(DATA_TAMING_PROGRESS); }
    public void setTamingProgress(int progress) { entityData.set(DATA_TAMING_PROGRESS, progress); }

    public boolean isTamed() { return entityData.get(DATA_TAMED); }
    public void setTamed(boolean tamed) { entityData.set(DATA_TAMED, tamed); }

    public boolean isRunningFromCreeper() { return runningFromCreeperTicks > 0; }
    public void setRunningFromCreeper(boolean running) {
        runningFromCreeperTicks = running ? 40 : 0;
    }

    public int getLaughCooldown() { return laughCooldown; }
    public void triggerLaughAnimation() {
        if (laughCooldown == 0) {
            laughCooldown = 80; // 4 секунды
            setLaughing(true);

            if (!level().isClientSide) {
                playLaughSound();
            }
        }
    }

    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNER_UUID).orElse(null);
    }

    public void setOwnerUUID(UUID uuid) {
        this.entityData.set(DATA_OWNER_UUID, Optional.ofNullable(uuid));
        this.ownerUUID = uuid; // для обратной совместимости
    }
    public LivingEntity getOwner() {
        return ownerUUID != null ? level().getPlayerByUUID(ownerUUID) : null;
    }

    public boolean isOffended() { return entityData.get(DATA_IS_OFFENDED); }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 18)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        if (stack.is(ModItems.TOFU.get())) {
            return true;
        }

        if (this.isBaby() || this.isTamed()) {
            return stack.is(Items.RABBIT) || stack.is(Items.COOKED_RABBIT) ||
                    stack.is(Items.CHICKEN) || stack.is(Items.COOKED_CHICKEN) ||
                    stack.is(Items.COD) || stack.is(Items.COOKED_COD) ||
                    stack.is(Items.SALMON) || stack.is(Items.COOKED_SALMON) ||
                    stack.is(Items.SWEET_BERRIES) || stack.is(Items.GLOW_BERRIES);
        }

        return false;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType reason, @Nullable SpawnGroupData spawnData,
                                        @Nullable CompoundTag dataTag) {
        if (reason != MobSpawnType.BREEDING) {
            setVariant(level.getRandom().nextInt(6));
            setEyeVariant(level.getRandom().nextInt(8));
        }
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }


    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("GrowTime", growTime);
        compound.putInt("Variant", getVariant());
        compound.putInt("HitCount", getHitCount());
        compound.putBoolean("IsAngry", isAngry());
        compound.putBoolean("IsLaughing", isLaughing());
        compound.putInt("TamingProgress", getTamingProgress());
        compound.putBoolean("Tamed", isTamed());
        compound.putInt("AIMode", getAIMode());
        compound.putBoolean("Sitting", isSitting());
        compound.putBoolean("Sleeping", isSleeping());
        compound.putBoolean("Yawning", isYawning());
        compound.putInt("EyeVariant", getEyeVariant());
        compound.putBoolean("Offended", isOffended());
        if (ownerUUID != null) {
            compound.putUUID("Owner", ownerUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("GrowTime")) {
            growTime = compound.getInt("GrowTime");
        }
        if (compound.contains("Variant")) setVariant(compound.getInt("Variant"));
        if (compound.contains("HitCount")) setHitCount(compound.getInt("HitCount"));
        if (compound.contains("IsAngry")) setAngry(compound.getBoolean("IsAngry"));
        if (compound.contains("IsLaughing")) setLaughing(compound.getBoolean("IsLaughing"));
        if (compound.contains("TamingProgress")) setTamingProgress(compound.getInt("TamingProgress"));
        if (compound.contains("Tamed")) setTamed(compound.getBoolean("Tamed"));
        if (compound.contains("AIMode")) setAIMode(compound.getInt("AIMode"));
        if (compound.contains("Sitting")) setSitting(compound.getBoolean("Sitting"));
        if (compound.contains("Sleeping")) setSleeping(compound.getBoolean("Sleeping"));
        if (compound.contains("Yawning")) setYawning(compound.getBoolean("Yawning"));
        if (compound.contains("EyeVariant")) setEyeVariant(compound.getInt("EyeVariant"));
        if (compound.hasUUID("Owner")) setOwnerUUID(compound.getUUID("Owner"));

    }

    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
        if (this.getPersistentData().getBoolean("maskDisappearing")) {
            return;
        }
        super.playSound(sound, volume, pitch);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.getPersistentData().getBoolean("maskDisappearing")) {
            return false;
        }
        boolean wasSleeping = this.isSleeping();
        Player attacker = null;
        if (source.getEntity() instanceof Player player) {
            attacker = player;
        }

        int oldHitCount = getHitCount();
        if (source.getEntity() instanceof Player) {
            setHitCount(oldHitCount + 1);
        }

        if (!this.level().isClientSide && amount > 0) {
            resetRestingAnimations();
        }

        if (source.getDirectEntity() instanceof AbstractArrow && dodgeCooldown <= 0) {
            if (random.nextFloat() < 0.3f) {
                dodgeArrow((Projectile) source.getDirectEntity());
                dodgeCooldown = 60;
                return false;
            }
        }
        boolean hurt = super.hurt(source, amount);

        if (!this.level().isClientSide && wasSleeping && attacker != null) {
            sendWakeUpMessage(attacker);
        }

        if (!this.level().isClientSide && this.isTamed() && attacker != null &&
                attacker.getUUID().equals(this.getOwnerUUID()) && !wasSleeping) {

            if (this.random.nextInt(5) == 0) {
                sendOwnerHurtMessage(attacker);
            }
        }

        if (!this.level().isClientSide && !this.isTamed() && attacker != null) {
            if (oldHitCount == 0 && getHitCount() == 1) {
                sendWildHurtMessage(attacker);
            } else if (oldHitCount == 1 && getHitCount() == 2) {
                sendAngryWildMessage(attacker);
                setAngry(true);
            }
        }

        return hurt;
    }


    @Override
    public boolean doHurtTarget(Entity target) {
        if (this.isBaby()) {
            return false;
        }

        if (!this.level().isClientSide) {
            resetRestingAnimations();
        }
        return super.doHurtTarget(target);
    }
    public void playIdleSound() {
        if (!level().isClientSide) {
            SoundEvent[] idleSounds = {ModSounds.KITSUNE_IDLE1.get(), ModSounds.KITSUNE_IDLE2.get(),
                    ModSounds.KITSUNE_IDLE3.get(), ModSounds.KITSUNE_IDLE4.get()};
            playSound(idleSounds[random.nextInt(idleSounds.length)], 0.6F, 1.0F);
        }
    }

    public void playLaughSound() {
        if (!level().isClientSide) {
            SoundEvent[] laughSounds = {ModSounds.KITSUNE_LAUGH1.get(), ModSounds.KITSUNE_LAUGH2.get(),
                    ModSounds.KITSUNE_LAUGH3.get()};
            playSound(laughSounds[random.nextInt(laughSounds.length)], 0.8F, 1.0F);
        }
    }

    public void playAngrySound() {
        if (!level().isClientSide) {
            playSound(ModSounds.KITSUNE_ANGRY1.get(), 0.7F, 0.9F + random.nextFloat() * 0.2F);
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        SoundEvent[] hurtSounds = {ModSounds.KITSUNE_HURT1.get(), ModSounds.KITSUNE_HURT2.get()};
        return hurtSounds[random.nextInt(hurtSounds.length)];
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.KITSUNE_HURT1.get();
    }

    private void dodgeArrow(Projectile arrow) {
        if (level().isClientSide) return;
        Vec3 arrowDirection = arrow.getDeltaMovement().normalize();
        Vec3 dodgeDirection = new Vec3(-arrowDirection.z, 0, arrowDirection.x).normalize();
        if (random.nextBoolean()) dodgeDirection = dodgeDirection.scale(-1);
        setDeltaMovement(dodgeDirection.x * 1.5D, 0.3D, dodgeDirection.z * 1.5D);
        spawnDodgeParticles();
    }

    private void useFoxFire() {
        if (level().isClientSide || getTarget() == null) return;
        LivingEntity target = getTarget();
        double distance = distanceToSqr(target);
        if (distance >= 16.0D && distance <= 144.0D && random.nextFloat() < 0.2f) {
            spawnFoxFire(target);
            foxFireCooldown = 100;
        }
    }

    private void useMagicAbilities() {
        if (level().isClientSide) return;

        this.createExplosionProtectionField();

        List<Creeper> nearbyCreepers = this.level().getEntitiesOfClass(Creeper.class,
                this.getBoundingBox().inflate(10.0D));

        if (!nearbyCreepers.isEmpty()) {
            this.protectFromCreepers();
        }

        this.healAllies();

        this.applySelfBuffs();
    }

    private void protectFromCreepers() {
        List<Creeper> nearbyCreepers = this.level().getEntitiesOfClass(Creeper.class,
                this.getBoundingBox().inflate(12.0D));

        for (Creeper creeper : nearbyCreepers) {
            boolean shouldProtect = creeper.isIgnited() ||
                    creeper.getSwellDir() > 0 ||
                    this.distanceToSqr(creeper) < 36.0D;

            if (shouldProtect) {
                List<KitsuneLightEntity> nearbyKitsune = this.level().getEntitiesOfClass(
                        KitsuneLightEntity.class, this.getBoundingBox().inflate(16.0D));

                for (KitsuneLightEntity kitsune : nearbyKitsune) {
                    if (!kitsune.hasEffect(MobEffects.DAMAGE_RESISTANCE)) {
                        kitsune.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 1));
                    }
                    if (!kitsune.hasEffect(MobEffects.REGENERATION)) {
                        kitsune.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0));
                    }
                }
                this.spawnMagicParticles();

                if (!this.level().isClientSide) {
                    this.playSound(ModSounds.KITSUNE_IDLE1.get(), 0.8F, 1.2F);
                }
                break;
            }
        }
    }

    private void healAllies() {
        if (this.getHealth() < this.getMaxHealth() * 0.7f) {
            this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0));
        }
        List<KitsuneLightEntity> nearbyKitsune = this.level().getEntitiesOfClass(
                KitsuneLightEntity.class, this.getBoundingBox().inflate(10.0D));

        for (KitsuneLightEntity kitsune : nearbyKitsune) {
            if (kitsune.getHealth() < kitsune.getMaxHealth() * 0.5f) {
                kitsune.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
            }
        }
    }

    private void applySelfBuffs() {
        if (this.random.nextFloat() < 0.1f) {
            MobEffect[] possibleBuffs = {
                    MobEffects.MOVEMENT_SPEED,
                    MobEffects.DAMAGE_BOOST,
                    MobEffects.DAMAGE_RESISTANCE
            };

            MobEffect selectedBuff = possibleBuffs[this.random.nextInt(possibleBuffs.length)];
            this.addEffect(new MobEffectInstance(selectedBuff, 300, 0));
        }
    }

    private void createExplosionProtectionField() {
        List<Creeper> nearbyCreepers = this.level().getEntitiesOfClass(Creeper.class,
                this.getBoundingBox().inflate(12.0D)); // Увеличиваем радиус

        for (Creeper creeper : nearbyCreepers) {
            boolean shouldProtect = creeper.isIgnited() ||
                    creeper.getSwellDir() > 0 ||
                    this.getTarget() == creeper ||
                    this.distanceToSqr(creeper) < 25.0D;

            if (shouldProtect) {
                CompoundTag persistentData = creeper.getPersistentData();
                persistentData.putBoolean("KitsuneProtectedExplosion", true);
                persistentData.putLong("KitsuneProtectionTime", this.level().getGameTime() + 300);
            }
        }
    }
    private void spawnTamingParticles() {
        if (this.level().isClientSide) {
            for(int i = 0; i < 7; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;
                this.level().addParticle(ParticleTypes.HAPPY_VILLAGER,
                        this.getRandomX(1.0D),
                        this.getRandomY() + 0.5D,
                        this.getRandomZ(1.0D),
                        d0, d1, d2);
            }
        }
    }

    private void spawnTamingSuccessParticles() {
        if (this.level().isClientSide) {
            for(int i = 0; i < 15; ++i) {
                this.level().addParticle(ParticleTypes.FIREWORK,
                        this.getRandomX(1.5D),
                        this.getRandomY() + 1.0D,
                        this.getRandomZ(1.5D),
                        this.random.nextGaussian() * 0.05D,
                        this.random.nextGaussian() * 0.05D + 0.2D,
                        this.random.nextGaussian() * 0.05D);
            }
        }
    }

    private void spawnDodgeParticles() {
        if (this.level().isClientSide) {
            for(int i = 0; i < 8; ++i) {
                this.level().addParticle(ParticleTypes.CLOUD,
                        this.getRandomX(0.5D),
                        this.getRandomY(),
                        this.getRandomZ(0.5D),
                        0, 0.1, 0);
            }
        }
    }

    private void spawnFoxFire(LivingEntity target) {
        if (this.level().isClientSide) return;

        for(int i = 0; i < 3; ++i) {
            double dx = target.getX() - this.getX();
            double dy = target.getY(0.5) - this.getY(0.5);
            double dz = target.getZ() - this.getZ();

            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        this.getX() + (dx * 0.25),
                        this.getY(0.5) + (dy * 0.25),
                        this.getZ() + (dz * 0.25),
                        5, 0.2, 0.2, 0.2, 0.05);
            }
        }

        target.hurt(this.damageSources().mobAttack(this), 3.0F);
        target.setSecondsOnFire(5);

        this.playSound(ModSounds.KITSUNE_ANGRY1.get(), 1.0F, 1.5F);
    }

    private void spawnLaughParticles() {
        if (this.level().isClientSide) {
            for(int i = 0; i < 3; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;

                this.level().addParticle(
                        ParticleTypes.HAPPY_VILLAGER,
                        this.getRandomX(1.0D),
                        this.getRandomY() + 0.5D,
                        this.getRandomZ(1.0D),
                        d0, d1, d2
                );
            }
        }
    }

    private void spawnMagicParticles() {
        if (this.level().isClientSide) {
            for(int i = 0; i < 10; ++i) {
                this.level().addParticle(ParticleTypes.ENCHANT,
                        this.getRandomX(1.5D),
                        this.getRandomY() + 0.5D,
                        this.getRandomZ(1.5D),
                        0, 0.1, 0);
            }
        }
    }

    private void spawnHealingParticles(KitsuneLightEntity target) {
        if (this.level().isClientSide) {
            for(int i = 0; i < 5; ++i) {
                this.level().addParticle(ParticleTypes.HAPPY_VILLAGER,
                        target.getRandomX(1.0D),
                        target.getRandomY() + 0.5D,
                        target.getRandomZ(1.0D),
                        0, 0.1, 0);
            }
        }
    }

    private void spawnBuffParticles() {
        if (this.level().isClientSide) {
            for(int i = 0; i < 8; ++i) {
                this.level().addParticle(ParticleTypes.ELECTRIC_SPARK,
                        this.getRandomX(1.0D),
                        this.getRandomY() + 0.5D,
                        this.getRandomZ(1.0D),
                        0, 0.05, 0);
            }
        }
    }

    public boolean canMove() {
        boolean cannotMove = isSitting() ||
                isSleeping() ||
                isSitAnimPlaying() ||
                isStandAnimPlaying() ||
                isSleepAnimPlaying() ||
                isWakeAnimPlaying() ||
                isDeadOrDying() ||
                !isAlive() ||
                isNoAi() ||
                isOffended();

        return !cannotMove;
    }



    public void resetRestingAnimations() {
        setSitting(false);
        setSleeping(false);
        setSitAnimPlaying(false);
        setStandAnimPlaying(false);
        setSleepAnimPlaying(false);
        setWakeAnimPlaying(false);
        setYawning(false);

        sitTimer = 0;
        sleepTimer = 0;
        animationTimer = 0;
        yawnTimer = 0;
        this.getNavigation().stop();
    }


    @Override
    public boolean canUpdate() {
        return super.canUpdate() && !isSleeping();
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (isSitting() || isSleeping() || isSitAnimPlaying() || isStandAnimPlaying() ||
                isSleepAnimPlaying() || isWakeAnimPlaying()) {
            this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
            return;
        }
        super.travel(travelVector);
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return !isSitting() && !isSleeping() && super.causeFallDamage(distance, damageMultiplier, source);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (isSitting() || isSleeping() || isSitAnimPlaying() || isStandAnimPlaying() ||
                isSleepAnimPlaying() || isWakeAnimPlaying()) {
            this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
            this.getNavigation().stop();
        }
    }

    private void tryToTeleportToOwner(LivingEntity owner) {
        for (int i = 0; i < 10; i++) {
            double x = owner.getX() + (this.random.nextDouble() - 0.5) * 8;
            double y = owner.getY() + this.random.nextInt(3) - 1;
            double z = owner.getZ() + (this.random.nextDouble() - 0.5) * 8;

            if (this.canTeleportTo(new BlockPos((int)x, (int)y, (int)z))) {
                this.teleportTo(x, y, z);
                this.getNavigation().stop();
                spawnTeleportParticles();
                break;
            }
        }
    }

    private boolean canTeleportTo(BlockPos pos) {
        return this.level().isEmptyBlock(pos) &&
                this.level().isEmptyBlock(pos.above()) &&
                this.level().getBlockState(pos.below()).isSolid();
    }

    private void spawnTeleportParticles() {
        if (this.level().isClientSide) {
            for (int i = 0; i < 15; i++) {
                this.level().addParticle(ParticleTypes.PORTAL,
                        this.getRandomX(1.0), this.getRandomY(), this.getRandomZ(1.0),
                        (this.random.nextDouble() - 0.5) * 2.0, -this.random.nextDouble(),
                        (this.random.nextDouble() - 0.5) * 2.0);
            }
        }
    }

    @Override
    protected void dropExperience() {
        if (this.shouldDropExperience()) {
            this.dropExperience(5 + this.random.nextInt(6));
        }
    }

    private void dropExperience(int experience) {
        super.dropExperience();
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int lootingLevel, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, lootingLevel, recentlyHit);

        if (this.random.nextFloat() < 0.7f) {
            this.spawnAtLocation(ModItems.GEMKITSUNE.get());
        }
    }

    @Override
    public Component getDisplayName() {
        if (this.hasCustomName()) {
            return this.getCustomName();
        }
        return Component.translatable("entity.fantastikmod.kitsune_light");
    }

    public boolean isTame() {
        return this.isTamed();
    }

    public void setTame(boolean tame) {
        this.setTamed(tame);
        if (tame && this.getOwnerUUID() != null) {
            this.setOwnerUUID(this.getOwnerUUID());
        }
    }

    private static final String[] KITSUNE_NAMES = {
            "Akari", "Yuki", "Hikari", "Kiko", "Sakura",
            "Ren", "Kai", "Haru", "Aiko", "Mitsuki",
            "Kitsu", "Yoru", "Hoshi", "Sora", "Kaze",
            "Tsuki", "Hana", "Rin", "Kuma", "Tora",
            "Shiro", "Kuro", "Momo", "Botan", "Fuji",
            "Asa", "Yume", "Kokoro", "Aoi", "Midori",
            "Akane", "Sumire", "Ran", "Shinju", "Kaguya",
            "Tamamo", "Kuzunoha", "Nogitsune", "Myobu"
    };

    private void setRandomName() {
        if (this.getCustomName() == null) {
            String name = KITSUNE_NAMES[this.random.nextInt(KITSUNE_NAMES.length)];
            this.setCustomName(Component.literal(name));
        }
    }

    public KitsuneLightEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean isCustomNameVisible() {
        return this.hasCustomName();
    }

    @Override
    public boolean hasCustomName() {
        return this.getCustomName() != null && !this.getCustomName().getString().isEmpty();
    }

    public String getMobTypeName() {
        return "kitsune_light";
    }

    public String getOwnerName() {
        if (this.getOwner() != null) {
            return this.getOwner().getDisplayName().getString();
        }
        return "";
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnType) {
        BlockPos pos = this.blockPosition();
        BlockState belowState = level.getBlockState(pos.below());

        if (belowState.getFluidState().is(FluidTags.WATER)) {
            return false;
        }

        return super.checkSpawnRules(level, spawnType);
    }

    private Component getTamingMessage(Player player) {
        return KitsunePhrases.getTamedMessage();
    }

    private void sendTofuMessage(Player player) {
        player.sendSystemMessage(KitsunePhrases.getFeedMessage());
    }

    private Component getDeathMessage(Player player) {
        return KitsunePhrases.getDeathMessage();
    }
    private void sendWakeUpMessage(Player player) {
        player.sendSystemMessage(KitsunePhrases.getWakeUpMessage());
    }
    private void sendWildHurtMessage(Player player) {
        player.sendSystemMessage(KitsunePhrases.getHurtMessage());
    }
    private void sendAngryWildMessage(Player player) {
        player.sendSystemMessage(KitsunePhrases.getAngryMessage());
    }
    private void sendOwnerHurtMessage(Player player) {
        player.sendSystemMessage(KitsunePhrases.getOwnerHurtMessage());
    }
    private void sendAngryTofuMessage(Player player) {
        player.sendSystemMessage(KitsunePhrases.getAngryTofuMessage());
    }
    private void sendForgivenessMessage(Player player) {
        player.sendSystemMessage(KitsunePhrases.getForgivenessMessage());
    }
    public void sayToPlayer(Player player, Component message) {
        if (!level().isClientSide) {
            player.sendSystemMessage(
                    Component.literal("<" + this.getDisplayName().getString() + "> ").append(message)
            );
        }
    }
    private int confusedIndex = 0;
}
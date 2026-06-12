package net.lisalaf.fantastikmod.entity.custom;

import net.lisalaf.fantastikmod.dialog.Dialog;
import net.lisalaf.fantastikmod.dialog.DialogScreen;
import net.lisalaf.fantastikmod.dialog.DialogSystem;
import net.lisalaf.fantastikmod.dialog.mobs.MoonDeerDialog;
import net.lisalaf.fantastikmod.dialog.mobs.WildMoonDeerDialog;
import net.lisalaf.fantastikmod.entity.ai.MoonDeerGoal;
import net.lisalaf.fantastikmod.entity.ai.moondeer.MoonDeerAttackGoal;
import net.lisalaf.fantastikmod.entity.client.MoonDeerRenderer;
import net.lisalaf.fantastikmod.entity.phrases.MoonDeerPhrases;
import net.lisalaf.fantastikmod.fantastikmod;
import net.lisalaf.fantastikmod.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.minecraft.world.entity.ai.control.LookControl;


import java.util.UUID;

public class MoonDeerEntity extends Animal implements GeoEntity, PlayerRideable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Data Parameters
    private static final EntityDataAccessor<Boolean> DATA_TAMED = SynchedEntityData.defineId(MoonDeerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_AI_MODE = SynchedEntityData.defineId(MoonDeerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_DRINKING = SynchedEntityData.defineId(MoonDeerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_RUNNING = SynchedEntityData.defineId(MoonDeerEntity.class, EntityDataSerializers.BOOLEAN);

    // AI Modes
    public static final int AI_FOLLOW = 0;
    public static final int AI_WANDER = 1;

    // Таймеры
    private int drinkCooldown = 0;
    private int drinkAnimationTimer = 0;
    private int runCooldown = 0;
    private UUID ownerUUID;
    private int jumpCooldown = 0;

    // Константы для анимации питья
    private static final int DRINK_ANIMATION_DURATION = 72;
    private static final int DRINK_COOLDOWN = 1200;

    public MoonDeerEntity(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.jumpControl = new MoonDeerJumpControl();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TAMED, false);
        this.entityData.define(DATA_AI_MODE, AI_FOLLOW);
        this.entityData.define(DATA_DRINKING, false);
        this.entityData.define(DATA_RUNNING, false);
    }

    @Override
    public void tick() {
        super.tick();



        if (!this.level().isClientSide) {
            updateTimers();
            handleWaterWalking();
            handleDrinkBehavior();
            handleRunning();
        }



        handleAnimations();
    }

    private void updateTimers() {
        if (drinkCooldown > 0) drinkCooldown--;
        if (drinkAnimationTimer > 0) {
            drinkAnimationTimer--;
            if (drinkAnimationTimer <= 0) {
                setDrinking(false);
            }
        }
        if (runCooldown > 0) runCooldown--;
        if (jumpCooldown > 0) jumpCooldown--;
    }

    // === ХОЖДЕНИЕ ПО ВОДЕ НОЧЬЮ ===
    private void handleWaterWalking() {
        if (this.level().isNight() && !isDrinking()) {
            BlockPos pos = this.blockPosition();
            if (this.level().getBlockState(pos).getBlock() == Blocks.WATER ||
                    this.level().getFluidState(pos).getType() == Fluids.WATER) {

                this.setNoGravity(true);
                double targetY = Math.floor(this.getY()) + 0.9D;
                if (this.getY() < targetY) {
                    this.setDeltaMovement(this.getDeltaMovement().x, 0.1D, this.getDeltaMovement().z);
                } else {
                    this.setDeltaMovement(this.getDeltaMovement().x, 0D, this.getDeltaMovement().z);
                }

                if (this.tickCount % 3 == 0 && this.level().isClientSide) {
                    for (int i = 0; i < 5; i++) {
                        this.level().addParticle(ParticleTypes.GLOW,
                                this.getRandomX(0.8D),
                                this.getY() + 0.1D,
                                this.getRandomZ(0.8D),
                                (this.random.nextDouble() - 0.5) * 0.1D,
                                0.02D,
                                (this.random.nextDouble() - 0.5) * 0.1D);
                    }
                }
            } else {
                this.setNoGravity(false);
            }
        } else {
            this.setNoGravity(false);
        }
    }

    // === ПОВЕДЕНИЕ ПИТЬЯ ВОДЫ ===
    private void handleDrinkBehavior() {
        if (drinkCooldown > 0 || isDrinking()) return;

        if (isNearWater() && !this.level().isNight() && this.random.nextFloat() < 0.3f) {
            startDrinkAnimation();
            drinkCooldown = DRINK_COOLDOWN + random.nextInt(600);
        }
    }

    private boolean isNearWater() {
        BlockPos pos = this.blockPosition();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockPos checkPos = pos.offset(x, -1, z);
                if (this.level().getBlockState(checkPos).getBlock() == Blocks.WATER ||
                        this.level().getFluidState(checkPos).getType() == Fluids.WATER) {
                    return true;
                }
            }
        }
        return false;
    }

    private void startDrinkAnimation() {
        setDrinking(true);
        drinkAnimationTimer = DRINK_ANIMATION_DURATION;
        this.getNavigation().stop();
        this.playSound(SoundEvents.GENERIC_DRINK, 0.7F, 0.8F + this.random.nextFloat() * 0.4F);

        if (this.level().isClientSide) {
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            if (isDrinking() && level().isClientSide) {
                                for (int i = 0; i < 8; i++) {
                                    level().addParticle(ParticleTypes.DRIPPING_WATER,
                                            getRandomX(0.5D),
                                            getY() + 0.1D,
                                            getRandomZ(0.5D),
                                            0, 0.05D, 0);
                                }
                            }
                        }
                    },
                    1000
            );
        }
    }

    // === СИСТЕМА БЕГА ===
    private void handleRunning() {
        if (isRunning() && runCooldown <= 0) {
            setRunning(false);
        }
    }

    private void toggleRunning() {
        if (runCooldown <= 0) {
            setRunning(!isRunning());
            runCooldown = 10;
        }
    }

    // === ПРИРУЧЕНИЕ И УПРАВЛЕНИЕ ===
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        // ЛЕЧЕНИЕ ЯГОДАМИ
        if (itemstack.is(ModItems.MOON_CROWBERRY.get())) {
            if (!isTamed()) {
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 0.9F + random.nextFloat() * 0.2F);

                Component message = getWildFeedingMessage(player);
                player.displayClientMessage(message, true);

                return InteractionResult.sidedSuccess(level().isClientSide);

            } else {
                // Прирученный олень - лечение
                if (this.getHealth() < this.getMaxHealth()) {
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }

                    // Восстановление здоровья
                    this.heal(4.0F); // 2 сердца = 4 единицы здоровья

                    spawnHealingParticles();
                    this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);

                    // Сообщение для прирученного оленя
                    Component message = getTamedHealingMessage(player);
                    player.displayClientMessage(message, true);

                    return InteractionResult.sidedSuccess(level().isClientSide);
                } else {
                    // Если здоровье полное
                    Component message = getFullHealthMessage(player);
                    player.displayClientMessage(message, true);
                    return InteractionResult.PASS;
                }
            }
        }

        // ДИАЛОГ С ПРИРУЧЕННЫМ ОЛЕНЕМ (Shift + ПКМ без предмета)
        if (itemstack.isEmpty() && hand == InteractionHand.MAIN_HAND && player.isShiftKeyDown()) {

            if (level().isClientSide) {
                try {
                    Dialog dialog = DialogSystem.getDialog(this);
                    if (dialog != null) {
                        Minecraft.getInstance().setScreen(new DialogScreen(this, dialog));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }

        if (!isTamed() && itemstack.is(ModItems.MOONMASCOT.get())) {
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            tame(player);
            sendTamingMessage(player);
            return InteractionResult.sidedSuccess(level().isClientSide);
        }

        if (isTamed() && player.getUUID().equals(getOwnerUUID()) && itemstack.is(net.minecraft.world.item.Items.STICK)) {
            cycleAIMode();
            player.displayClientMessage(Component.literal(getAIModeMessage(player)), true);
            this.playSound(SoundEvents.ITEM_FRAME_ROTATE_ITEM, 0.7F, 1.0F);
            return InteractionResult.sidedSuccess(level().isClientSide);
        }

        if (isTamed() && player.getUUID().equals(getOwnerUUID())) {
            if (player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
                player.displayClientMessage(Component.literal(getAIModeMessage(player)), true);
                return InteractionResult.sidedSuccess(level().isClientSide);
            }

            if (!player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
                if (this.isVehicle()) {
                    toggleRunning();
                    player.displayClientMessage(Component.literal(isRunning() ?
                            (isRussianLanguage(player) ? "Режим: Бег" : "Mode: Running") :
                            (isRussianLanguage(player) ? "Режим: Ходьба" : "Mode: Walking")), true);
                } else {
                    player.startRiding(this);
                }
                return InteractionResult.sidedSuccess(level().isClientSide);
            }
        }

        return super.mobInteract(player, hand);
    }

    private void tame(Player player) {
        setTamed(true);
        setOwnerUUID(player.getUUID());
        sendTamingMessage(player);
        spawnTamingParticles();
        this.playSound(SoundEvents.PLAYER_LEVELUP, 1.0F, 1.0F);
    }

    private void spawnHealingParticles() {
        if (level().isClientSide) {
            for(int i = 0; i < 12; ++i) {
                level().addParticle(ParticleTypes.GLOW,
                        getRandomX(0.8D), getRandomY() + 0.5D, getRandomZ(0.8D),
                        (random.nextDouble() - 0.5) * 0.1D,
                        0.2D,
                        (random.nextDouble() - 0.5) * 0.1D);
            }
            for(int i = 0; i < 8; ++i) {
                level().addParticle(ParticleTypes.HEART,
                        getRandomX(0.6D), getRandomY() + 0.8D, getRandomZ(0.6D),
                        (random.nextDouble() - 0.5) * 0.05D,
                        0.3D,
                        (random.nextDouble() - 0.5) * 0.05D);
            }
        }
    }

    private void sendTamingMessage(Player player) {
        if (!level().isClientSide) {
            player.sendSystemMessage(MoonDeerPhrases.getTamedMessage());
        }
    }


    // === СМЕРТЬ ===
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

    // === AI И РЕЖИМЫ ===
    private void cycleAIMode() {
        int newMode = (getAIMode() + 1) % 2;
        setAIMode(newMode);

        if (this.level().isClientSide) {
            this.playSound(SoundEvents.UI_BUTTON_CLICK, 0.5F, 1.0F);
        }
    }

    private void playSound(Holder.Reference<SoundEvent> uiButtonClick, float volume, float pitch) {
    }


    private String getAIModeMessage(Player player) {
        boolean isRussian = isRussianLanguage(player);
        switch (getAIMode()) {
            case AI_FOLLOW -> {
                return isRussian ? "Режим: Следование" : "Mode: Following";
            }
            case AI_WANDER -> {
                return isRussian ? "Режим: Бродить" : "Mode: Wandering";
            }
            default -> {
                return isRussian ? "Режим: Неизвестно" : "Mode: Unknown";
            }
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MoonDeerAttackGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0D));
        this.goalSelector.addGoal(2, new MoonDeerGoal(this, 1.0D, 5.0F, 12.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    // === ЕЗДА НА ОЛЕНЕ ===

    private class MoonDeerJumpControl extends JumpControl {
        public MoonDeerJumpControl() {
            super(MoonDeerEntity.this);
        }

        @Override
        public void jump() {
            this.jump = true;
        }

        @Override
        public void tick() {
            if (this.jump) {
                MoonDeerEntity.this.performCustomJump();
                this.jump = false;
            }
        }
    }


    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        return super.getDismountLocationForPassenger(passenger);
    }

    private void performCustomJump() {
        if (this.onGround() && jumpCooldown <= 0) {
            // Мощный прыжок вперед
            float jumpPower = 1.8F; // Увеличена базовая мощность прыжка

            if (isRunning()) {
                jumpPower *= 1.5F; // Усиленный прыжок в режиме бега
            }

            // Получаем направление взгляда всадника
            LivingEntity rider = this.getControllingPassenger();
            float yRot = rider != null ? rider.getYRot() : this.getYRot();
            float yRotRad = yRot * ((float)Math.PI / 180F);

            // Горизонтальная составляющая прыжка
            double jumpX = -Math.sin(yRotRad) * jumpPower * 1.0;
            double jumpZ = Math.cos(yRotRad) * jumpPower * 1.5;

            // Вертикальная составляющая
            double jumpY = jumpPower * 0.3;

            this.setDeltaMovement(jumpX, jumpY, jumpZ);
            jumpCooldown = 20; // 1 секунда кулдауна
            this.playSound(SoundEvents.HORSE_JUMP, 0.8F, 1.0F);
            this.hasImpulse = true;

        }
    }


    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        if (this.hasPassenger(passenger)) {
            float yRotRad = this.getYRot() * ((float)Math.PI / 180F);
            double xOffset = 0.0D;
            double zOffset = 0.2D;
            double worldXOffset = -Math.sin(yRotRad) * zOffset;
            double worldZOffset = Math.cos(yRotRad) * zOffset;

            Vec3 ridePosition = new Vec3(worldXOffset, this.getPassengersRidingOffset() + passenger.getMyRidingOffset(), worldZOffset);
            moveFunction.accept(passenger, this.getX() + ridePosition.x, this.getY() + ridePosition.y, this.getZ() + ridePosition.z);
        }
    }


    @Override
    public double getPassengersRidingOffset() {
        return this.getBbHeight() * 0.7D;
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof LivingEntity living ? living : null;
    }

    // === ПРЫЖКИ ===

    // === ДВИЖЕНИЕ ПРИ ЕЗДЕ ===
    @Override
    public void travel(Vec3 travelVector) {
        if (this.isVehicle() && this.getControllingPassenger() != null) {
            LivingEntity rider = this.getControllingPassenger();

            this.setYRot(rider.getYRot());
            this.yRotO = this.getYRot();
            this.setXRot(rider.getXRot() * 0.5F);
            this.setRot(this.getYRot(), this.getXRot());

            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.yBodyRot;

            float forward = rider.zza;
            float strafe = rider.xxa * 0.5F;

            float speedMultiplier = isRunning() ? 1.8F : 1.0F;
            this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * speedMultiplier);

            if (forward > 0 && this.jumping && this.onGround() && jumpCooldown <= 0) {
                performCustomJump();
                this.jumping = false;
            }

            super.travel(new Vec3(strafe, travelVector.y, forward));
            this.setSprinting(isRunning() && forward > 0);

        } else {
            super.travel(travelVector);
        }
    }


    // === ЗАЩИТА ОТ УРОНА ПРИ ПАДЕНИИ ===
    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource source) {
        if (fallDistance <= 4.0F) {
            return false;
        }
        return super.causeFallDamage(fallDistance - 4.0F, damageMultiplier, source);
    }

    @Override
    protected void jumpFromGround() {
        super.jumpFromGround();
        Vec3 currentMovement = this.getDeltaMovement();
        this.setDeltaMovement(
                currentMovement.x * 2.0,
                currentMovement.y,
                currentMovement.z * 2.0
        );
    }

    @Override
    protected float getJumpPower() {
        return 1.1F * super.getJumpPower();
    }


    @Override
    public boolean showVehicleHealth() {
        return true;
    }

    public void openCustomInventoryScreen(Player player) {
        if (!this.level().isClientSide && this.isTamed() && this.isOwnedBy(player)) {
            player.displayClientMessage(Component.literal("HP: " + (int)this.getHealth() + "/" + (int)this.getMaxHealth()), true);
        }
    }

    public boolean isOwnedBy(Player player) {
        return ownerUUID != null && player.getUUID().equals(ownerUUID);
    }

    // === АНИМАЦИИ ===
    private void handleAnimations() {
        if (isDrinking() && drinkAnimationTimer <= 0) {
            setDrinking(false);
            if (!this.isVehicle()) {
                this.getNavigation().moveTo(this.getNavigation().getPath(), 1.0D);
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "MainController", 0, this::handleMainAnimations));
    }

    private PlayState handleMainAnimations(AnimationState<MoonDeerEntity> event) {
        if (this.isDrinking()) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("drink_water"));
        }

        if (this.isRunning() || this.isPanicking()) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("run"));
        }

        if (event.isMoving()) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("walk"));
        }

        return event.setAndContinue(RawAnimation.begin().thenPlay("idle"));
    }

    // === ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ===
    private boolean isPanicking() {
        return this.getDeltaMovement().horizontalDistanceSqr() > 0.03D;
    }

    private boolean isRussianLanguage(Player player) {
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            String language = serverPlayer.getLanguage();
            return language != null && (language.startsWith("ru_") || language.equals("ru_ru") || language.contains("russian"));
        }
        return false;
    }

    private void spawnTamingParticles() {
        if (level().isClientSide) {
            for(int i = 0; i < 15; ++i) {
                level().addParticle(ParticleTypes.GLOW,
                        getRandomX(1.0D), getRandomY() + 1.0D, getRandomZ(1.0D),
                        (random.nextDouble() - 0.5) * 0.2D,
                        0.1D,
                        (random.nextDouble() - 0.5) * 0.2D);
            }
            for(int i = 0; i < 8; ++i) {
                level().addParticle(ParticleTypes.HEART,
                        getRandomX(0.7D), getRandomY() + 0.5D, getRandomZ(0.7D),
                        (random.nextDouble() - 0.5) * 0.1D,
                        0.2D,
                        (random.nextDouble() - 0.5) * 0.1D);
            }
        }
    }

    public void playSound(SoundEvent sound, float volume, float pitch) {
        if (!this.isSilent()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), sound, this.getSoundSource(), volume, pitch);
        }
    }

    // === ОСНОВНЫЕ МЕТОДЫ ===
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    @Override
    public boolean canBreed() {
        return false;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ModItems.MOON_CROWBERRY.get());
    }

    // === ГЕТТЕРЫ И СЕТТЕРЫ ===
    public boolean isTamed() { return entityData.get(DATA_TAMED); }
    public void setTamed(boolean tamed) { entityData.set(DATA_TAMED, tamed); }

    public int getAIMode() { return entityData.get(DATA_AI_MODE); }
    public void setAIMode(int mode) { entityData.set(DATA_AI_MODE, mode); }

    public boolean isDrinking() { return entityData.get(DATA_DRINKING); }
    public void setDrinking(boolean drinking) {
        entityData.set(DATA_DRINKING, drinking);
        if (drinking) {
            this.getNavigation().stop();
        }
    }

    public boolean isRunning() { return entityData.get(DATA_RUNNING); }
    public void setRunning(boolean running) { entityData.set(DATA_RUNNING, running); }

    public void setOwnerUUID(UUID uuid) { ownerUUID = uuid; }
    public UUID getOwnerUUID() {
        return ownerUUID;
    }
    public LivingEntity getOwner() {
        if (ownerUUID != null && level() != null) {
            return level().getPlayerByUUID(ownerUUID);
        }
        return null;
    }

    // === АТРИБУТЫ ===
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.JUMP_STRENGTH, 1.0D);
    }

    // === СОХРАНЕНИЕ ДАННЫХ ===
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Tamed", isTamed());
        compound.putInt("AIMode", getAIMode());
        compound.putInt("DrinkCooldown", drinkCooldown);
        compound.putInt("DrinkAnimationTimer", drinkAnimationTimer);
        compound.putBoolean("Running", isRunning());
        if (ownerUUID != null) {
            compound.putUUID("Owner", ownerUUID);
        } else {
            compound.putUUID("Owner", new UUID(0, 0));
        }

    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Tamed")) setTamed(compound.getBoolean("Tamed"));
        if (compound.contains("AIMode")) setAIMode(compound.getInt("AIMode"));
        if (compound.contains("DrinkCooldown")) drinkCooldown = compound.getInt("DrinkCooldown");
        if (compound.contains("DrinkAnimationTimer")) drinkAnimationTimer = compound.getInt("DrinkAnimationTimer");
        if (compound.contains("Running")) setRunning(compound.getBoolean("Running"));
        if (compound.hasUUID("Owner")) setOwnerUUID(compound.getUUID("Owner"));

        if (compound.hasUUID("Owner")) {
            UUID savedUUID = compound.getUUID("Owner");
            if (!savedUUID.equals(new UUID(0, 0))) {
                setOwnerUUID(savedUUID);
            }
        }

        if (drinkAnimationTimer > 0) {
            setDrinking(true);
        }
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        if (level().isClientSide && DialogSystem.hasDialog(this)) {
            Dialog dialog = DialogSystem.getDialog(this);
            if (dialog != null) {
                dialog.reset();
            }
        }
        super.remove(reason);
    }

    private void sendNamingMessage(Player player, Component name) {
        if (!level().isClientSide && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            Component message = MoonDeerPhrases.getNamingMessage(name.getString());
            serverPlayer.sendSystemMessage(message);
        }
    }

    private void spawnNamingParticles() {
        if (level().isClientSide) {
            for(int i = 0; i < 12; ++i) {
                level().addParticle(ParticleTypes.GLOW,
                        getRandomX(0.8D), getRandomY() + 0.8D, getRandomZ(0.8D),
                        (random.nextDouble() - 0.5) * 0.1D,
                        0.15D,
                        (random.nextDouble() - 0.5) * 0.1D);
            }
            for(int i = 0; i < 6; ++i) {
                level().addParticle(ParticleTypes.ENCHANT,
                        getRandomX(0.6D), getRandomY() + 0.6D, getRandomZ(0.6D),
                        (random.nextDouble() - 0.5) * 0.05D,
                        0.1D,
                        (random.nextDouble() - 0.5) * 0.05D);
            }
        }
    }

    // === СООБЩЕНИЯ ПРИ УДАРЕ ===
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getDirectEntity() instanceof Player player) {
            if (!this.level().isClientSide) {
                if (isTamed()) {
                    // Прирученный олень - компаньон в путешествии
                    Component message = getHurtTamedMessage(player);
                    player.sendSystemMessage(message);
                } else {
                    // Дикий олень - дух леса
                    Component message = getHurtWildMessage(player);
                    player.sendSystemMessage(message);
                }
            }
        }
        return super.hurt(source, amount);
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

    public Vec3 getAntlerMiddlePosition() {
        Vec3 pos = this.position();
        float headYaw = this.yHeadRot;
        float headYawRad = (float) Math.toRadians(headYaw);

        double baseX = 0.0;
        double baseY = this.getBbHeight() * 1.7;
        double baseZ = -0.8;

        float cosYaw = (float) Math.cos(headYawRad);
        float sinYaw = (float) Math.sin(headYawRad);

        return new Vec3(
                pos.x + baseZ * sinYaw,
                pos.y + baseY,
                pos.z + baseZ * cosYaw
        );
    }

    // Кормление дикого
    private Component getWildFeedingMessage(Player player) {
        return MoonDeerPhrases.getWildFeedingMessage();
    }

    // Лечение прирученного
    private Component getTamedHealingMessage(Player player) {
        return MoonDeerPhrases.getTamedHealingMessage();
    }

    // Полное здоровье
    private Component getFullHealthMessage(Player player) {
        return MoonDeerPhrases.getFullHealthMessage();
    }

    // Приручение
    private Component getTamingMessage(Player player) {
        return MoonDeerPhrases.getTamedMessage();
    }

    // Смерть
    private Component getDeathMessage(Player player) {
        return MoonDeerPhrases.getDeathMessage();
    }

    // Удар по дикому
    private Component getHurtWildMessage(Player player) {
        return MoonDeerPhrases.getHurtWildMessage();
    }

    // Удар по прирученному
    private Component getHurtTamedMessage(Player player) {
        return MoonDeerPhrases.getHurtTamedMessage();
    }

    // Именование
    private Component getNamingMessage(Player player, String name) {
        return MoonDeerPhrases.getNamingMessage(name);
    }
}
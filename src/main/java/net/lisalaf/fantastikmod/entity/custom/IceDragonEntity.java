package net.lisalaf.fantastikmod.entity.custom;

import net.lisalaf.fantastikmod.entity.ai.IceDragonGoal;
import net.lisalaf.fantastikmod.item.ModItems;
import net.lisalaf.fantastikmod.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Objects;

public class IceDragonEntity extends Animal implements GeoEntity, FlyingAnimal {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Boolean> DATA_BABY = SynchedEntityData.defineId(IceDragonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(IceDragonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_EYE_VARIANT = SynchedEntityData.defineId(IceDragonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_FLYING = SynchedEntityData.defineId(IceDragonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_TAKEOFF = SynchedEntityData.defineId(IceDragonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_LANDING = SynchedEntityData.defineId(IceDragonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_FEMALE = SynchedEntityData.defineId(IceDragonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_AGE = SynchedEntityData.defineId(IceDragonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_GROWTH_STAGE = SynchedEntityData.defineId(IceDragonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_ATTACKING = SynchedEntityData.defineId(IceDragonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> DATA_NAME = SynchedEntityData.defineId(IceDragonEntity.class, EntityDataSerializers.STRING);

    // Анимации
    private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK_ANIMATION = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation FLIGHT_START_ANIMATION = RawAnimation.begin().thenPlay("flight_started");
    private static final RawAnimation FLIGHT_IDLE_ANIMATION = RawAnimation.begin().thenLoop("idle_fly");
    private static final RawAnimation FLIGHT_ANIMATION = RawAnimation.begin().thenLoop("fly");
    private static final RawAnimation LAND_ANIMATION = RawAnimation.begin().thenPlay("land");
    private static final RawAnimation ATTACK_ANIMATION = RawAnimation.begin().thenPlay("attack1");

    private int takeoffAnimationTimer = 0;
    private int landingAnimationTimer = 0;
    private int growthTimer = 0;
    private int attackTimer = 0;

    private static final int GROWTH_INTERVAL = 30 * 60 * 20;

    public static final int STAGE_BABY = 0;      // Малыш (0-30 мин)
    public static final int STAGE_TEEN = 1;      // Подросток (30-60 мин)
    public static final int STAGE_YOUNG = 2;     // Юный (60-90 мин)
    public static final int STAGE_ADULT = 3;     // Взрослый (90+ мин)

    @Override
    public boolean isMultipartEntity() {
        return false;
    }

    @Override
    public PartEntity<?>[] getParts() {
        return new PartEntity[0];
    }

    public static class DragonPart extends PartEntity<IceDragonEntity> {
        private final String partName;
        private final float width;
        private final float height;

        public DragonPart(IceDragonEntity parent, String partName, float width, float height) {
            super(parent);
            this.partName = partName;
            this.width = width;
            this.height = height;
            this.refreshDimensions();
        }

        @Override
        public boolean isPickable() {
            return true;
        }

        @Override
        protected void readAdditionalSaveData(CompoundTag pCompound) {}

        @Override
        protected void addAdditionalSaveData(CompoundTag pCompound) {}

        @Override
        protected void defineSynchedData() {}

        @Override
        public boolean hurt(DamageSource source, float amount) {
            return !this.isInvulnerableTo(source) && this.getParent().hurt(source, amount);
        }

        @Override
        public EntityDimensions getDimensions(Pose pose) {
            return EntityDimensions.scalable(this.width, this.height);
        }

        @Override
        public boolean shouldBeSaved() {
            return false;
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        int stage = this.getGrowthStage();

        switch(stage) {
            case STAGE_BABY:
                return EntityDimensions.scalable(0.5f, 1.0f);
            case STAGE_TEEN:
                return EntityDimensions.scalable(2.5f, 5.5f);
            case STAGE_YOUNG:
                return EntityDimensions.scalable(3.0f, 6.0f);
            case STAGE_ADULT:
            default:
                float baseWidth = 5.0f;
                float baseHeight = 12.0f;

                if (this.isFlying()) {
                    baseHeight = 4.0f;
                }

                float adultScale = this.isFemale() ? 1.1f : 1.0f;
                return EntityDimensions.scalable(baseWidth * adultScale, baseHeight * adultScale);
        }
    }

    @Override
    protected AABB makeBoundingBox() {
        EntityDimensions dimensions = this.getDimensions(this.getPose());
        float width = dimensions.width;
        float height = dimensions.height;

        float depth;
        int stage = this.getGrowthStage();

        switch(stage) {
            case STAGE_BABY:
                depth = 2.0f;
                break;
            case STAGE_TEEN:
                depth = 8.0f;
                break;
            case STAGE_YOUNG:
                depth = 12.0f;
                break;
            case STAGE_ADULT:
            default:
                depth = 16.0f;
                break;
        }

        AABB boundingBox = new AABB(
                -width / 2, 0, -depth / 2,
                width / 2, height, depth / 2
        );

        float modelOffsetY;
        if (this.isFlying()) {
            switch(this.getGrowthStage()) {
                case STAGE_BABY:
                    modelOffsetY = 3.0f;
                    break;
                case STAGE_TEEN:
                    modelOffsetY = 6.0f;
                    break;
                case STAGE_YOUNG:
                    modelOffsetY = 14.0f;
                    break;
                case STAGE_ADULT:
                default:
                    modelOffsetY = 18.0f;
                    break;
            }
        } else {
            modelOffsetY = 0.0f;
        }

        return boundingBox.move(this.getX(), this.getY() + modelOffsetY, this.getZ());
    }

    public IceDragonEntity(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setCanFly(true);

        // Принудительно устанавливаем взрослую стадию при создании
        this.setGrowthStage(STAGE_ADULT);
        this.setAge(GROWTH_INTERVAL * 3);

        if (!pLevel.isClientSide()) {
            this.generateRandomName(this.random);
        }

        this.refreshDimensions();
    }


    @Override
    public void setRemainingFireTicks(int ticks) {
        super.setRemainingFireTicks((int)(ticks * 1.2f));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (DATA_BABY.equals(key) || DATA_FEMALE.equals(key) || DATA_GROWTH_STAGE.equals(key)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(key);
    }

    private void updatePartPositions() {
        if (this.level().isClientSide) return;
        // Логика позиционирования частей (если нужно)
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getDirectEntity() instanceof Player player) {
            if (!this.level().isClientSide()) {
                boolean isRussian = isPlayerRussian(player);
                String message = getRandomHurtMessage(isRussian);
                String dragonName = this.getDragonName();

                String prefix;
                if (isRussian) {
                    prefix = "🐉 " + dragonName + " восклицает: ";
                } else {
                    prefix = "🐉 " + dragonName + " exclaims: ";
                }

                player.sendSystemMessage(Component.literal(prefix + message));
            }
        }

        // Твоя существующая логика урона от огня
        if (source.is(DamageTypeTags.IS_FIRE)) {
            amount *= 1.5f;
        }

        if (source.getDirectEntity() instanceof LivingEntity attacker) {
            ItemStack weapon = attacker.getMainHandItem();
            if (weapon.isEnchanted()) {
                if (weapon.getEnchantmentLevel(Enchantments.FIRE_ASPECT) > 0) {
                    amount *= 1.3f;
                }
                if (weapon.getEnchantmentLevel(Enchantments.FLAMING_ARROWS) > 0) {
                    amount *= 1.3f;
                }
            }
        }

        return super.hurt(source, amount);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(5, new IceDragonGoal.FlyWanderGoal(this));
        this.goalSelector.addGoal(4, new IceDragonGoal.TakeOffGoal(this));
        this.goalSelector.addGoal(3, new IceDragonGoal.LandGoal(this));

        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        // Атакует только в ответ на удар
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));

        this.goalSelector.addGoal(0, new FloatGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_BABY, false);
        this.entityData.define(DATA_VARIANT, 0);
        this.entityData.define(DATA_EYE_VARIANT, 0);
        this.entityData.define(DATA_FLYING, false);
        this.entityData.define(DATA_TAKEOFF, false);
        this.entityData.define(DATA_LANDING, false);
        this.entityData.define(DATA_FEMALE, false);
        this.entityData.define(DATA_AGE, 0);
        this.entityData.define(DATA_GROWTH_STAGE, STAGE_ADULT);
        this.entityData.define(DATA_ATTACKING, false);
        this.entityData.define(DATA_NAME, "");
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation flyingPathNavigation = new FlyingPathNavigation(this, level);
        flyingPathNavigation.setCanOpenDoors(false);
        flyingPathNavigation.setCanFloat(true);
        flyingPathNavigation.setCanPassDoors(true);
        return flyingPathNavigation;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 600.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FLYING_SPEED, 0.8D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 10.0D);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "MainController", 5, event -> {
            if (this.isAttacking()) {
                return event.setAndContinue(ATTACK_ANIMATION);
            }

            if (this.isLanding()) {
                return event.setAndContinue(LAND_ANIMATION);
            }

            if (this.getGrowthStage() == STAGE_BABY) {
                if (event.isMoving()) {
                    return event.setAndContinue(WALK_ANIMATION);
                } else {
                    return event.setAndContinue(IDLE_ANIMATION);
                }
            }

            if (this.isTakingOff()) {
                return event.setAndContinue(FLIGHT_START_ANIMATION);
            }

            if (this.isFlying()) {
                Vec3 movement = this.getDeltaMovement();
                double horizontalSpeed = movement.horizontalDistance();
                double verticalSpeed = Math.abs(movement.y);

                if (horizontalSpeed > 0.1 && verticalSpeed < 0.3) {
                    return event.setAndContinue(FLIGHT_ANIMATION);
                } else {
                    return event.setAndContinue(FLIGHT_IDLE_ANIMATION);
                }
            } else {
                if (event.isMoving()) {
                    return event.setAndContinue(WALK_ANIMATION);
                } else {
                    return event.setAndContinue(IDLE_ANIMATION);
                }
            }
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public boolean onGround() {
        return !this.isFlying() && super.onGround();
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isTakingOff() || this.isLanding() || this.getGrowthStage() == STAGE_BABY) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
            return;
        }

        if (this.isFlying()) {
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.95D));
        } else {
            super.travel(travelVector);
        }
    }

    // Геттеры и сеттеры
    public void setFlying(boolean flying) {
        this.entityData.set(DATA_FLYING, flying);
        this.setCanFly(flying);
    }

    public boolean isTakingOff() {
        return this.entityData.get(DATA_TAKEOFF);
    }

    public void setTakingOff(boolean takingOff) {
        this.entityData.set(DATA_TAKEOFF, takingOff);
        if (takingOff) {
            this.takeoffAnimationTimer = 40;
        }
    }

    public boolean isLanding() {
        return this.entityData.get(DATA_LANDING);
    }

    public void setLanding(boolean landing) {
        this.entityData.set(DATA_LANDING, landing);
        if (landing) {
            this.landingAnimationTimer = 40;
        }
    }

    public void setCanFly(boolean canFly) {
        this.setNoGravity(canFly);
    }

    public int getVariant() {
        return this.entityData.get(DATA_VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(DATA_VARIANT, variant);
    }

    public boolean isFemale() {
        return this.entityData.get(DATA_FEMALE);
    }

    public void setFemale(boolean female) {
        this.entityData.set(DATA_FEMALE, female);
        this.refreshDimensions();
    }

    public boolean isBaby() {
        return this.entityData.get(DATA_BABY);
    }

    public void setBaby(boolean baby) {
        this.entityData.set(DATA_BABY, baby);
        this.refreshDimensions();
    }

    public int getAge() {
        return this.entityData.get(DATA_AGE);
    }

    public void setAge(int age) {
        this.entityData.set(DATA_AGE, age);
        this.updateGrowthStage();
    }

    public int getGrowthStage() {
        return this.entityData.get(DATA_GROWTH_STAGE);
    }

    private void setGrowthStage(int stage) {
        this.entityData.set(DATA_GROWTH_STAGE, stage);
        this.refreshDimensions();
    }

    public boolean isAttacking() {
        return this.entityData.get(DATA_ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(DATA_ATTACKING, attacking);
    }

    public void setAttackTimer(int timer) {
        this.attackTimer = timer;
        this.setAttacking(timer > 0);
    }

    public void useIceBreath(LivingEntity target) {
        if (this.getGrowthStage() >= STAGE_TEEN) {
            // Логика ледяного дыхания для обычных драконов
        }
    }

    private void updateGrowthStage() {
        int age = this.getAge();
        if (age < GROWTH_INTERVAL) {
            this.setGrowthStage(STAGE_BABY);
        } else if (age < GROWTH_INTERVAL * 2) {
            this.setGrowthStage(STAGE_TEEN);
        } else if (age < GROWTH_INTERVAL * 3) {
            this.setGrowthStage(STAGE_YOUNG);
        } else {
            this.setGrowthStage(STAGE_ADULT);
        }
        this.refreshDimensions();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", this.getVariant());
        compound.putInt("EyeVariant", this.getEyeVariant());
        compound.putBoolean("Flying", this.isFlying());
        compound.putBoolean("Female", this.isFemale());
        compound.putBoolean("IsBaby", this.isBaby());
        compound.putInt("Age", this.getAge());
        compound.putInt("FurDropTimer", this.furDropTimer);
        compound.putString("DragonName", this.getDragonName());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Variant")) {
            this.setVariant(compound.getInt("Variant"));
        }
        if (compound.contains("EyeVariant")) {
            this.setEyeVariant(compound.getInt("EyeVariant"));
        }
        if (compound.contains("Flying")) {
            this.setFlying(compound.getBoolean("Flying"));
        }
        if (compound.contains("Female")) {
            this.setFemale(compound.getBoolean("Female"));
        }
        if (compound.contains("IsBaby")) {
            this.setBaby(compound.getBoolean("IsBaby"));
        }

        if (compound.contains("Age")) {
            this.setAge(compound.getInt("Age"));
        } else {
            this.setGrowthStage(STAGE_ADULT);
            this.setAge(GROWTH_INTERVAL * 3);
        }

        if (compound.contains("FurDropTimer")) {
            this.furDropTimer = compound.getInt("FurDropTimer"); // Загружаем таймер
        }

        if (compound.contains("DragonName")) {
            this.setDragonName(compound.getString("DragonName"));
        } else {
            // Генерируем имя если его нет
            this.generateRandomName(this.random);
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        // Убираем проверку на BREEDING или расширяем условия
        this.setVariant(level.getRandom().nextInt(3));
        this.setEyeVariant(level.getRandom().nextInt(5));
        this.setFemale(level.getRandom().nextBoolean());

        // Всегда взрослая стадия
        this.setGrowthStage(STAGE_ADULT);
        this.setAge(GROWTH_INTERVAL * 3);

        // Генерируем имя
        generateRandomName(level.getRandom());

        System.out.println("SPAWN: Dragon spawned - Stage: " + this.getGrowthStage() +
                ", Name: " + this.getDragonName() +
                ", SpawnType: " + reason);

        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    private void generateRandomName(RandomSource random) {
        boolean isRussian = isWorldRussian();
        boolean isFemale = this.isFemale();

        String name;
        if (isRussian) {
            if (isFemale) {
                name = FEMALE_SCANDINAVIAN_NAMES[random.nextInt(FEMALE_SCANDINAVIAN_NAMES.length)];
            } else {
                name = MALE_SCANDINAVIAN_NAMES[random.nextInt(MALE_SCANDINAVIAN_NAMES.length)];
            }
        } else {
            if (isFemale) {
                name = FEMALE_ENGLISH_NAMES[random.nextInt(FEMALE_ENGLISH_NAMES.length)];
            } else {
                name = MALE_ENGLISH_NAMES[random.nextInt(MALE_ENGLISH_NAMES.length)];
            }
        }

        this.setDragonName(name);
    }

    private boolean isWorldRussian() {
        // Простая проверка языка мира
        try {
            if (this.level().isClientSide()) {
                return net.minecraft.client.Minecraft.getInstance().options.languageCode.startsWith("ru");
            } else {
                // Для сервера можно использовать системные настройки
                return System.getProperty("user.language", "en").startsWith("ru");
            }
        } catch (Exception e) {
            return false;
        }
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
        return stack.getItem() == Items.COD || stack.getItem() == Items.SALMON;
    }

    @Override
    public boolean canFallInLove() {
        return super.canFallInLove() && this.getGrowthStage() >= STAGE_ADULT;
    }

    @Override
    public void tick() {
        super.tick();

        updatePartPositions();

        if (!this.level().isClientSide() && this.tickCount % 400 == 0 && this.random.nextInt(3) == 0) {
            this.playIdleSound();
        }

        // ТАЙМЕР СБРОСА МЕХА - каждые 10 минут
        if (!this.level().isClientSide() && this.getGrowthStage() >= STAGE_TEEN) {
            this.furDropTimer++;
            if (this.furDropTimer >= FUR_DROP_INTERVAL) {
                this.dropFur();
                this.furDropTimer = 0;

                // Дебаг сообщение (можно убрать потом)
                if (this.tickCount % 1200 == 0) { // Каждую минуту показываем прогресс
                    int minutesLeft = (FUR_DROP_INTERVAL - this.furDropTimer) / 1200;
                    System.out.println("Dragon will drop fur in " + minutesLeft + " minutes");
                }
            }
        }

        if (this.takeoffAnimationTimer > 0) {
            this.takeoffAnimationTimer--;
            if (this.takeoffAnimationTimer <= 0 && this.isTakingOff()) {
                this.setTakingOff(false);
                this.setFlying(true);
            }
        }

        if (this.landingAnimationTimer > 0) {
            this.landingAnimationTimer--;
            if (this.landingAnimationTimer <= 0 && this.isLanding()) {
                this.setLanding(false);
                this.setFlying(false);
            }
        }

        if (this.getGrowthStage() == STAGE_BABY) {
            if (this.isFlying() || this.isTakingOff() || this.isLanding()) {
                this.setFlying(false);
                this.setTakingOff(false);
                this.setLanding(false);
                this.setNoGravity(false);
            }
        }

        if (this.tickCount % 20 == 0 && this.getGrowthStage() < STAGE_ADULT) {
            this.growthTimer++;
            if (this.growthTimer >= 60) {
                this.growthTimer = 0;
                this.setAge(this.getAge() + 20 * 60);
            }
        }

        if (this.attackTimer > 0) {
            this.attackTimer--;
            if (this.attackTimer <= 0) {
                this.setAttacking(false);
            }
        }
    }


    @Override
    public boolean isFlying() {
        return this.entityData.get(DATA_FLYING);
    }

    public int getEyeVariant() {
        return this.entityData.get(DATA_EYE_VARIANT);
    }

    public void setEyeVariant(int eyeVariant) {
        this.entityData.set(DATA_EYE_VARIANT, eyeVariant);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double maxDistance = 1024.0;
        return distance < maxDistance * maxDistance;
    }

    public boolean isAlwaysRenderNameTag() {
        return true;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int lootingLevel, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, lootingLevel, recentlyHit);

        int furCount = 4 + this.random.nextInt(12);
        this.spawnAtLocation(ModItems.FUR_ICE_DRAGON.get(), furCount);

        this.spawnAtLocation(ModItems.HEART_ICE_DRAGON.get());
    }

    private int furDropTimer = 0;
    private static final int FUR_DROP_INTERVAL = 10 * 60 * 20; // 10 минут в тиках (10 * 60 * 20)
    public void dropFur() {
        if (!this.level().isClientSide()) {
            // Сбрасываем 1 единицу меха
            this.spawnAtLocation(ModItems.FUR_ICE_DRAGON.get(), 1);
            System.out.println("Dragon dropped fur at position: " + this.blockPosition());
        }
    }

    @Override
    protected void dropExperience() {
        if (this.level() instanceof ServerLevel serverLevel) {
            // Опыт: среднее между варденом (5) и эндер-драконом (500)
            // Варден: 5 опыта, Эндер-дракон: 500 опыта, наше среднее: ~250
            int experience = 250 + this.random.nextInt(30); // 250-300 опыта

            ExperienceOrb.award(serverLevel, this.position(), experience);
            System.out.println("Dragon dropped " + experience + " experience points");
        }
    }

    private boolean isPlayerRussian(Player player) {
        try {
            // Для тестирования - всегда английский
            return false;

            // Или раскомментируй для реальной проверки:
            // String systemLanguage = System.getProperty("user.language", "en");
            // return systemLanguage.startsWith("ru");
        } catch (Exception e) {
            return false;
        }
    }



    private String getRandomFeedingMessage(boolean isRussian) {
        if (isRussian) {
            String[] russianMessages = {
                    "Спасибо за рыбу! Обычно я сам охочусь...",
                    "Ммм, свежая рыбка! Как редко меня угощают!",
                    "Ты смелый, что подошел так близко! Спасибо за угощение!",
                    "Эта рыба напоминает мне о северных морях...",
                    "Я не ожидал такой щедрости от двуногого!",
                    "Хрум-хрум! Вкуснее, чем пойманная самостоятельно!",
                    "Ты заслужил мое доверие, маленький друг!",
                    "Рыбка! Я обожаю рыбку! Спасибо тебе!",
                    "Как приятно, когда кто-то заботится обо мне...",
                    "Этот запах свежей рыбы сводит меня с ума!"
            };
            return russianMessages[this.random.nextInt(russianMessages.length)];
        } else {
            String[] englishMessages = {
                    "Thank you for the fish! Usually I have to go hunting for this...",
                    "Mmm, fresh fish! I rarely get treated",
                    "You're brave to approach so close! Thank you for the treat",
                    "This fish reminds me of the northern seas...",
                    "Unexpected generosity from a two-legged",
                    "How nice not to have to go looking for food..."
            };
            return englishMessages[this.random.nextInt(englishMessages.length)];
        }
    }

    private void spawnHearts() {
        if (this.level().isClientSide()) {
            for (int i = 0; i < 5; i++) {
                double x = this.getX() + (this.random.nextDouble() - 0.5) * 2.0;
                double y = this.getY() + 1.0 + this.random.nextDouble() * 2.0;
                double z = this.getZ() + (this.random.nextDouble() - 0.5) * 2.0;

                this.level().addParticle(ParticleTypes.HEART, x, y, z, 0, 0.1, 0);
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (this.isFood(itemstack)) {
            if (!this.level().isClientSide()) {
                boolean isRussian = isPlayerRussian(player);
                String message = getRandomFeedingMessage(isRussian);
                String dragonName = this.getDragonName();

                // Разные префиксы для языков
                String prefix;
                if (isRussian) {
                    prefix = "🐉 " + dragonName + " говорит: ";
                } else {
                    prefix = "🐉 " + dragonName + " says: ";
                }

                player.sendSystemMessage(Component.literal(prefix + message));

                spawnHearts();

                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                this.usePlayerItem(player, hand, itemstack);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        return super.mobInteract(player, hand);
    }


    private static final String[] MALE_SCANDINAVIAN_NAMES = {
            "Фафнир", "Йормунганд", "Фенрир", "Нидхёгг", "Грендель",
            "Хрёсвельг", "Хейдрун", "Рататоск", "Гулльинбурсти", "Хримфакси"
    };

    // Скандинавские имена для женских драконов
    private static final String[] FEMALE_SCANDINAVIAN_NAMES = {
            "Хель", "Скади", "Фрейя", "Сигунн", "Брюнхильд",
            "Гудрун", "Астрид", "Ингрид", "Сигрид", "Рагнхильд"
    };

    // English versions
    private static final String[] MALE_ENGLISH_NAMES = {
            "Fafnir", "Jormungand", "Fenrir", "Nidhogg", "Grendel",
            "Hraesvelgr", "Heidrun", "Ratatosk", "Gullinbursti", "Hrimfaxi"
    };

    private static final String[] FEMALE_ENGLISH_NAMES = {
            "Hel", "Skadi", "Freya", "Sigunn", "Brynhild",
            "Gudrun", "Astrid", "Ingrid", "Sigrid", "Ragnhild"
    };

    public String getDragonName() {
        return this.entityData.get(DATA_NAME);
    }

    public void setDragonName(String name) {
        this.entityData.set(DATA_NAME, name);
        this.setCustomName(Component.literal(name));
        this.setCustomNameVisible(true);
    }

    private String getRandomHurtMessage(boolean isRussian) {
        if (isRussian) {
            String[] russianHurtMessages = {
                    "За тысячу лет никто так со мной не обращался!",
                    "Мои годы научили меня терпению, но ты испытываешь его!",
                    "Даже ледники помнят меньше обид, чем я сейчас!",
                    "В мои годы подобное обращение оскорбительно!",
                    "Я видел цивилизации, но такой дерзости не припоминаю!",
                    "Мое долгожитие не повод для грубости!",
                    "За века я многое происходило, но такое впервые!",
            };
            return russianHurtMessages[this.random.nextInt(russianHurtMessages.length)];
        } else {
            String[] englishHurtMessages = {
                    "In a thousand years, no one has treated me like this!",
                    "My years have taught me patience, but you're testing it!",
                    "Even glaciers remember fewer grievances than I do now!",
                    "At my age, such treatment is offensive!",
                    "I've seen civilizations, but I don't recall such audacity!",
                    "My longevity is no reason for rudeness!",
                    "Over the centuries, I've seen much, but this is new!",
            };
            return englishHurtMessages[this.random.nextInt(englishHurtMessages.length)];
        }
    }

    public void playIdleSound() {
        if (!this.level().isClientSide()) {
            SoundEvent[] idleSounds = {
                    ModSounds.DRAGON_IDLE1.get(),
                    ModSounds.DRAGON_IDLE2.get(),
                    ModSounds.DRAGON_IDLE3.get()
            };
            SoundEvent sound = idleSounds[this.random.nextInt(idleSounds.length)];
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    sound, this.getSoundSource(), 1.0F, 1.0F);
        }
    }

    public void playAggressiveSound() {
        if (!this.level().isClientSide()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.DRAGON_AGGRESSIVE_ROAR.get(), this.getSoundSource(), 1.5F, 1.0F);
        }
    }




}
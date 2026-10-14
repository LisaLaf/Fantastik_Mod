package net.lisalaf.fantastikmod.entity.custom;

import net.lisalaf.fantastikmod.block.ModBlocks;
import net.lisalaf.fantastikmod.entity.ai.ghost.GhostAttackGoal;
import net.lisalaf.fantastikmod.item.ModItems;
import net.lisalaf.fantastikmod.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

@SuppressWarnings("removal")
public class GhostGuardsEntity extends Monster implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final double FULL_SILVER_KEEP_DISTANCE = 4.0D;
    private static final double PARTIAL_SILVER_KEEP_DISTANCE = 1.0D;
    private static final double ATTACK_REACH = 2.0D;
    private double spawnX = Double.NaN;
    private double spawnZ = Double.NaN;
    private static final double MAX_WANDER_DISTANCE = 25.0D;
    private static final double MAX_WANDER_DISTANCE_SQR = MAX_WANDER_DISTANCE * MAX_WANDER_DISTANCE;
    private int attackDamageTicks = 0;
    private int attackResetTicks = 0;
    private Entity pendingTarget = null;
    private static final int ATTACK_HALF_TICKS = 8;
    private static final int ATTACK_FULL_TICKS = 30;

    private static final double HOVER_BASE = 0.5;
    private static final double HOVER_AMPLITUDE = 0.02;
    private static final float HOVER_SPEED = 0.08f;
    private static final double HOVER_STEP = 0.02;
    private static final int GROUND_SEARCH_RANGE = 5;
    private static final int MAX_WALL_THICKNESS = 3;
    private int ambientSoundCooldown = 0;
    private int walkSoundCooldown = 0;

    private static final double WOLF_FEAR_RADIUS = 8.0D;

    private static final double SILVER_BLOCK_KEEP_DISTANCE = 5.0D;
    private static final double SILVER_ORE_KEEP_DISTANCE = 2.0D;

    private double targetY = -1;
    private float hoverPhase = 0f;

    private static final EntityDataAccessor<Integer> DATA_TEXTURE_VARIANT =
            SynchedEntityData.defineId(GhostGuardsEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_ATTACKING =
            SynchedEntityData.defineId(GhostGuardsEntity.class, EntityDataSerializers.BOOLEAN);

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TEXTURE_VARIANT, 0);
        this.entityData.define(DATA_ATTACKING, false);
    }

    public int getTextureVariant() {
        return this.entityData.get(DATA_TEXTURE_VARIANT);
    }

    public void setTextureVariant(int variant) {
        this.entityData.set(DATA_TEXTURE_VARIANT, variant);
    }

    public boolean isAttacking() {
        return this.entityData.get(DATA_ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(DATA_ATTACKING, attacking);
    }

    public GhostGuardsEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = false;
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.MOVEMENT_SPEED, 1.25D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation nav = new FlyingPathNavigation(this, level);
        nav.setCanOpenDoors(false);
        nav.setCanFloat(true);
        nav.setCanPassDoors(true);
        return nav;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new GhostAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomFlyingGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 0.8F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 3, this::predicate));
    }

    private PlayState predicate(AnimationState<GhostGuardsEntity> state) {
        if (this.isAttacking()) {
            return state.setAndContinue(RawAnimation.begin().thenPlay("attack"));
        }
        if (state.isMoving()) {
            return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
        }
        return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) return;

        updateHover();

        if (attackDamageTicks > 0) {
            attackDamageTicks--;
            if (attackDamageTicks <= 0 && pendingTarget != null) {
                if (pendingTarget.isAlive() && this.distanceTo(pendingTarget) <= ATTACK_REACH + 1.0D) {
                    pendingTarget.hurt(this.damageSources().mobAttack(this), (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    playAttackSound();
                }
                pendingTarget = null;
            }
        }

        if (attackResetTicks > 0) {
            attackResetTicks--;
            if (attackResetTicks <= 0) {
                setAttacking(false);
            }
        }


        if (isBlockedByThickWall()) {
            this.getNavigation().stop();
            Vec3 v = this.getDeltaMovement();
            this.setDeltaMovement(0, v.y, 0);
        }

        avoidSilverBlocks();

        if (attackResetTicks > 0) {
            attackResetTicks--;
            if (attackResetTicks <= 0) {
                setAttacking(false);
            }
        }

        Player nearest = this.level().getNearestPlayer(this, 16.0D);
        if (nearest != null) {
            int silverPieces = countSilverArmor(nearest);
            double keepDistance = silverPieces >= 4
                    ? FULL_SILVER_KEEP_DISTANCE
                    : (silverPieces > 0 ? PARTIAL_SILVER_KEEP_DISTANCE : 0.0D);

            if (keepDistance > 0.0D) {
                double dist = this.distanceTo(nearest);
                if (dist < keepDistance) {
                    Vec3 away = this.position().subtract(nearest.position()).normalize();
                    this.setDeltaMovement(away.x * 0.2D, this.getDeltaMovement().y, away.z * 0.2D);
                    this.hasImpulse = true;
                }
            }
        }

        if (this.tickCount % 10 == 0) {
            scareWolves();
        }

        if (ambientSoundCooldown > 0) ambientSoundCooldown--;
        if (walkSoundCooldown > 0) walkSoundCooldown--;

        if (walkSoundCooldown <= 0 && this.getDeltaMovement().horizontalDistanceSqr() > 0.001) {
            playWalkSound();
            walkSoundCooldown = 20 + random.nextInt(20);
        }
    }

    private void updateHover() {
        double newTargetY = this.getY();
        BlockPos.MutableBlockPos pos = this.blockPosition().mutable();
        for (int dy = 1; dy <= GROUND_SEARCH_RANGE; dy++) {
            pos.set(this.getBlockX(), this.getBlockY() - dy, this.getBlockZ());
            if (!this.level().getBlockState(pos).isAir()) {
                newTargetY = pos.getY() + 1 + HOVER_BASE;
                break;
            }
        }
        this.targetY = newTargetY;

        double currentY = this.getY();
        double diff = targetY - currentY;
        double step = Math.max(-HOVER_STEP, Math.min(HOVER_STEP, diff));

        hoverPhase += HOVER_SPEED;
        if (hoverPhase > (float)(Math.PI * 2)) hoverPhase -= (float)(Math.PI * 2);
        double bob = Math.sin(hoverPhase) * HOVER_AMPLITUDE;

        Vec3 v = this.getDeltaMovement();
        this.setDeltaMovement(v.x, step + bob, v.z);
    }

    @Override
    public void travel(Vec3 travelVector) {
        super.travel(new Vec3(travelVector.x, 0, travelVector.z));
    }

    private boolean isBlockedByThickWall() {
        Vec3 motion = this.getDeltaMovement();
        double dx = motion.x, dz = motion.z;
        double len = Math.sqrt(dx * dx + dz * dz);
        if (len < 0.01) return false;
        dx /= len;
        dz /= len;

        int thickness = 0;
        for (int i = 1; i <= GROUND_SEARCH_RANGE; i++) {
            int bx = (int)Math.floor(this.getX() + dx * i);
            int by = (int)Math.floor(this.getY() + 0.5);
            int bz = (int)Math.floor(this.getZ() + dz * i);
            BlockPos p = new BlockPos(bx, by, bz);
            if (!this.level().getBlockState(p).isAir()) {
                thickness++;
            } else {
                break;
            }
        }
        return thickness > MAX_WALL_THICKNESS;
    }

    private void avoidSilverBlocks() {
        int radius = (int)Math.ceil(SILVER_BLOCK_KEEP_DISTANCE);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        Vec3 pushAway = Vec3.ZERO;
        int count = 0;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    pos.set(this.getBlockX() + dx, this.getBlockY() + dy, this.getBlockZ() + dz);
                    BlockState state = this.level().getBlockState(pos);
                    double keep;
                    if (state.is(ModBlocks.SILVER_BLOCK.get())) {
                        keep = SILVER_BLOCK_KEEP_DISTANCE;
                    } else if (state.is(ModBlocks.SILVER_ORE.get())) {
                        keep = SILVER_ORE_KEEP_DISTANCE;
                    } else {
                        continue;
                    }

                    Vec3 blockCenter = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    Vec3 away = this.position().subtract(blockCenter);
                    double dist = away.length();
                    if (dist < keep && dist > 0.01) {
                        pushAway = pushAway.add(away.normalize().scale(keep - dist));
                        count++;
                    }
                }
            }
        }

        if (count > 0) {
            Vec3 push = pushAway.scale(0.05);
            Vec3 v = this.getDeltaMovement();
            this.setDeltaMovement(v.x + push.x, v.y, v.z + push.z);
            this.hasImpulse = true;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof Player player) {
            ItemStack mainHand = player.getMainHandItem();
            if (!isSilverTool(mainHand)) {
                return false;
            }
        } else {
            return false;
        }

        return super.hurt(source, amount);
    }

    private boolean isSilverTool(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.is(ModItems.SILVER_SWORD.get())
                || stack.is(ModItems.SILVER_AXE.get())
                || stack.is(ModItems.SILVER_PICKAXE.get())
                || stack.is(ModItems.SILVER_SHOVEL.get())
                || stack.is(ModItems.SILVER_HOE.get());
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (this.distanceTo(target) > ATTACK_REACH + this.getBbWidth() / 2 + target.getBbWidth() / 2) {
            return false;
        }
        this.setAttacking(true);
        this.attackDamageTicks = ATTACK_HALF_TICKS;
        this.attackResetTicks = ATTACK_FULL_TICKS;
        this.pendingTarget = target;

        return true;
    }

    private int countSilverArmor(Player player) {
        int count = 0;
        if (player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.SILVER_HELMET.get())) count++;
        if (player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.SILVER_CHESTPLATE.get())) count++;
        if (player.getItemBySlot(EquipmentSlot.LEGS).is(ModItems.SILVER_LEGGINGS.get())) count++;
        if (player.getItemBySlot(EquipmentSlot.FEET).is(ModItems.SILVER_BOOTS.get())) count++;
        return count;
    }

    private void scareWolves() {
        List<Wolf> wolves = this.level().getEntitiesOfClass(Wolf.class,
                this.getBoundingBox().inflate(WOLF_FEAR_RADIUS));
        for (Wolf wolf : wolves) {
            Vec3 away = wolf.position().subtract(this.position()).normalize();
            wolf.setDeltaMovement(away.x * 0.3, wolf.getDeltaMovement().y, away.z * 0.3);
            wolf.hurtMarked = true;
            wolf.getNavigation().stop();
            if (this.random.nextInt(5) == 0) {
                wolf.playSound(SoundEvents.WOLF_WHINE, 0.6f, 1.0f);
            }
        }
    }

    private void playIdleSound() {
        if (this.level().isClientSide) return;
        SoundEvent[] idleSounds = {
                ModSounds.GHOST_GUARDS_IDLE1.get(),
                ModSounds.GHOST_GUARDS_IDLE2.get()
        };
        this.playSound(idleSounds[random.nextInt(idleSounds.length)], 0.6F, 0.9F + random.nextFloat() * 0.2F);
    }

    private void playWalkSound() {
        if (this.level().isClientSide) return;
        SoundEvent[] walkSounds = {
                ModSounds.GHOST_GUARDS_WALK1.get(),
                ModSounds.GHOST_GUARDS_WALK2.get()
        };
        this.playSound(walkSounds[random.nextInt(walkSounds.length)], 0.4F, 1.0F + random.nextFloat() * 0.1F);
    }

    private void playAttackSound() {
        if (this.level().isClientSide) return;
        this.playSound(ModSounds.GHOST_GUARDS_ATTACK.get(), 0.8F, 1.0F);
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("TextureVariant", getTextureVariant());
        compound.putDouble("TargetY", targetY);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("TextureVariant")) setTextureVariant(compound.getInt("TextureVariant"));
        if (compound.contains("TargetY")) targetY = compound.getDouble("TargetY");
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType reason, @Nullable SpawnGroupData spawnData,
                                        @Nullable CompoundTag dataTag) {
        setTextureVariant(this.random.nextInt(4));
        this.spawnX = this.getX();
        this.spawnZ = this.getZ();
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }
}
package net.lisalaf.fantastikmod.entity.custom;

import net.lisalaf.fantastikmod.entity.ai.kitsune.soul.*;
import net.lisalaf.fantastikmod.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KitsuneSoulPossessedEntity extends Monster {

    private static final EntityDataAccessor<String> DATA_OWNER_NAME =
            SynchedEntityData.defineId(KitsuneSoulPossessedEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DATA_OWNER_TEXTURE =
            SynchedEntityData.defineId(KitsuneSoulPossessedEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DATA_OWNER_TEXTURE_SLIM =
            SynchedEntityData.defineId(KitsuneSoulPossessedEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> DATA_ATTACK_COOLDOWN =
            SynchedEntityData.defineId(KitsuneSoulPossessedEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_FLEEING =
            SynchedEntityData.defineId(KitsuneSoulPossessedEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_EATING =
            SynchedEntityData.defineId(KitsuneSoulPossessedEntity.class, EntityDataSerializers.BOOLEAN);

    private UUID ownerUUID;
    private int attackCooldown = 0;
    private int fleeCooldown = 0;
    private boolean isFleeing = false;
    private boolean dropItemsOnDeath = true;

    private final List<ItemStack> storedInventory = new ArrayList<>();

    private static final int ATTACK_COOLDOWN_NORMAL = 300;
    private static final int ATTACK_COOLDOWN_LOW_HP = 400;
    private static final int ATTACK_COOLDOWN_CRITICAL = 600;
    private static final float MELEE_DAMAGE = 2.0f;
    private static final int FIRE_DURATION = 60;

    public KitsuneSoulPossessedEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 50;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.ATTACK_DAMAGE, MELEE_DAMAGE)
                .add(Attributes.FOLLOW_RANGE, 48.0D)
                .add(Attributes.JUMP_STRENGTH, 0.9D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_OWNER_NAME, "");
        this.entityData.define(DATA_OWNER_TEXTURE, "");
        this.entityData.define(DATA_OWNER_TEXTURE_SLIM, "");
        this.entityData.define(DATA_ATTACK_COOLDOWN, 0);
        this.entityData.define(DATA_FLEEING, false);
        this.entityData.define(DATA_EATING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new KitsuneSoulEatFoodGoal(this));
        this.goalSelector.addGoal(2, new KitsuneSoulJumpAttackGoal(this));
        this.goalSelector.addGoal(3, new KitsuneSoulAttackGoal(this));
        this.goalSelector.addGoal(4, new KitsuneSoulHuntAnimalsGoal(this));
        this.goalSelector.addGoal(5, new KitsuneSoulFleeGoal(this));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true,
                player -> player.getUUID().equals(ownerUUID)));
    }

    @Override
    public void tick() {
        super.tick();

        if (attackCooldown > 0) attackCooldown--;
        if (fleeCooldown > 0) fleeCooldown--;

        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            this.entityData.set(DATA_ATTACK_COOLDOWN, attackCooldown);
            this.entityData.set(DATA_FLEEING, isFleeing);

            if (this.tickCount % 4 == 0) {
                serverLevel.sendParticles(
                        ParticleTypes.SOUL_FIRE_FLAME,
                        this.getX(), this.getY() + 1.0, this.getZ(),
                        2, 0.3, 0.5, 0.3, 0.01);
            }
            if (this.tickCount % 8 == 0) {
                serverLevel.sendParticles(
                        ParticleTypes.SOUL,
                        this.getX(), this.getY() + 0.5, this.getZ(),
                        1, 0.2, 0.2, 0.2, 0.02);
            }
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType reason, @Nullable SpawnGroupData spawnData,
                                        @Nullable CompoundTag dataTag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
        if (dataTag != null && dataTag.contains("OwnerUUID")) {
            this.ownerUUID = dataTag.getUUID("OwnerUUID");
        }
        if (dataTag != null && dataTag.contains("OwnerName")) {
            this.setOwnerName(dataTag.getString("OwnerName"));
        }
        return data;
    }

    public void setupFromPlayer(Player player) {
        this.ownerUUID = player.getUUID();
        this.setOwnerName(player.getName().getString());
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            var textures = serverPlayer.getGameProfile().getProperties().get("textures");
            if (textures != null && !textures.isEmpty()) {
                var tex = textures.iterator().next();
                this.setOwnerTexture(tex.getValue());
                this.setOwnerTextureSlim(tex.getSignature() != null ? tex.getSignature() : "");
            }
        }

        this.setItemSlot(EquipmentSlot.HEAD,
                new ItemStack(ModItems.KITSUNE_SOUL_MASK_0.get()));
        this.setItemSlot(EquipmentSlot.CHEST,
                new ItemStack(ModItems.KITSUNE_SOUL_TAIL.get()));
        storedInventory.clear();
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty()) {
                storedInventory.add(stack.copy());
            }
        }

        this.dropItemsOnDeath = true;
    }
    public void performFireAttack(LivingEntity target) {
        if (attackCooldown > 0) return;

        float healthPercent = this.getHealth() / this.getMaxHealth();
        if (healthPercent < 0.25f) {
            attackCooldown = ATTACK_COOLDOWN_CRITICAL;
        } else if (healthPercent < 0.5f) {
            attackCooldown = ATTACK_COOLDOWN_LOW_HP;
        } else {
            attackCooldown = ATTACK_COOLDOWN_NORMAL;
        }

        this.doHurtTarget(target);
        target.setSecondsOnFire(FIRE_DURATION / 20);

        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    target.getX(), target.getY() + 1, target.getZ(),
                    30, 0.5, 0.5, 0.5, 0.1);
        }

        this.playSound(SoundEvents.SOUL_ESCAPE, 1.0f, 0.8f);
    }

    public boolean canAttack() {
        return attackCooldown <= 0;
    }
    public boolean tryEatFood() {
        if (this.getHealth() >= this.getMaxHealth()) return false;

        for (ItemStack stack : this.getHandSlots()) {
            if (isHealingFood(stack)) {
                this.heal(1.0f);
                stack.shrink(1);
                this.playSound(SoundEvents.GENERIC_EAT, 0.8f, 1.0f);
                this.entityData.set(DATA_EATING, true);

                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HEART,
                            this.getX(), this.getY() + 2, this.getZ(),
                            3, 0.3, 0.3, 0.3, 0.05);
                }
                return true;
            }
        }
        return false;
    }

    private boolean isHealingFood(ItemStack stack) {
        return stack.is(Items.COOKED_BEEF) || stack.is(Items.BEEF)
                || stack.is(Items.COOKED_PORKCHOP) || stack.is(Items.PORKCHOP)
                || stack.is(Items.COOKED_CHICKEN) || stack.is(Items.CHICKEN)
                || stack.is(Items.COOKED_MUTTON) || stack.is(Items.MUTTON)
                || stack.is(Items.COOKED_RABBIT) || stack.is(Items.RABBIT)
                || stack.is(Items.ROTTEN_FLESH);
    }

    public void startFleeing() {
        if (fleeCooldown > 0) return;
        this.isFleeing = true;
        this.fleeCooldown = 200 + this.random.nextInt(200);
    }

    public void stopFleeing() { this.isFleeing = false; }
    public boolean isFleeing() { return this.isFleeing; }
    public boolean canStartFleeing() { return fleeCooldown <= 0; }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 100; i++) {
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        this.getX() + (random.nextDouble() - 0.5) * 2,
                        this.getY() + random.nextDouble() * 2,
                        this.getZ() + (random.nextDouble() - 0.5) * 2,
                        1, 0, 0.1, 0, 0.1);
            }
            for (int i = 0; i < 60; i++) {
                serverLevel.sendParticles(ParticleTypes.SOUL,
                        this.getX() + (random.nextDouble() - 0.5) * 2,
                        this.getY() + random.nextDouble() * 2,
                        this.getZ() + (random.nextDouble() - 0.5) * 2,
                        1, 0, 0.15, 0, 0.05);
            }
            this.playSound(SoundEvents.SOUL_ESCAPE, 1.5f, 0.5f);

            if (dropItemsOnDeath) {
                for (ItemStack stack : storedInventory) {
                    if (!stack.isEmpty()) {
                        this.spawnAtLocation(stack);
                    }
                }
                storedInventory.clear();
            }
        }
        super.die(source);
    }

    @Override
    protected SoundEvent getAmbientSound() { return SoundEvents.ENDERMAN_AMBIENT; }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) { return SoundEvents.ENDERMAN_HURT; }

    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.ENDERMAN_DEATH; }


    public String getOwnerName() { return this.entityData.get(DATA_OWNER_NAME); }
    public void setOwnerName(String name) { this.entityData.set(DATA_OWNER_NAME, name); }

    public String getOwnerTexture() { return this.entityData.get(DATA_OWNER_TEXTURE); }
    public void setOwnerTexture(String texture) { this.entityData.set(DATA_OWNER_TEXTURE, texture); }

    public String getOwnerTextureSlim() { return this.entityData.get(DATA_OWNER_TEXTURE_SLIM); }
    public void setOwnerTextureSlim(String sig) { this.entityData.set(DATA_OWNER_TEXTURE_SLIM, sig); }

    public UUID getOwnerUUID() { return ownerUUID; }

    public boolean isEating() { return this.entityData.get(DATA_EATING); }
    public void setEating(boolean eating) { this.entityData.set(DATA_EATING, eating); }


    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (ownerUUID != null) tag.putUUID("OwnerUUID", ownerUUID);
        tag.putString("OwnerName", getOwnerName());
        tag.putString("OwnerTexture", getOwnerTexture());
        tag.putString("OwnerTextureSlim", getOwnerTextureSlim());
        tag.putInt("AttackCooldown", attackCooldown);
        tag.putBoolean("Fleeing", isFleeing);
        tag.putBoolean("DropItems", dropItemsOnDeath);

        ListTag invList = new ListTag();
        for (ItemStack stack : storedInventory) {
            if (!stack.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                stack.save(itemTag);
                invList.add(itemTag);
            }
        }
        tag.put("StoredInventory", invList);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("OwnerUUID")) ownerUUID = tag.getUUID("OwnerUUID");
        setOwnerName(tag.getString("OwnerName"));
        setOwnerTexture(tag.getString("OwnerTexture"));
        setOwnerTextureSlim(tag.getString("OwnerTextureSlim"));
        attackCooldown = tag.getInt("AttackCooldown");
        isFleeing = tag.getBoolean("Fleeing");
        dropItemsOnDeath = tag.getBoolean("DropItems");
        storedInventory.clear();
        if (tag.contains("StoredInventory")) {
            ListTag invList = tag.getList("StoredInventory", 10);
            for (int i = 0; i < invList.size(); i++) {
                ItemStack stack = ItemStack.of(invList.getCompound(i));
                if (!stack.isEmpty()) storedInventory.add(stack);
            }
        }
    }
}
package net.lisalaf.fantastikmod.entity.custom;

import lombok.Getter;
import lombok.Setter;
import net.lisalaf.fantastikmod.entity.ai.BakenekoGoal;
import net.lisalaf.fantastikmod.entity.dialog.BakenekoDialog;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

@Getter
@Setter
public class BakenekoEntity extends Animal implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // === АНИМАЦИИ И РЕЖИМЫ ===
    private static final EntityDataAccessor<Boolean> DATA_STANDING = SynchedEntityData.defineId(BakenekoEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_ANIMATION_STATE = SynchedEntityData.defineId(BakenekoEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HAS_ITEM = SynchedEntityData.defineId(BakenekoEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_ANGRY = SynchedEntityData.defineId(BakenekoEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDimensions NORMAL_DIMENSIONS = EntityDimensions.fixed(0.6f, 0.6f);
    private static final EntityDimensions STANDING_DIMENSIONS = EntityDimensions.fixed(0.8f, 1.2f);
    private static final EntityDimensions STANDING_WALK_DIMENSIONS = EntityDimensions.fixed(0.9f, 1.2f);


    // Состояния анимации
    public static final int ANIMATION_IDLE = 0;
    public static final int ANIMATION_WALK = 1;
    public static final int ANIMATION_STAND_IDLE = 2;
    public static final int ANIMATION_STAND_WALK = 3;
    public static final int ANIMATION_ATTACK = 4;
    public static final int ANIMATION_SIT_IDLE = 5;
    public static final int ANIMATION_SIT_WASH = 6;
    public static final int ANIMATION_SLEEP = 7;
    public static final int ANIMATION_SLEEP2 = 8;
    public static final int ANIMATION_SLEEP3 = 9;

    private int standCooldown = 0;
    private int animationChangeCooldown = 0;
    private int stealCooldown = 0;
    private int dropCooldown = 0;
    private int angerTime = 0;
    private ItemStack heldItem = ItemStack.EMPTY;
    private final net.minecraft.world.SimpleContainer inventory = new net.minecraft.world.SimpleContainer(9);
    private Player lastThief = null;
    private int sitCooldown = 0;
    private int sleepCooldown = 0;
    private int sitDuration = 0;
    private int sleepDuration = 0;
    private BlockPos sittingPos = null;
    private BlockPos sleepSpot = null;
    private boolean isSitting = false;
    private boolean isSleeping = false;

    public BakenekoEntity(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.refreshDimensions();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_STANDING, false);
        this.entityData.define(DATA_ANIMATION_STATE, ANIMATION_IDLE);
        this.entityData.define(DATA_HAS_ITEM, false);
        this.entityData.define(DATA_IS_ANGRY, false);
    }

    // === ИЗМЕНЯЕМЫЕ ХИТБОКСЫ ===
    @Override
    public void refreshDimensions() {
        super.refreshDimensions();
        if (this.level() != null && !this.level().isClientSide) {
            updateHitbox();
        }
    }

    private void updateHitbox() {
        EntityDimensions dimensions = getDimensions(Pose.STANDING);
        float width = dimensions.width;
        float height = dimensions.height;

        this.setBoundingBox(new AABB(
                this.getX() - width / 2,
                this.getY(),
                this.getZ() - width / 2,
                this.getX() + width / 2,
                this.getY() + height,
                this.getZ() + width / 2
        ));
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        if (isStanding()) {
            return EntityDimensions.fixed(0.8f, 1.2f);
        } else {
            return EntityDimensions.fixed(0.6f, 0.6f);
        }
    }

    private void forceHitboxUpdate() {
        Vec3 oldPos = this.position();

        this.reapplyPosition();

        this.setPos(oldPos);

        this.setBoundingBox(this.getDimensions(this.getPose()).makeBoundingBox(this.position()));
    }


    // Геттеры и сеттеры
    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    public void setSitting(boolean sitting, @Nullable BlockPos pos) {
        this.isSitting = sitting;
        this.sittingPos = pos;
        if (sitting) {
            this.sitDuration = 400 + random.nextInt(1000);
            this.setAnimationState(ANIMATION_SIT_IDLE);
            this.getNavigation().stop();
        } else {
            this.sitDuration = 0;
        }
    }

    public void setSleeping(boolean sleeping, @Nullable BlockPos pos) {
        this.isSleeping = sleeping;
        this.sleepSpot = pos;
        if (sleeping) {
            this.sleepDuration = 600 + random.nextInt(1800);
            int sleepAnim = ANIMATION_SLEEP + random.nextInt(3);
            this.setAnimationState(sleepAnim);
            this.getNavigation().stop();
        } else {
            this.sleepDuration = 0;
        }
    }

    public boolean isSitting() {
        return isSitting;
    }

    public boolean isSleeping() {
        return isSleeping;
    }

    public boolean isStanding() {
        return entityData.get(DATA_STANDING);
    }

    public void setStanding(boolean standing) {
        boolean wasStanding = isStanding();
        entityData.set(DATA_STANDING, standing);
        if (standing) {
            setAnimationState(ANIMATION_STAND_IDLE);
        } else {
            setAnimationState(ANIMATION_IDLE);
        }

        if (wasStanding != standing) {
            if (!this.level().isClientSide) {
                forceHitboxUpdate();
            }
        }
    }

    public int getAnimationState() {
        return entityData.get(DATA_ANIMATION_STATE);
    }

    public void setAnimationState(int state) {
        entityData.set(DATA_ANIMATION_STATE, state);
    }

    public boolean isHoldingItem() {
        return entityData.get(DATA_HAS_ITEM);
    }

    public void setHasItem(boolean hasItem) {
        entityData.set(DATA_HAS_ITEM, hasItem);
    }

    public boolean isAngry() {
        return entityData.get(DATA_IS_ANGRY);
    }

    public void setAngry(boolean angry) {
        entityData.set(DATA_IS_ANGRY, angry);
        if (angry) {
            angerTime = 400;
        }
    }

    // === ОСНОВНОЙ ТИК И ПОВЕДЕНИЕ ===
    @Override
    public void tick() {
        super.tick();
        if (this.level() == null) {
            return;
        }

        if (!this.level().isClientSide) {
            if (this.position() == null || this.blockPosition() == null) {
                return;
            }

            if (isSitting || isSleeping) {
                this.getNavigation().stop();
            }

            if (this.level().isRaining() && this.level().canSeeSky(this.blockPosition())) {
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.4D);
            } else {
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25D);
            }

            if (lastThief != null && this.distanceToSqr(lastThief) > 100.0D && this.tickCount % 100 == 0) {
                angerTime -= 100;
                if (angerTime <= 0) {
                    setAngry(false);
                    lastThief = null;
                }
            }

            updateTimers();
            handleStandingMode();
            handleDoorOpening();
            handleStealing();
            handleItemPickup();
            handleItemDrop();
            handleAnger();

            if (animationChangeCooldown == 1) {
                refreshDimensions();
            }
        }
    }

    private void updateTimers() {
        if (standCooldown > 0) standCooldown--;
        if (animationChangeCooldown > 0) animationChangeCooldown--;
        if (stealCooldown > 0) stealCooldown--;
        if (dropCooldown > 0) dropCooldown--;
        if (angerTime > 0) angerTime--;

        if (angerTime <= 0 && isAngry()) {
            setAngry(false);
        }

        if (!isSitting && !isSleeping && animationChangeCooldown <= 0) {
            boolean isMoving = this.getDeltaMovement().horizontalDistanceSqr() > 0.001D;
            boolean isStanding = isStanding();

            int newAnimationState;
            if (isStanding) {
                newAnimationState = isMoving ? ANIMATION_STAND_WALK : ANIMATION_STAND_IDLE;
            } else {
                newAnimationState = isMoving ? ANIMATION_WALK : ANIMATION_IDLE;
            }

            if (getAnimationState() != newAnimationState) {
                setAnimationState(newAnimationState);
                animationChangeCooldown = 5;
            }
        }

        if (isSitting && sitDuration > 0) {
            sitDuration--;
            if (sitDuration <= 0) {
                setSitting(false, null);
                stopSittingSleepingAnimation();
            } else if (sitDuration % 100 == 0 && random.nextFloat() < 0.3f) {
                setAnimationState(ANIMATION_SIT_WASH);
                animationChangeCooldown = 40;
            } else if (getAnimationState() == ANIMATION_SIT_WASH && animationChangeCooldown <= 0) {
                setAnimationState(ANIMATION_SIT_IDLE);
            }
        }

        if (isSleeping && sleepDuration > 0) {
            sleepDuration--;
            if (sleepDuration <= 0) {
                setSleeping(false, null);
                stopSittingSleepingAnimation();
            }
        }
    }


    private void handleAnger() {
        if (isAngry() && lastThief != null && lastThief.isAlive()) {
            if (lastThief.isCreative() || lastThief.isSpectator()) {
                setAngry(false);
                lastThief = null;
                return;
            }
            if (this.position() == null || lastThief.position() == null) {
                return;
            }

            if (this.distanceToSqr(lastThief) < 64.0D) {
                this.getLookControl().setLookAt(lastThief, 30.0F, 30.0F);
                if (this.distanceToSqr(lastThief) < 4.0D && this.tickCount % 20 == 0) {
                    lastThief.hurt(this.damageSources().mobAttack(this), 4.0F);
                    setAnimationState(ANIMATION_ATTACK);
                    animationChangeCooldown = 10;
                    playSound(SoundEvents.CAT_HISS, 1.0F, 0.5F);
                }
            }

            if (this.distanceToSqr(lastThief) > 100.0D && this.tickCount % 100 == 0) {
                angerTime = Math.max(angerTime - 100, 0);
            }
        } else if (isAngry() && (lastThief == null || !lastThief.isAlive())) {
            setAngry(false);
            lastThief = null;
        }
    }

    private void handleStandingMode() {
        if (standCooldown <= 0 && this.random.nextFloat() < 0.001f && !isAngry()) {
            toggleStandingMode();
            standCooldown = 200 + random.nextInt(800);
        }
    }

    // === ОТКРЫВАНИЕ ДВЕРЕЙ ===
    private void handleDoorOpening() {
        if (!this.level().isClientSide && this.tickCount % 10 == 0) {
            if (this.level() == null || this.position() == null) {
                return;
            }
            Vec3 mobPos = this.position();
            Vec3 lookVec = this.getLookAngle();
            for (int y = 0; y <= 1; y++) {
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && z == 0) continue;

                        BlockPos checkPos = this.blockPosition().offset(x, y, z);
                        BlockState blockState = this.level().getBlockState(checkPos);

                        if (blockState.getBlock() instanceof DoorBlock door) {
                            Vec3 doorCenter = Vec3.atCenterOf(checkPos);
                            Vec3 toDoor = doorCenter.subtract(mobPos).normalize();
                            float dotProduct = (float) lookVec.dot(toDoor);

                            if (dotProduct > 0.3f) {
                                if (!door.isOpen(blockState)) {
                                    door.setOpen(this, this.level(), blockState, checkPos, true);
                                    this.gameEvent(GameEvent.BLOCK_OPEN);
                                    playSound(SoundEvents.WOODEN_DOOR_OPEN, 0.7F, 1.0F);
                                    return;
                                }
                            } else if (door.isOpen(blockState)) {
                                door.setOpen(this, this.level(), blockState, checkPos, false);
                                this.gameEvent(GameEvent.BLOCK_CLOSE);
                                playSound(SoundEvents.WOODEN_DOOR_CLOSE, 0.7F, 1.0F);
                            }
                        }
                    }
                }
            }
        }
    }

    // === ПОДБИРАНИЕ ПРЕДМЕТОВ ===
    private void handleItemPickup() {
        if (this.tickCount % 10 == 0 && !this.isHoldingItem()) {
            List<ItemEntity> items = this.level().getEntitiesOfClass(ItemEntity.class,
                    this.getBoundingBox().inflate(7.0D));

            items.sort((a, b) -> {
                int priorityA = getItemPriority(a.getItem());
                int priorityB = getItemPriority(b.getItem());
                return Integer.compare(priorityB, priorityA);
            });

            for (ItemEntity itemEntity : items) {
                if (!itemEntity.isRemoved() && itemEntity.isAlive()) {
                    ItemStack itemStack = itemEntity.getItem();

                    if (tryPickupItem(itemStack)) {
                        itemEntity.discard();
                        playSound(SoundEvents.ITEM_PICKUP, 0.5F, 1.0F);

                        if (isTreasure(itemStack)) {
                            playSound(SoundEvents.CAT_PURREOW, 0.8F, 1.2F);
                        }
                        break;
                    }
                }
            }
        }
    }

    private int getItemPriority(ItemStack stack) {
        if (isTreasure(stack)) return 90;
        if (isFood(stack)) return 80;
        if (stack.is(Items.STRING) || stack.is(Items.FEATHER) ||
                stack.is(Items.BONE) || stack.is(Items.LEATHER)) return 50;
        return 40;
    }

    private boolean isTreasure(ItemStack stack) {
        return stack.is(Items.EMERALD) || stack.is(Items.DIAMOND) ||
                stack.is(Items.GOLD_INGOT) || stack.is(Items.IRON_INGOT) ||
                stack.is(Items.LAPIS_LAZULI) || stack.is(Items.REDSTONE) ||
                stack.is(Items.QUARTZ) || stack.is(Items.AMETHYST_SHARD);
    }

    private boolean tryPickupItem(ItemStack itemStack) {
        if (!canPickupItem(itemStack)) {
            return false;
        }

        if (this.heldItem.isEmpty()) {
            this.heldItem = itemStack.copy();
            this.heldItem.setCount(1);
            setHasItem(true);
            itemStack.shrink(1);
            return true;
        } else if (addToInventory(itemStack.copy())) {
            itemStack.shrink(1);
            return true;
        }
        return false;
    }

    private boolean canPickupItem(ItemStack itemStack) {
        if (itemStack.is(Items.ROTTEN_FLESH) || itemStack.is(Items.POISONOUS_POTATO)) {
            return false;
        }
        if (isInventoryFull()) {
            return isTreasure(itemStack) || isFood(itemStack);
        }

        return true;
    }

    private boolean addToInventory(ItemStack itemStack) {
        itemStack.setCount(1);

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack slotStack = inventory.getItem(i);
            if (ItemStack.isSameItemSameTags(slotStack, itemStack) &&
                    slotStack.getCount() < slotStack.getMaxStackSize()) {
                slotStack.grow(1);
                return true;
            }
        }

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (inventory.getItem(i).isEmpty()) {
                inventory.setItem(i, itemStack);
                return true;
            }
        }
        return false;
    }

    // ===ВЫКИДЫВАНИЕ ПРЕДМЕТОВ ===
    private void handleItemDrop() {
        if (dropCooldown > 0) return;
        if (isInventoryFull() && this.random.nextFloat() < 0.005f) {
            dropLeastValuableItem();
            dropCooldown = 200;
        }

        if (!this.heldItem.isEmpty() && isFood(this.heldItem)) {
            if (this.getHealth() < this.getMaxHealth()) {
                eatHeldFood();
            } else if (this.random.nextFloat() < 0.4f) {
                dropHeldItem();
            }
        }
    }

    private boolean isInventoryFull() {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (inventory.getItem(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void dropLeastValuableItem() {
        int worstSlot = -1;
        int worstPriority = Integer.MAX_VALUE;

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                int priority = getItemPriority(stack);
                if (priority < worstPriority) {
                    worstPriority = priority;
                    worstSlot = i;
                }
            }
        }

        if (worstSlot != -1) {
            ItemStack toDrop = inventory.getItem(worstSlot).copy();
            spawnAtLocation(toDrop);
            inventory.setItem(worstSlot, ItemStack.EMPTY);
            playSound(SoundEvents.ITEM_PICKUP, 0.5F, 0.8F);
        } else if (!this.heldItem.isEmpty()) {
            dropHeldItem();
        }
    }

    private void dropHeldItem() {
        if (!this.heldItem.isEmpty()) {
            spawnAtLocation(this.heldItem.copy());
            this.heldItem = ItemStack.EMPTY;
            setHasItem(false);
            playSound(SoundEvents.ITEM_PICKUP, 0.5F, 0.8F);
        }
    }

    private void eatHeldFood() {
        if (!this.heldItem.isEmpty() && isFood(this.heldItem)) {
            this.heal(4.0F);
            this.heldItem.shrink(1);
            if (this.heldItem.isEmpty()) {
                setHasItem(false);
            }
            playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);

            if (this.level().isClientSide) {
                for (int i = 0; i < 5; i++) {
                    this.level().addParticle(net.minecraft.core.particles.ParticleTypes.HEART,
                            this.getRandomX(0.5D), this.getRandomY() + 0.5D, this.getRandomZ(0.5D),
                            0, 0.1D, 0);
                }
            }
        }
    }

    // === ВОРОВСТВО ИЗ СУНДУКОВ ===
    private void handleStealing() {
        if (stealCooldown > 0 || this.random.nextFloat() > 0.001f) return;

        BlockPos currentPos = this.blockPosition();
        for (int x = -5; x <= 5; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -5; z <= 5; z++) {
                    BlockPos checkPos = currentPos.offset(x, y, z);
                    BlockState blockState = this.level().getBlockState(checkPos);

                    if (blockState.getBlock() instanceof ChestBlock) {
                        if (tryStealFromChest(checkPos)) {
                            stealCooldown = 6000 + random.nextInt(6000);
                            return;
                        }
                    }
                }
            }
        }
    }

    private boolean tryStealFromChest(BlockPos chestPos) {
        if (this.level().getBlockEntity(chestPos) instanceof ChestBlockEntity chest) {
            net.minecraft.world.Container chestInventory = ChestBlock.getContainer(
                    (ChestBlock) chest.getBlockState().getBlock(),
                    chest.getBlockState(), this.level(), chestPos, false);

            if (chestInventory != null) {
                for (int i = 0; i < chestInventory.getContainerSize(); i++) {
                    ItemStack stack = chestInventory.getItem(i);
                    if (!stack.isEmpty() && (isTreasure(stack) || isFood(stack))) {
                        ItemStack stolen = stack.copy();
                        stolen.setCount(1);

                        if (tryPickupItem(stolen)) {
                            stack.shrink(1);
                            playSound(SoundEvents.CAT_HISS, 0.8F, 0.7F);

                            if (!this.level().isClientSide) {
                                Player nearestPlayer = this.level().getNearestPlayer(this, 10);
                                if (nearestPlayer != null) {
                                    nearestPlayer.sendSystemMessage(Component.literal(
                                            net.lisalaf.fantastikmod.entity.dialog.BakenekoDialog.getStealFromPlayerMessage(nearestPlayer)
                                    ));
                                }
                            }

                            if (this.level().isClientSide) {
                                for (int j = 0; j < 8; j++) {
                                    this.level().addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE,
                                            chestPos.getX() + 0.5, chestPos.getY() + 1.0, chestPos.getZ() + 0.5,
                                            (random.nextDouble() - 0.5) * 0.1, 0.1, (random.nextDouble() - 0.5) * 0.1);
                                }
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // === ВЗАИМОДЕЙСТВИЕ С ИГРОКОМ ===
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (isAngry() && player == lastThief) {
            player.hurt(this.damageSources().mobAttack(this), 2.0F);
            return InteractionResult.FAIL;
        }

        if (isFood(itemstack)) {
            return feed(player, itemstack);
        }

        if (!itemstack.isEmpty() && hand == InteractionHand.MAIN_HAND) {
            return giveItemToBakeneko(player, itemstack);
        }
        if (itemstack.isEmpty() && hand == InteractionHand.MAIN_HAND) {
            return interactWithInventory(player);
        }

        return super.mobInteract(player, hand);
    }

    private InteractionResult interactWithInventory(Player player) {
        if (this.isHoldingItem()) {
            return takeHeldItemFromBakeneko(player);
        }

        if (!this.level().isClientSide) {
            boolean hasItems = false;
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                if (!inventory.getItem(i).isEmpty()) {
                    hasItems = true;
                    break;
                }
            }

            if (hasItems) {
                setAngry(true);
                lastThief = player;
                player.hurt(this.damageSources().mobAttack(this), 3.0F);
                playSound(SoundEvents.CAT_HISS, 1.0F, 0.5F);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    private InteractionResult takeHeldItemFromBakeneko(Player player) {
        if (!this.heldItem.isEmpty()) {
            if (!player.getInventory().add(this.heldItem.copy())) {
                player.drop(this.heldItem.copy(), false);
            }
            this.heldItem = ItemStack.EMPTY;
            setHasItem(false);
            playSound(SoundEvents.CAT_AMBIENT, 0.5F, 1.0F);
            setAngry(true);
            lastThief = player;

            if (!this.level().isClientSide) {
                player.sendSystemMessage(Component.literal(
                        BakenekoDialog.getStolenFromBakenekoMessage(player)
                ));
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return InteractionResult.PASS;
    }

    private InteractionResult giveItemToBakeneko(Player player, ItemStack itemstack) {
        if (tryPickupItem(itemstack.copy())) {
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            playCatSound();
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return InteractionResult.PASS;
    }

    private InteractionResult feed(Player player, ItemStack itemstack) {
        if (this.getHealth() < this.getMaxHealth()) {
            this.heal(4.0F);

            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }

            if (!this.level().isClientSide) {
                player.sendSystemMessage(Component.literal(
                        BakenekoDialog.getFeedMessage(player)
                ));
            }

            playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
            playCatSound();

            if (this.level().isClientSide) {
                for (int i = 0; i < 5; i++) {
                    this.level().addParticle(net.minecraft.core.particles.ParticleTypes.HEART,
                            this.getRandomX(0.5D), this.getRandomY() + 0.5D, this.getRandomZ(0.5D),
                            0, 0.1D, 0);
                }
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, InteractionHand.MAIN_HAND);
    }


    // === ЗВУКИ КОШЕК ===
    private void playCatSound() {
        SoundEvent[] catSounds = {
                SoundEvents.CAT_AMBIENT,
                SoundEvents.CAT_PURR,
                SoundEvents.CAT_PURREOW,
                SoundEvents.CAT_HISS,
                SoundEvents.CAT_BEG_FOR_FOOD
        };

        SoundEvent sound = catSounds[random.nextInt(catSounds.length)];
        float pitch = 0.8F + random.nextFloat() * 0.4F;
        playSound(sound, 0.5F, pitch);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (isAngry()) {
            return SoundEvents.CAT_HISS;
        }
        return random.nextFloat() < 0.7f ? SoundEvents.CAT_AMBIENT : SoundEvents.CAT_PURR;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.CAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.CAT_DEATH;
    }

    // === ИММУНИТЕТ К ОТРАВЛЕНИЮ ===
    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        if (effect.getEffect() == MobEffects.POISON) {
            return false;
        }
        return super.canBeAffected(effect);
    }

    // === ПИТАНИЕ ===
    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.COD) || stack.is(Items.SALMON) || stack.is(Items.TROPICAL_FISH) ||
                stack.is(Items.PUFFERFISH) || stack.is(Items.COOKED_COD) || stack.is(Items.COOKED_SALMON) ||
                stack.is(Items.BEEF) || stack.is(Items.COOKED_BEEF) || stack.is(Items.PORKCHOP) ||
                stack.is(Items.COOKED_PORKCHOP) || stack.is(Items.CHICKEN) || stack.is(Items.COOKED_CHICKEN) ||
                stack.is(Items.MUTTON) || stack.is(Items.COOKED_MUTTON) || stack.is(Items.RABBIT) ||
                stack.is(Items.COOKED_RABBIT) || stack.is(Items.MILK_BUCKET) || stack.is(Items.SPIDER_EYE) ||
                stack.is(Items.FERMENTED_SPIDER_EYE);
    }

    // === ЦЕЛИ AI ===
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D));
        this.goalSelector.addGoal(2, new BakenekoGoal(this, 1.0D, 2.0F, 16.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                (player) -> isAngry() && player == lastThief));

        ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
    }

    public void startSittingAnimation() {
        setAnimationState(ANIMATION_SIT_IDLE);
    }

    public void startSleepingAnimation() {
        int sleepAnim = ANIMATION_SLEEP + random.nextInt(3);
        setAnimationState(sleepAnim);
    }

    public void stopSittingSleepingAnimation() {
        if (isStanding()) {
            setAnimationState(ANIMATION_STAND_IDLE);
        } else {
            setAnimationState(ANIMATION_IDLE);
        }
    }

    // === РАЗМНОЖЕНИЕ ===
    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return null;
    }

    @Override
    public boolean canBreed() {
        return false;
    }

    // === АНИМАЦИИ ===
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "MainController", 0, this::handleAnimations));
    }

    private PlayState handleAnimations(AnimationState<BakenekoEntity> event) {
        int state = getAnimationState();

        switch (state) {
            case ANIMATION_WALK:
                return event.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            case ANIMATION_STAND_IDLE:
                return event.setAndContinue(RawAnimation.begin().thenLoop("stands_paws_idle"));
            case ANIMATION_STAND_WALK:
                return event.setAndContinue(RawAnimation.begin().thenLoop("stands_paws_walk"));
            case ANIMATION_ATTACK:
                return event.setAndContinue(RawAnimation.begin().thenPlay("attack"));
            case ANIMATION_SIT_IDLE:
                return event.setAndContinue(RawAnimation.begin().thenLoop("sit_idle"));
            case ANIMATION_SIT_WASH:
                return event.setAndContinue(RawAnimation.begin().thenPlay("sit_wash"));
            case ANIMATION_SLEEP:
                return event.setAndContinue(RawAnimation.begin().thenLoop("sleep"));
            case ANIMATION_SLEEP2:
                return event.setAndContinue(RawAnimation.begin().thenLoop("sleep2"));
            case ANIMATION_SLEEP3:
                return event.setAndContinue(RawAnimation.begin().thenLoop("sleep3"));
            default:
                return event.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    // === ДОПОЛНИТЕЛЬНЫЕ МЕТОДЫ ===
    public void toggleStandingMode() {
        setStanding(!isStanding());
        playSound(SoundEvents.CAT_PURR, 0.5F, isStanding() ? 0.8F : 1.2F);
    }

    public ItemStack getHeldItem() {
        return heldItem.copy();
    }

    public String getItemDisplayBone() {
        return isStanding() ? "leds2.3" : "mouth";
    }

    // === АТРИБУТЫ ===
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.ARMOR, 4.0D);
    }

    // === СОХРАНЕНИЕ ДАННЫХ ===
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Standing", isStanding());
        compound.putInt("AnimationState", getAnimationState());
        compound.putInt("StandCooldown", standCooldown);
        compound.putInt("StealCooldown", stealCooldown);
        compound.putBoolean("HasItem", isHoldingItem());
        compound.putBoolean("Angry", isAngry());
        compound.putInt("AngerTime", angerTime);
        compound.putBoolean("IsSitting", isSitting);
        compound.putBoolean("IsSleeping", isSleeping);
        compound.putInt("SitDuration", sitDuration);
        compound.putInt("SleepDuration", sleepDuration);
        if (sittingPos != null) {
            compound.putLong("SittingPos", sittingPos.asLong());
        }
        if (sleepSpot != null) {
            compound.putLong("SleepingPos", sleepSpot.asLong());
        }

        if (!this.heldItem.isEmpty()) {
            compound.put("HeldItem", this.heldItem.save(new CompoundTag()));
        }

        net.minecraft.nbt.ListTag items = new net.minecraft.nbt.ListTag();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putByte("Slot", (byte)i);
                stack.save(itemTag);
                items.add(itemTag);
            }
        }
        compound.put("Items", items);
    }



    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Standing")) setStanding(compound.getBoolean("Standing"));
        if (compound.contains("AnimationState")) setAnimationState(compound.getInt("AnimationState"));
        if (compound.contains("StandCooldown")) standCooldown = compound.getInt("StandCooldown");
        if (compound.contains("StealCooldown")) stealCooldown = compound.getInt("StealCooldown");
        if (compound.contains("HasItem")) setHasItem(compound.getBoolean("HasItem"));
        if (compound.contains("Angry")) setAngry(compound.getBoolean("Angry"));
        if (compound.contains("AngerTime")) angerTime = compound.getInt("AngerTime");
        if (compound.contains("IsSitting")) isSitting = compound.getBoolean("IsSitting");
        if (compound.contains("IsSleeping")) isSleeping = compound.getBoolean("IsSleeping");
        if (compound.contains("SitDuration")) sitDuration = compound.getInt("SitDuration");
        if (compound.contains("SleepDuration")) sleepDuration = compound.getInt("SleepDuration");
        if (compound.contains("SittingPos")) sittingPos = BlockPos.of(compound.getLong("SittingPos"));
        if (compound.contains("SleepingPos")) sleepSpot = BlockPos.of(compound.getLong("SleepingPos"));

        if (compound.contains("HeldItem")) {
            this.heldItem = ItemStack.of(compound.getCompound("HeldItem"));
        }

        if (compound.contains("Items", net.minecraft.nbt.Tag.TAG_LIST)) {
            net.minecraft.nbt.ListTag items = compound.getList("Items", net.minecraft.nbt.Tag.TAG_COMPOUND);
            for (int i = 0; i < items.size(); i++) {
                CompoundTag itemTag = items.getCompound(i);
                int slot = itemTag.getByte("Slot") & 255;
                if (slot >= 0 && slot < inventory.getContainerSize()) {
                    inventory.setItem(slot, ItemStack.of(itemTag));
                }
            }
        }

        refreshDimensions();
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        if (!this.level().isClientSide) {
            if (!this.heldItem.isEmpty()) {
                this.spawnAtLocation(this.heldItem);
            }

            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);
                if (!stack.isEmpty()) {
                    this.spawnAtLocation(stack);
                }
            }
        }
        super.remove(reason);
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType reason, @Nullable SpawnGroupData spawnData,
                                        @Nullable CompoundTag dataTag) {
        if (this.random.nextFloat() < 0.3f) {
            setStanding(true);
        }

        if (this.random.nextFloat() < 0.2f) {
            ItemStack[] possibleItems = {
                    new ItemStack(Items.SALMON),
                    new ItemStack(Items.BONE),
                    new ItemStack(Items.STRING),
                    new ItemStack(Items.FEATHER),
                    new ItemStack(Items.EMERALD)
            };
            this.heldItem = possibleItems[random.nextInt(possibleItems.length)];
            setHasItem(true);
        }

        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide && source.getEntity() instanceof Player player) {
            player.sendSystemMessage(Component.literal(
                    BakenekoDialog.getHurtMessage(player)
            ));
        }
        return super.hurt(source, amount);
    }

    public Player getLastThief() {
        return lastThief;
    }

}
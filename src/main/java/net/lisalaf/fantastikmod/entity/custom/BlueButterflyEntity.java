package net.lisalaf.fantastikmod.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BlueButterflyEntity extends FlyingMob implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Float> DATA_SCALE =
            SynchedEntityData.defineId(BlueButterflyEntity.class, EntityDataSerializers.FLOAT);

    public BlueButterflyEntity(EntityType<? extends FlyingMob> entityType, Level level) {
        super(entityType, level);
        this.noCulling = true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SCALE, 1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return FlyingMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 2.0)
                .add(Attributes.FLYING_SPEED, 0.2)
                .add(Attributes.MOVEMENT_SPEED, 0.15);
    }

    @Override
    public void tick() {
        super.tick();

        // Просто летает - движек Minecraft сам обрабатывает полет
        if (!this.level().isClientSide) {
            // Случайное движение
            this.setDeltaMovement(
                    this.getDeltaMovement().add(
                            (this.random.nextDouble() - 0.5) * 0.1,
                            (this.random.nextDouble() - 0.5) * 0.1,
                            (this.random.nextDouble() - 0.5) * 0.1
                    )
            );
        }
    }

    // Геттер для размера
    public float getScale() {
        return this.entityData.get(DATA_SCALE);
    }

    // Установка случайного размера при спавне
    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!this.level().isClientSide) {
            // Случайный размер от 0.5 до 1.5
            float scale = 0.5f + this.random.nextFloat() * 1.0f;
            this.entityData.set(DATA_SCALE, scale);
        }
    }

    // Сохранение и загрузка размера
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("Scale", getScale());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Scale")) {
            this.entityData.set(DATA_SCALE, compound.getFloat("Scale"));
        }
    }

    // ГЕОЛАЙБ МЕТОДЫ
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<BlueButterflyEntity> event) {
        // Используйте правильное имя анимации - просто "fly"
        event.getController().setAnimation(RawAnimation.begin()
                .then("fly", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean isNoGravity() {
        return true; // Бабочка всегда летает
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false; // Не деспавнится
    }
}
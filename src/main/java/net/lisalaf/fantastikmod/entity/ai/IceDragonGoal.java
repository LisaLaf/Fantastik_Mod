package net.lisalaf.fantastikmod.entity.ai;

import net.lisalaf.fantastikmod.entity.custom.IceDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class IceDragonGoal {

    public static class FlyWanderGoal extends Goal {
        private final IceDragonEntity dragon;
        private Vec3 currentTarget = null;
        private int cooldown = 0;
        private int stuckTimer = 0;

        public FlyWanderGoal(IceDragonEntity dragon) {
            this.dragon = dragon;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.dragon.getGrowthStage() == IceDragonEntity.STAGE_BABY) {
                return false;
            }
            return this.dragon.isFlying() && !this.dragon.isTakingOff() && !this.dragon.isLanding();
        }

        @Override
        public void start() {
            currentTarget = getFlightTarget();
            cooldown = 200 + this.dragon.getRandom().nextInt(200); // Увеличиваем время между сменой целей
        }

        @Override
        public void tick() {
            if (currentTarget == null || cooldown <= 0) {
                currentTarget = getFlightTarget();
                cooldown = 200 + this.dragon.getRandom().nextInt(200);
                stuckTimer = 0;
            }

            cooldown--;

            Vec3 currentPos = this.dragon.position();
            double distanceToTarget = currentPos.distanceTo(currentTarget);

            // Если близко к цели или застрял - ищем новую цель
            if (distanceToTarget < 10.0 || stuckTimer > 100) {
                currentTarget = getFlightTarget();
                cooldown = 200 + this.dragon.getRandom().nextInt(200);
                stuckTimer = 0;
                return;
            }

            // Направление к цели
            Vec3 direction = currentTarget.subtract(currentPos).normalize();

            // Более плавное движение
            double speed = 0.2; // Уменьшаем скорость для плавности

            // Плавное изменение движения
            Vec3 currentMotion = this.dragon.getDeltaMovement();
            double motionX = currentMotion.x + (direction.x * speed - currentMotion.x) * 0.1;
            double motionY = currentMotion.y + (direction.y * speed - currentMotion.y) * 0.1;
            double motionZ = currentMotion.z + (direction.z * speed - currentMotion.z) * 0.1;

            this.dragon.setDeltaMovement(motionX, motionY, motionZ);

            // Плавный поворот головы в направлении движения
            if (this.dragon.getDeltaMovement().horizontalDistanceSqr() > 0.01) {
                float targetYaw = (float) Math.toDegrees(Math.atan2(motionX, motionZ));
                this.dragon.setYRot(rotlerp(this.dragon.getYRot(), targetYaw, 5.0F));
            }

            // Проверяем не застрял ли дракон
            if (distanceToTarget > currentPos.distanceTo(this.dragon.position())) {
                stuckTimer++;
            } else {
                stuckTimer = 0;
            }
        }

        private Vec3 getFlightTarget() {
            RandomSource random = this.dragon.getRandom();
            Level level = this.dragon.level();

            // Больший радиус для более разнообразного полета
            double x = this.dragon.getX() + (random.nextDouble() - 0.5) * 60;
            double z = this.dragon.getZ() + (random.nextDouble() - 0.5) * 60;

            // Более широкий диапазон высот
            double currentY = this.dragon.getY();
            double minY = Math.max(level.getSeaLevel() + 5, currentY - 20);
            double maxY = Math.min(level.getMaxBuildHeight() - 5, currentY + 20);
            double y = minY + random.nextDouble() * (maxY - minY);

            return new Vec3(x, y, z);
        }

        private float rotlerp(float current, float target, float maxChange) {
            float f = (target - current) % 360.0F;
            if (f < -180.0F) {
                f += 360.0F;
            }
            if (f >= 180.0F) {
                f -= 360.0F;
            }
            return current + Math.min(Math.max(f, -maxChange), maxChange);
        }

        @Override
        public void stop() {
            currentTarget = null;
            cooldown = 0;
            stuckTimer = 0;
        }
    }

    public static class LandGoal extends Goal {
        private final IceDragonEntity dragon;
        private int landCooldown = 0;

        public LandGoal(IceDragonEntity dragon) {
            this.dragon = dragon;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.dragon.getGrowthStage() == IceDragonEntity.STAGE_BABY) {
                return false;
            }

            if (landCooldown > 0) {
                landCooldown--;
                return false;
            }

            // Решаем приземлиться только если летим и прошло достаточно времени
            return this.dragon.isFlying() && !this.dragon.isTakingOff() && !this.dragon.isLanding() &&
                    this.dragon.getRandom().nextInt(400) == 0 && // Увеличиваем шанс приземления
                    findLandingPosition();
        }

        private boolean findLandingPosition() {
            Level level = this.dragon.level();
            BlockPos currentPos = this.dragon.blockPosition();

            for (int i = 0; i < 10; i++) {
                BlockPos landPos = currentPos.offset(
                        this.dragon.getRandom().nextInt(30) - 15, // Увеличиваем радиус поиска
                        -this.dragon.getRandom().nextInt(10),
                        this.dragon.getRandom().nextInt(30) - 15
                );

                if (isSafeLandingPosition(landPos)) {
                    this.dragon.getNavigation().moveTo(landPos.getX() + 0.5, landPos.getY() + 1, landPos.getZ() + 0.5, 1.0);
                    return true;
                }
            }
            return false;
        }

        private boolean isSafeLandingPosition(BlockPos pos) {
            Level level = this.dragon.level();
            return level.isEmptyBlock(pos.above()) &&
                    level.isEmptyBlock(pos.above(2)) &&
                    !level.isEmptyBlock(pos) &&
                    level.getBlockState(pos).entityCanStandOn(level, pos, this.dragon);
        }

        @Override
        public void start() {
            this.dragon.setLanding(true);
        }

        @Override
        public void stop() {
            if (this.dragon.isLanding()) {
                this.dragon.setFlying(false);
                this.dragon.setLanding(false);
            }
            this.landCooldown = 400; // Увеличиваем кулдаун
        }
    }

    public static class TakeOffGoal extends Goal {
        private final IceDragonEntity dragon;
        private int takeoffCooldown = 0;

        public TakeOffGoal(IceDragonEntity dragon) {
            this.dragon = dragon;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (takeoffCooldown > 0) {
                takeoffCooldown--;
                return false;
            }

            // Только если на земле и не выполняем другие анимации
            return !this.dragon.isFlying() && !this.dragon.isTakingOff() && !this.dragon.isLanding() &&
                    this.dragon.getRandom().nextInt(600) == 0 && // Уменьшаем шанс взлета
                    this.dragon.onGround();
        }

        @Override
        public void start() {
            this.dragon.setTakingOff(true);
        }

        @Override
        public void stop() {
            if (this.dragon.isTakingOff()) {
                this.dragon.setFlying(true);
                this.dragon.setTakingOff(false);
            }
            this.takeoffCooldown = 600; // Увеличиваем кулдаун
        }
    }
}
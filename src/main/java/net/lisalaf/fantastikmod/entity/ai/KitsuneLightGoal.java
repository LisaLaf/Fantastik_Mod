package net.lisalaf.fantastikmod.entity.ai;

import net.lisalaf.fantastikmod.entity.custom.BakenekoEntity;
import net.lisalaf.fantastikmod.entity.custom.KitsuneLightEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class KitsuneLightGoal {

    // === ЦЕЛЬ АТАКИ ===
    public static class KitsuneAttackGoal extends MeleeAttackGoal {
        private final KitsuneLightEntity kitsune;

        public KitsuneAttackGoal(KitsuneLightEntity kitsune, double speed, boolean following) {
            super(kitsune, speed, following);
            this.kitsune = kitsune;
        }

        @Override
        public boolean canUse() {
            if (!canMoveOrAct()) return false;

            LivingEntity target = kitsune.getTarget();
            if (target == null) return false;
            if (target instanceof BakenekoEntity) {
                kitsune.setTarget(null);
                return false;
            }

            if (kitsune.isTamed()) {
                if (target instanceof Player) return false;
                if (!wasProvoked()) return false;
            }

            // Проверка для криперов - атакуем только рядом с деревней или игроком
            if (target instanceof Creeper) {
                if (!isNearVillageOrPlayer()) {
                    return false;
                }
                if (((Creeper) target).isIgnited() || ((Creeper) target).getSwellDir() > 0) {
                    return false;
                }
            }

            return kitsune.isAngry() && !(target instanceof KitsuneLightEntity) && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            if (!canMoveOrAct()) return false;

            LivingEntity target = kitsune.getTarget();
            if (target == null) return false;

            // НИКОГДА не продолжаем атаковать бакэнэко
            if (target instanceof BakenekoEntity) {
                kitsune.setTarget(null);
                return false;
            }

            if (target instanceof Creeper) {
                if (!isNearVillageOrPlayer()) {
                    kitsune.setTarget(null);
                    return false;
                }
                if (((Creeper) target).isIgnited() || ((Creeper) target).getSwellDir() > 0) {
                    return false;
                }
            }

            return kitsune.isAngry() && !(target instanceof KitsuneLightEntity) && super.canContinueToUse();
        }

        private boolean canMoveOrAct() {
            return !kitsune.isSitting() && !kitsune.isSleeping() &&
                    !kitsune.isSitAnimPlaying() && !kitsune.isStandAnimPlaying() &&
                    !kitsune.isSleepAnimPlaying() && !kitsune.isWakeAnimPlaying();
        }

        private boolean wasProvoked() {
            LivingEntity owner = kitsune.getOwner();
            if (owner != null) {
                LivingEntity lastAttacker = owner.getLastHurtByMob();
                if (lastAttacker != null && lastAttacker == kitsune.getTarget()) {
                    return true;
                }
            }

            LivingEntity lastHurt = kitsune.getLastHurtByMob();
            return lastHurt != null && lastHurt == kitsune.getTarget();
        }

        private boolean isNearVillageOrPlayer() {
            List<Villager> villagers = kitsune.level().getEntitiesOfClass(Villager.class,
                    kitsune.getBoundingBox().inflate(32.0D));
            if (!villagers.isEmpty()) return true;

            List<Player> players = kitsune.level().getEntitiesOfClass(Player.class,
                    kitsune.getBoundingBox().inflate(32.0D));
            return !players.isEmpty();
        }
    }

    // === ЗАЩИТА ВЛАДЕЛЬЦА ===
    public static class DefendOwnerGoal extends TargetGoal {
        private final KitsuneLightEntity kitsune;
        private LivingEntity ownerLastHurt;
        private int timestamp;

        public DefendOwnerGoal(KitsuneLightEntity kitsune) {
            super(kitsune, false);
            this.kitsune = kitsune;
            this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            if (!kitsune.isTamed() || kitsune.isSitting() || kitsune.isSleeping()) return false;

            LivingEntity owner = kitsune.getOwner();
            if (owner == null) return false;

            this.ownerLastHurt = owner.getLastHurtByMob();
            if (this.ownerLastHurt == null) return false;

            // Не защищаем от бакэнэко
            if (ownerLastHurt instanceof BakenekoEntity) return false;
            if (ownerLastHurt instanceof Player || ownerLastHurt instanceof KitsuneLightEntity) return false;

            int lastHurtTimestamp = owner.getLastHurtByMobTimestamp();
            return lastHurtTimestamp != this.timestamp && this.canAttack(this.ownerLastHurt);
        }

        private boolean canAttack(LivingEntity target) {
            if (target == null || !target.isAlive()) return false;
            if (target instanceof Player || target instanceof KitsuneLightEntity || target instanceof BakenekoEntity) return false;
            return this.mob.distanceToSqr(target) < 144.0D;
        }

        @Override
        public void start() {
            this.mob.setTarget(this.ownerLastHurt);
            LivingEntity owner = kitsune.getOwner();
            if (owner != null) {
                this.timestamp = owner.getLastHurtByMobTimestamp();
            }
            kitsune.setAngry(true);
            super.start();
        }
    }

    // === ИЗБЕГАНИЕ КРИПЕРА ===
    public static class AvoidCreeperGoal extends Goal {
        private final KitsuneLightEntity kitsune;
        private Creeper avoidTarget;
        private final double sprintSpeedModifier;
        private int cooldown = 0;
        private int avoidDuration = 0;

        public AvoidCreeperGoal(KitsuneLightEntity kitsune, double walkSpeed, double sprintSpeed) {
            this.kitsune = kitsune;
            this.sprintSpeedModifier = 1.8D;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.cooldown > 0) {
                this.cooldown--;
                return false;
            }

            if (kitsune.isSitting() || kitsune.isSleeping()) return false;

            List<Creeper> creepers = this.kitsune.level().getEntitiesOfClass(Creeper.class,
                    this.kitsune.getBoundingBox().inflate(12.0D, 4.0D, 12.0D));

            for (Creeper creeper : creepers) {
                if ((creeper.isIgnited() || creeper.getSwellDir() > 0) &&
                        this.kitsune.distanceToSqr(creeper) < 144.0D) {
                    this.avoidTarget = creeper;
                    this.avoidDuration = 60;
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.avoidTarget != null &&
                    this.avoidTarget.isAlive() &&
                    this.avoidDuration > 0 &&
                    this.kitsune.distanceToSqr(this.avoidTarget) < 256.0D;
        }

        @Override
        public void start() {
            double dx = this.kitsune.getX() - this.avoidTarget.getX();
            double dz = this.kitsune.getZ() - this.avoidTarget.getZ();
            double length = Math.sqrt(dx * dx + dz * dz);

            if (length > 0) {
                double targetX = this.kitsune.getX() + dx / length * 20.0D;
                double targetZ = this.kitsune.getZ() + dz / length * 20.0D;
                this.kitsune.getNavigation().moveTo(targetX, this.kitsune.getY(), targetZ, this.sprintSpeedModifier);
            }

            this.kitsune.setRunningFromCreeper(true);
            this.kitsune.setTarget(null);
        }

        @Override
        public void tick() {
            if (this.avoidDuration > 0) {
                this.avoidDuration--;
            }

            this.kitsune.setRunningFromCreeper(true);

            if (this.avoidTarget != null &&
                    (this.avoidTarget.isIgnited() || this.avoidTarget.getSwellDir() > 0) &&
                    this.kitsune.distanceToSqr(this.avoidTarget) < 100.0D) {

                if (this.kitsune.tickCount % 20 == 0) {
                    this.updateEscapePath();
                }
            }
        }

        @Override
        public void stop() {
            this.kitsune.setRunningFromCreeper(false);
            this.avoidTarget = null;
            this.avoidDuration = 0;
            this.cooldown = 40;
        }

        private void updateEscapePath() {
            if (this.avoidTarget == null) return;

            double dx = this.kitsune.getX() - this.avoidTarget.getX();
            double dz = this.kitsune.getZ() - this.avoidTarget.getZ();
            double length = Math.sqrt(dx * dx + dz * dz);

            if (length > 0) {
                double targetX = this.kitsune.getX() + dx / length * 15.0D;
                double targetZ = this.kitsune.getZ() + dz / length * 15.0D;
                this.kitsune.getNavigation().moveTo(targetX, this.kitsune.getY(), targetZ, this.sprintSpeedModifier);
            }
        }
    }

    // === ПОБЕГ ПРИ НИЗКОМ HP ===
    public static class KitsuneEscapeGoal extends PanicGoal {
        public KitsuneEscapeGoal(KitsuneLightEntity kitsune, double speed) {
            super(kitsune, speed);
        }

        @Override
        public boolean canUse() {
            if (mob.isOnFire() || mob.isFreezing() || mob.getLastHurtByMob() != null) {
                return super.canUse();
            }
            return false;
        }
    }

    // === ЦЕЛЬ ПРИ АТАКЕ НА КИЦУНЭ ===
    public static class KitsuneHurtByTargetGoal extends HurtByTargetGoal {
        public KitsuneHurtByTargetGoal(KitsuneLightEntity kitsune) {
            super(kitsune);
            this.setAlertOthers(KitsuneLightEntity.class);
        }

        @Override
        public boolean canUse() {
            // Не мстим бакэнэко
            if (mob.getLastHurtByMob() instanceof BakenekoEntity) return false;

            return ((KitsuneLightEntity)this.mob).getHitCount() >= 2 && super.canUse();
        }
    }

    // === ЗАЩИТА ЖИТЕЛЕЙ ===
    public static class DefendVillagersGoal extends Goal {
        private final KitsuneLightEntity kitsune;

        public DefendVillagersGoal(KitsuneLightEntity kitsune) {
            this.kitsune = kitsune;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (kitsune.isTamed()) return false;
            if (this.kitsune.getRandom().nextInt(5) != 0) return false;
            if (kitsune.isSitting() || kitsune.isSleeping()) return false;

            List<Villager> villagers = this.kitsune.level().getEntitiesOfClass(Villager.class,
                    this.kitsune.getBoundingBox().inflate(20.0D));

            for (Villager villager : villagers) {
                LivingEntity attacker = villager.getLastHurtByMob();
                if (attacker != null && !(attacker instanceof Player) &&
                        !(attacker instanceof KitsuneLightEntity) &&
                        !(attacker instanceof BakenekoEntity) && attacker.isAlive()) {
                    this.kitsune.setAngry(true);
                    this.kitsune.setTarget(attacker);
                    return true;
                }

                List<Monster> nearbyMonsters = this.kitsune.level().getEntitiesOfClass(Monster.class,
                        villager.getBoundingBox().inflate(8.0D));
                if (!nearbyMonsters.isEmpty()) {
                    Monster monster = nearbyMonsters.get(0);
                    if (monster instanceof Creeper && !isNearVillageOrPlayer()) continue;

                    this.kitsune.setAngry(true);
                    this.kitsune.setTarget(monster);
                    return true;
                }
            }
            return false;
        }

        private boolean isNearVillageOrPlayer() {
            List<Villager> villagers = kitsune.level().getEntitiesOfClass(Villager.class,
                    kitsune.getBoundingBox().inflate(32.0D));
            if (!villagers.isEmpty()) return true;

            List<Player> players = kitsune.level().getEntitiesOfClass(Player.class,
                    kitsune.getBoundingBox().inflate(32.0D));
            return !players.isEmpty();
        }
    }

    // === ЗАЩИТА ИГРОКОВ ===
    public static class ProtectPlayersGoal extends Goal {
        private final KitsuneLightEntity kitsune;

        public ProtectPlayersGoal(KitsuneLightEntity kitsune) {
            this.kitsune = kitsune;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (kitsune.isTamed()) return false;
            if (this.kitsune.getRandom().nextInt(4) != 0) return false;
            if (kitsune.isSitting() || kitsune.isSleeping()) return false;

            List<Player> players = this.kitsune.level().getEntitiesOfClass(Player.class,
                    this.kitsune.getBoundingBox().inflate(16.0D));

            for (Player player : players) {
                LivingEntity attacker = player.getLastHurtByMob();
                if (attacker != null && !(attacker instanceof Player) &&
                        !(attacker instanceof KitsuneLightEntity) &&
                        !(attacker instanceof BakenekoEntity) && attacker.isAlive()) {
                    this.kitsune.setAngry(true);
                    this.kitsune.setTarget(attacker);
                    return true;
                }

                List<Monster> nearbyMonsters = this.kitsune.level().getEntitiesOfClass(Monster.class,
                        player.getBoundingBox().inflate(10.0D));
                if (!nearbyMonsters.isEmpty()) {
                    Monster monster = nearbyMonsters.get(0);
                    if (monster instanceof Creeper && !isNearVillageOrPlayer()) continue;

                    this.kitsune.setAngry(true);
                    this.kitsune.setTarget(monster);
                    return true;
                }
            }
            return false;
        }

        private boolean isNearVillageOrPlayer() {
            List<Villager> villagers = kitsune.level().getEntitiesOfClass(Villager.class,
                    kitsune.getBoundingBox().inflate(32.0D));
            if (!villagers.isEmpty()) return true;

            List<Player> players = kitsune.level().getEntitiesOfClass(Player.class,
                    kitsune.getBoundingBox().inflate(16.0D));
            return !players.isEmpty();
        }
    }

    // === АВТОМАТИЧЕСКАЯ АТАКА МОНСТРОВ ===
    public static class KitsuneDefendVillagersTargetGoal extends NearestAttackableTargetGoal<Monster> {
        public KitsuneDefendVillagersTargetGoal(KitsuneLightEntity kitsune) {
            super(kitsune, Monster.class, 20, true, false, null);
        }

        @Override
        public boolean canUse() {
            Monster target = (Monster) this.target;
            if (target instanceof Creeper && !isNearVillageOrPlayer()) return false;

            return ((KitsuneLightEntity)this.mob).isAngry() && super.canUse();
        }

        private boolean isNearVillageOrPlayer() {
            KitsuneLightEntity kitsune = (KitsuneLightEntity)this.mob;
            List<Villager> villagers = kitsune.level().getEntitiesOfClass(Villager.class,
                    kitsune.getBoundingBox().inflate(32.0D));
            if (!villagers.isEmpty()) return true;

            List<Player> players = kitsune.level().getEntitiesOfClass(Player.class,
                    kitsune.getBoundingBox().inflate(32.0D));
            return !players.isEmpty();
        }
    }

    // === СОЦИАЛЬНОЕ ПОВЕДЕНИЕ ===
    public static class SocialBehaviorGoal extends Goal {
        private final KitsuneLightEntity kitsune;
        private int laughCheckCooldown = 0;
        private int socialCheckCooldown = 0;

        public SocialBehaviorGoal(KitsuneLightEntity kitsune) {
            this.kitsune = kitsune;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return !this.kitsune.isAngry() && this.kitsune.getTarget() == null &&
                    !this.kitsune.isSitting() && !this.kitsune.isSleeping();
        }

        @Override
        public void tick() {
            if (this.kitsune.level().isClientSide) return;

            if (this.laughCheckCooldown > 0) this.laughCheckCooldown--;
            if (this.socialCheckCooldown > 0) this.socialCheckCooldown--;

            if (this.socialCheckCooldown > 0) return;
            this.socialCheckCooldown = 50;

            if (this.laughCheckCooldown == 0) {
                this.laughCheckCooldown = 100 + this.kitsune.getRandom().nextInt(100);

                List<KitsuneLightEntity> nearbyKitsune = this.kitsune.level().getEntitiesOfClass(
                        KitsuneLightEntity.class, this.kitsune.getBoundingBox().inflate(2.0D));

                nearbyKitsune.removeIf(k ->
                        k == this.kitsune ||
                                k.isAngry() ||
                                k.getLaughCooldown() > 0
                );

                if (!nearbyKitsune.isEmpty() && this.kitsune.getRandom().nextFloat() < 0.1F) {
                    this.kitsune.triggerLaughAnimation();

                    int laughCount = Math.min(nearbyKitsune.size(), this.kitsune.getRandom().nextInt(2) + 1);
                    for (int i = 0; i < laughCount; i++) {
                        KitsuneLightEntity otherKitsune = nearbyKitsune.get(i);
                        if (otherKitsune.getRandom().nextFloat() < 0.4F) {
                            otherKitsune.triggerLaughAnimation();
                        }
                    }
                }
            }
        }
    }

    // === СЛЕДОВАНИЕ ЗА ВЛАДЕЛЬЦЕМ ===
    public static class FollowOwnerGoal extends Goal {
        private final KitsuneLightEntity kitsune;
        private LivingEntity owner;
        private final double speedModifier;
        private int timeToRecalcPath;
        private final float stopDistance;
        private final float startDistance;

        public FollowOwnerGoal(KitsuneLightEntity kitsune, double speed, float minDist, float maxDist) {
            this.kitsune = kitsune;
            this.speedModifier = speed;
            this.startDistance = minDist;
            this.stopDistance = maxDist;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (kitsune.getAIMode() != KitsuneLightEntity.AI_FOLLOW) return false;
            if (kitsune.isSitting() || kitsune.isSleeping()) return false;

            LivingEntity livingentity = this.kitsune.getOwner();
            if (livingentity == null) {
                return false;
            } else if (this.kitsune.distanceToSqr(livingentity) < (double)(this.startDistance * this.startDistance)) {
                return false;
            } else {
                this.owner = livingentity;
                return true;
            }
        }

        @Override
        public boolean canContinueToUse() {
            if (kitsune.getAIMode() != KitsuneLightEntity.AI_FOLLOW) return false;
            if (this.kitsune.getNavigation().isDone()) {
                return false;
            } else if (this.kitsune.isSitting()) {
                return false;
            } else {
                return !(this.kitsune.distanceToSqr(this.owner) <= (double)(this.stopDistance * this.stopDistance));
            }
        }

        @Override
        public void start() {
            this.timeToRecalcPath = 0;
        }

        @Override
        public void stop() {
            this.owner = null;
            this.kitsune.getNavigation().stop();
        }

        @Override
        public void tick() {
            this.kitsune.getLookControl().setLookAt(this.owner, 10.0F, (float)this.kitsune.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = 10;
                this.kitsune.getNavigation().moveTo(this.owner, this.speedModifier);
            }
        }
    }

    // === РЕЖИМ БРОДЯЖНИЧЕСТВА ===
    public static class WanderGoal extends Goal {
        private final KitsuneLightEntity kitsune;
        private final double speedModifier;

        public WanderGoal(KitsuneLightEntity kitsune, double speed) {
            this.kitsune = kitsune;
            this.speedModifier = speed;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return kitsune.getAIMode() == KitsuneLightEntity.AI_WANDER &&
                    !kitsune.isSitting() && !kitsune.isSleeping() &&
                    kitsune.getNavigation().isDone() && kitsune.getRandom().nextInt(10) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return kitsune.getAIMode() == KitsuneLightEntity.AI_WANDER &&
                    !kitsune.isSitting() && !kitsune.isSleeping() &&
                    !kitsune.getNavigation().isDone();
        }

        @Override
        public void start() {
            Vec3 vec3 = this.findRandomPosition();
            if (vec3 != null) {
                this.kitsune.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, this.speedModifier);
            }
        }

        @Nullable
        private Vec3 findRandomPosition() {
            Vec3 viewVector = kitsune.getViewVector(0.0F);
            Vec3 randomPos = DefaultRandomPos.getPos(kitsune, 10, 7);
            return randomPos == null ? DefaultRandomPos.getPos(kitsune, 5, 3) : randomPos;
        }
    }

    // === ВЗАИМОДЕЙСТВИЕ С ЖИТЕЛЯМИ ===
    public static class InteractWithVillagersGoal extends Goal {
        private final KitsuneLightEntity kitsune;
        private Villager villagerTarget;
        private int interactCooldown;

        public InteractWithVillagersGoal(KitsuneLightEntity kitsune) {
            this.kitsune = kitsune;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (kitsune.isTamed()) return false;
            if (kitsune.isSitting() || kitsune.isSleeping()) return false;
            if (!kitsune.level().isDay() || kitsune.isAngry()) return false;
            if (kitsune.getRandom().nextInt(100) != 0) return false;

            List<Villager> villagers = kitsune.level().getEntitiesOfClass(Villager.class,
                    kitsune.getBoundingBox().inflate(12.0D));

            if (!villagers.isEmpty()) {
                this.villagerTarget = villagers.get(kitsune.getRandom().nextInt(villagers.size()));
                return true;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.villagerTarget != null &&
                    this.villagerTarget.isAlive() &&
                    kitsune.distanceToSqr(villagerTarget) < 144.0D &&
                    !kitsune.isAngry() &&
                    !kitsune.isSitting() &&
                    !kitsune.isSleeping() &&
                    this.interactCooldown > 0;
        }

        @Override
        public void start() {
            this.interactCooldown = 100 + kitsune.getRandom().nextInt(100);
        }

        @Override
        public void tick() {
            if (this.villagerTarget != null) {
                kitsune.getLookControl().setLookAt(villagerTarget, 10.0F, (float)kitsune.getMaxHeadXRot());

                if (kitsune.distanceToSqr(villagerTarget) > 9.0D) {
                    kitsune.getNavigation().moveTo(villagerTarget, 1.0D);
                } else {
                    kitsune.getNavigation().stop();
                }

                this.interactCooldown--;
            }
        }

        @Override
        public void stop() {
            this.villagerTarget = null;
            this.interactCooldown = 0;
        }
    }

    // === СИДЕНИЕ (для диких) ===
    public static class SitGoal extends Goal {
        private final KitsuneLightEntity kitsune;
        private int sitCheckCooldown;

        public SitGoal(KitsuneLightEntity kitsune) {
            this.kitsune = kitsune;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (kitsune.isTamed()) return false;
            if (sitCheckCooldown > 0) {
                sitCheckCooldown--;
                return false;
            }
            if (kitsune.isSitting() || kitsune.isSleeping()) return false;
            if (kitsune.isStandAnimPlaying() || kitsune.isWakeAnimPlaying()) return false;

            return kitsune.getRandom().nextInt(100) < 5 &&
                    !kitsune.isAngry() &&
                    kitsune.getTarget() == null &&
                    kitsune.onGround();
        }

        @Override
        public boolean canContinueToUse() {
            return kitsune.isSitting() && !kitsune.isStandAnimPlaying() && !kitsune.isTamed();
        }

        @Override
        public void start() {
            kitsune.startSitAnimation();
            sitCheckCooldown = 100 + kitsune.getRandom().nextInt(100);
        }

        @Override
        public void stop() {}
    }

    // === СОН (для диких) ===
    public static class SleepGoal extends Goal {
        private final KitsuneLightEntity kitsune;
        private int sleepCheckCooldown;

        public SleepGoal(KitsuneLightEntity kitsune) {
            this.kitsune = kitsune;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (kitsune.isTamed()) return false;
            if (sleepCheckCooldown > 0) {
                sleepCheckCooldown--;
                return false;
            }
            if (!kitsune.level().isNight()) return false;
            if (kitsune.isSleeping() || kitsune.isSitting()) return false;
            if (kitsune.isStandAnimPlaying() || kitsune.isWakeAnimPlaying()) return false;

            return kitsune.getRandom().nextInt(200) < 10 &&
                    !kitsune.isAngry() &&
                    kitsune.getTarget() == null &&
                    kitsune.onGround();
        }

        @Override
        public boolean canContinueToUse() {
            return kitsune.isSleeping() && !kitsune.isWakeAnimPlaying() && !kitsune.isTamed();
        }

        @Override
        public void start() {
            kitsune.startSleep();
            sleepCheckCooldown = 200 + kitsune.getRandom().nextInt(200);
        }

        @Override
        public void stop() {}
    }

    // === ПОБЕГ ИЗ ВОДЫ ===
    public static class KitsuneEscapeWaterGoal extends Goal {
        private final KitsuneLightEntity kitsune;
        private final double speedModifier;
        private double wantedX;
        private double wantedY;
        private double wantedZ;

        public KitsuneEscapeWaterGoal(KitsuneLightEntity kitsune, double speedModifier) {
            this.kitsune = kitsune;
            this.speedModifier = speedModifier;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!this.kitsune.isInWater()) {
                return false;
            }

            Vec3 landPos = findLandPosition();
            if (landPos == null) {
                return false;
            }

            this.wantedX = landPos.x;
            this.wantedY = landPos.y;
            this.wantedZ = landPos.z;
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return !this.kitsune.getNavigation().isDone() && this.kitsune.isInWater();
        }

        @Override
        public void start() {
            this.kitsune.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
        }

        @Override
        public void tick() {
            if (this.kitsune.isInWater() && this.kitsune.getNavigation().isDone()) {
                Vec3 newLandPos = findLandPosition();
                if (newLandPos != null) {
                    this.wantedX = newLandPos.x;
                    this.wantedY = newLandPos.y;
                    this.wantedZ = newLandPos.z;
                    this.kitsune.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
                }
            }
        }

        @Nullable
        private Vec3 findLandPosition() {
            RandomSource random = this.kitsune.getRandom();
            BlockPos kitsunePos = this.kitsune.blockPosition();

            for (int i = 0; i < 10; i++) {
                BlockPos checkPos = kitsunePos.offset(
                        random.nextInt(16) - 8,
                        random.nextInt(6) - 3,
                        random.nextInt(16) - 8
                );

                if (isDryLand(checkPos) && this.kitsune.getNavigation().isStableDestination(checkPos)) {
                    return Vec3.atBottomCenterOf(checkPos);
                }
            }

            BlockPos surfacePos = findAnyLandSurface();
            if (surfacePos != null) {
                return Vec3.atBottomCenterOf(surfacePos);
            }

            return null;
        }

        private boolean isDryLand(BlockPos pos) {
            return !this.kitsune.level().getFluidState(pos).isSource() &&
                    !this.kitsune.level().getFluidState(pos.above()).isSource() &&
                    this.kitsune.level().getBlockState(pos.below()).isSolid();
        }

        @Nullable
        private BlockPos findAnyLandSurface() {
            BlockPos kitsunePos = this.kitsune.blockPosition();

            for (int radius = 1; radius <= 12; radius++) {
                for (int x = -radius; x <= radius; x++) {
                    for (int z = -radius; z <= radius; z++) {
                        BlockPos checkPos = kitsunePos.offset(x, 0, z);
                        for (int y = -2; y <= 3; y++) {
                            BlockPos verticalPos = checkPos.above(y);
                            if (isDryLand(verticalPos)) {
                                return verticalPos;
                            }
                        }
                    }
                }
            }
            return null;
        }
    }
}
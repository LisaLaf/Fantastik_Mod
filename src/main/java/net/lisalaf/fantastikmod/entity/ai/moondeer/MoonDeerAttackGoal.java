package net.lisalaf.fantastikmod.entity.ai.moondeer;

import net.lisalaf.fantastikmod.entity.custom.MoonDeerEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.BlockTags;

import java.util.EnumSet;

@SuppressWarnings("removal")
public class MoonDeerAttackGoal extends Goal {
    private final MoonDeerEntity deer;
    private LivingEntity target;
    private int attackCooldown = 0;
    private static final int ATTACK_RANGE = 12;
    private static final int PARTICLE_DURATION = 20;

    public MoonDeerAttackGoal(MoonDeerEntity deer) {
        this.deer = deer;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (deer.isTamed()) return false;
        if (!isInMoonForest()) return false;

        LivingEntity attacker = deer.getLastHurtByMob();
        if (attacker != null && attacker.isAlive() && deer.tickCount - deer.getLastHurtByMobTimestamp() < 100) {
            this.target = attacker;
            return true;
        }

        Player nearbyPlayer = deer.level().getNearestPlayer(deer, ATTACK_RANGE);
        if (nearbyPlayer != null && isPlayerBurningForest(nearbyPlayer)) {
            this.target = nearbyPlayer;
            return true;
        }

        return false;
    }

    private boolean isInMoonForest() {
        return deer.level().getBiome(deer.blockPosition()).is(new net.minecraft.resources.ResourceLocation("fantastikmod", "blue_moon_forest"));
    }

    private boolean isPlayerBurningForest(Player player) {
        BlockPos playerPos = player.blockPosition();
        for (int x = -5; x <= 5; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -5; z <= 5; z++) {
                    BlockPos checkPos = playerPos.offset(x, y, z);
                    BlockState state = deer.level().getBlockState(checkPos);
                    if (state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE)) {
                        BlockPos belowPos = checkPos.below();
                        BlockState belowState = deer.level().getBlockState(belowPos);
                        if (belowState.is(BlockTags.LOGS) || belowState.getBlock() instanceof LeavesBlock) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return target != null && target.isAlive() && !deer.isTamed() && isInMoonForest() && attackCooldown < 60;
    }

    @Override
    public void start() {
        attackCooldown = 0;
        deer.setAggressive(true);
    }

    @Override
    public void stop() {
        target = null;
        attackCooldown = 0;
        deer.setAggressive(false);
    }

    @Override
    public void tick() {
        if (target == null) return;

        deer.getLookControl().setLookAt(target, 30.0F, 30.0F);

        double distance = deer.distanceToSqr(target);

        if (attackCooldown > 0) {
            attackCooldown--;
            if (attackCooldown <= PARTICLE_DURATION && deer.level() instanceof ServerLevel serverLevel) {
                spawnHornParticles(serverLevel);
            }
        }

        if (attackCooldown <= 0 && distance <= ATTACK_RANGE * ATTACK_RANGE) {
            performMagicAttack();
            attackCooldown = 40; // 2 секунды между атаками
        }

        if (distance > ATTACK_RANGE * ATTACK_RANGE) {
            deer.getNavigation().moveTo(target, 1.2D);
        } else {
            deer.getNavigation().stop();
        }
    }

    private void spawnHornParticles(ServerLevel level) {
        Vec3 hornPos = deer.getAntlerMiddlePosition();

        for (int i = 0; i < 3; i++) {
            level.sendParticles(ParticleTypes.END_ROD,
                    hornPos.x + (deer.getRandom().nextDouble() - 0.5) * 0.3,
                    hornPos.y + (deer.getRandom().nextDouble() - 0.5) * 0.3,
                    hornPos.z + (deer.getRandom().nextDouble() - 0.5) * 0.3,
                    1, 0.05, 0.05, 0.05, 0.02);
            level.sendParticles(ParticleTypes.SNOWFLAKE,
                    hornPos.x + (deer.getRandom().nextDouble() - 0.5) * 0.4,
                    hornPos.y + (deer.getRandom().nextDouble() - 0.5) * 0.4,
                    hornPos.z + (deer.getRandom().nextDouble() - 0.5) * 0.4,
                    1, 0.1, 0.1, 0.1, 0.05);
        }
    }

    private void performMagicAttack() {
        if (!(deer.level() instanceof ServerLevel serverLevel)) return;

        deer.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.0F, 0.5F);

        Vec3 startPos = deer.getAntlerMiddlePosition();
        Vec3 targetPos = target.position().add(0, target.getBbHeight() * 0.5, 0);
        Vec3 direction = targetPos.subtract(startPos).normalize();

        for (int i = 0; i < 15; i++) {
            Vec3 particlePos = startPos.add(direction.scale(i * 0.5));
            serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                    particlePos.x, particlePos.y, particlePos.z,
                    2, 0.1, 0.1, 0.1, 0.1);
            serverLevel.sendParticles(ParticleTypes.ENCHANT,
                    particlePos.x, particlePos.y, particlePos.z,
                    1, 0.05, 0.05, 0.05, 0.02);
        }

        target.hurt(deer.damageSources().magic(), 4.0F);

        serverLevel.sendParticles(ParticleTypes.END_ROD,
                targetPos.x, targetPos.y, targetPos.z,
                10, 0.3, 0.3, 0.3, 0.1);

        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0));
    }
}
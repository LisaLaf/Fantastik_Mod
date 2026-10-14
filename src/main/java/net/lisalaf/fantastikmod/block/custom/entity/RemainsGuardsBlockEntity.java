package net.lisalaf.fantastikmod.block.custom.entity;

import net.lisalaf.fantastikmod.block.ModBlockEntities;
import net.lisalaf.fantastikmod.entity.ModEntities;
import net.lisalaf.fantastikmod.entity.custom.GhostGuardsEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class RemainsGuardsBlockEntity extends BlockEntity {

    private UUID ghostUUID = null;
    private boolean spawned = false;

    public RemainsGuardsBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REMAINS_GUARDS.get(), pos, state);
    }
    public void serverTick(ServerLevel level, BlockPos pos, BlockState state) {
        if (spawned) return;

        Player nearest = level.getNearestPlayer(
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                16.0D, false);

        if (nearest == null) return;

        if (nearest.isCreative() || nearest.isSpectator()) return;

        spawnGhost(level, pos);
    }

    private void spawnGhost(ServerLevel level, BlockPos pos) {
        GhostGuardsEntity ghost = ModEntities.GHOST_GUARDS.get().create(level);
        if (ghost == null) return;

        double spawnX = pos.getX() + 0.5;
        double spawnY = pos.getY() + 1.0;
        double spawnZ = pos.getZ() + 0.5;

        ghost.moveTo(spawnX, spawnY, spawnZ,
                level.random.nextFloat() * 360F, 0.0F);
        ghost.getPersistentData().putLong("RemainsPos", pos.asLong());
        ghost.setTextureVariant(level.random.nextInt(4));

        level.addFreshEntity(ghost);

        level.sendParticles(ParticleTypes.WHITE_ASH,
                spawnX, spawnY + 0.5, spawnZ,
                40, 0.4, 0.6, 0.4, 0.02);
        level.sendParticles(ParticleTypes.SMOKE,
                spawnX, spawnY + 0.5, spawnZ,
                20, 0.3, 0.5, 0.3, 0.01);

        this.ghostUUID = ghost.getUUID();
        this.spawned = true;
        setChanged();
    }
    public void killLinkedGhost(ServerLevel level) {
        if (ghostUUID == null) return;

        for (GhostGuardsEntity ghost : level.getEntitiesOfClass(
                GhostGuardsEntity.class,
                new net.minecraft.world.phys.AABB(getBlockPos()).inflate(128))) {

            if (ghost.getUUID().equals(ghostUUID)) {
                level.sendParticles(ParticleTypes.WHITE_ASH,
                        ghost.getX(), ghost.getY() + 0.5, ghost.getZ(),
                        60, 0.5, 0.8, 0.5, 0.03);
                level.sendParticles(ParticleTypes.SMOKE,
                        ghost.getX(), ghost.getY() + 0.5, ghost.getZ(),
                        30, 0.4, 0.6, 0.4, 0.02);

                ghost.setHealth(0.0F);
                ghost.die(ghost.damageSources().genericKill());
                break;
            }
        }

        ghostUUID = null;
        setChanged();
    }
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (ghostUUID != null) tag.putUUID("GhostUUID", ghostUUID);
        tag.putBoolean("Spawned", spawned);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.hasUUID("GhostUUID")) ghostUUID = tag.getUUID("GhostUUID");
        spawned = tag.getBoolean("Spawned");
    }

    public UUID getGhostUUID() { return ghostUUID; }
    public boolean isSpawned() { return spawned; }
}
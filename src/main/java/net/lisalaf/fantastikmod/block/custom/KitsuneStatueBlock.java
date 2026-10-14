package net.lisalaf.fantastikmod.block.custom;

import net.lisalaf.fantastikmod.effect.ModEffects;
import net.lisalaf.fantastikmod.entity.custom.KitsuneLightEntity;
import net.lisalaf.fantastikmod.entity.custom.MoonDeerEntity;
import net.lisalaf.fantastikmod.fantastikmod;
import net.lisalaf.fantastikmod.item.ModItems;
import net.lisalaf.fantastikmod.block.ModBlocks;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("removal")
public class KitsuneStatueBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Block.box(3.0, 0.0, 2.0, 13.0, 20.0, 14.0);

    private static final Map<UUID, DialogState> activeDialogs = new HashMap<>();

    private static class DialogState {
        int currentLine = 0;
        boolean isActive = false;

        DialogState() {
            this.currentLine = 0;
            this.isActive = true;
        }
    }

    private static final String[] DIALOG_LINES_KEYS = {
            "dialog.kitsune_statue.line1",
            "dialog.kitsune_statue.line2",
            "dialog.kitsune_statue.line3",
            "dialog.kitsune_statue.line4",
            "dialog.kitsune_statue.line5",
            "dialog.kitsune_statue.line6"
    };

    private static final String[] MOON_DEER_DIALOG_LINES_KEYS = {
            "dialog.moon_deer_statue.line1",
            "dialog.moon_deer_statue.line2",
            "dialog.moon_deer_statue.line3",
            "dialog.moon_deer_statue.line4"
    };

    public KitsuneStatueBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide) {
            boolean hasKitsune = false;
            for (KitsuneLightEntity kitsune : level.getEntitiesOfClass(KitsuneLightEntity.class,
                    player.getBoundingBox().inflate(16))) {
                if (kitsune.isTamed()) {
                    hasKitsune = true;
                    break;
                }
            }

            if (hasKitsune) {
                startDialogWithNearbyKitsune(level, player);
            } else {
                startDialogWithNearbyMoonDeer(level, player);
            }
        }
        super.attack(state, level, pos, player);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemInHand = player.getItemInHand(hand);
        boolean isSakeItem = itemInHand.getItem() == ModItems.SAKE_DRINK.get();
        boolean isRice = itemInHand.getItem() == ModItems.RICE.get();

        if (isRice || isSakeItem) {
            if (!level.isClientSide) {
                int amplifier = 0;
                int duration = 15 * 20;

                if (isSakeItem) {
                    amplifier = 1;
                    duration = 30 * 20;
                }

                MobEffectInstance currentEffect = player.getEffect(ModEffects.INARI_BLESSING.get());
                int newDuration = duration;
                int newAmplifier = amplifier;

                if (currentEffect != null) {
                    newDuration += currentEffect.getDuration();
                    if (newDuration > 180 * 20) newDuration = 180 * 20;
                    if (currentEffect.getAmplifier() >= amplifier) {
                        newAmplifier = currentEffect.getAmplifier();
                    }
                }

                player.addEffect(new MobEffectInstance(ModEffects.INARI_BLESSING.get(), newDuration, newAmplifier));

                if (!player.isCreative()) {
                    itemInHand.shrink(1);
                }

                level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 1.0f, 1.0f);

            }
            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide) {
            boolean hasKitsune = false;
            for (KitsuneLightEntity kitsune : level.getEntitiesOfClass(KitsuneLightEntity.class,
                    player.getBoundingBox().inflate(16))) {
                if (kitsune.isTamed()) {
                    hasKitsune = true;
                    break;
                }
            }

            if (hasKitsune) {
                startDialogWithNearbyKitsune(level, player);
            } else {
                startDialogWithNearbyMoonDeer(level, player);
            }

            if (player instanceof ServerPlayer serverPlayer) {
                Advancement advancement = serverPlayer.server.getAdvancements()
                        .getAdvancement(new ResourceLocation(fantastikmod.MOD_ID, "inari_blessing"));
                if (advancement != null) {
                    serverPlayer.getAdvancements().award(advancement, "make_offering");
                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    private void startDialogWithNearbyKitsune(Level level, Player player) {
        KitsuneLightEntity nearbyKitsune = null;
        double closestDistance = 16.0;

        for (KitsuneLightEntity kitsune : level.getEntitiesOfClass(KitsuneLightEntity.class,
                player.getBoundingBox().inflate(16))) {
            if (kitsune.isTamed()) {
                double distance = player.distanceTo(kitsune);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    nearbyKitsune = kitsune;
                }
            }
        }

        if (nearbyKitsune != null) {
            if (activeDialogs.containsKey(player.getUUID()) && activeDialogs.get(player.getUUID()).isActive) {
                return;
            }

            DialogState dialogState = new DialogState();
            activeDialogs.put(player.getUUID(), dialogState);

            sendNextDialogLine(level, player, nearbyKitsune, dialogState);

            if (level instanceof ServerLevel serverLevel) {
                scheduleNextDialog(serverLevel, player, nearbyKitsune, dialogState, 1);
            }
        }
    }

    private void scheduleNextDialog(ServerLevel level, Player player, KitsuneLightEntity kitsune, DialogState state, int nextLineIndex) {
        new Thread(() -> {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                return;
            }

            level.getServer().execute(() -> {
                if (!state.isActive || player.isRemoved() || !player.isAlive()) {
                    activeDialogs.remove(player.getUUID());
                    return;
                }

                state.currentLine = nextLineIndex;

                if (state.currentLine < DIALOG_LINES_KEYS.length) {
                    sendNextDialogLine(level, player, kitsune, state);
                    scheduleNextDialog(level, player, kitsune, state, state.currentLine + 1);
                } else {
                    state.isActive = false;
                    activeDialogs.remove(player.getUUID());
                }
            });
        }).start();
    }

    private void sendNextDialogLine(Level level, Player player, KitsuneLightEntity kitsune, DialogState state) {
        if (state.currentLine >= DIALOG_LINES_KEYS.length) return;

        String kitsuneName = kitsune.getCustomName() != null
                ? kitsune.getCustomName().getString()
                : Component.translatable("entity.fantastikmod.kitsune_light").getString();

        Component line = Component.translatable(DIALOG_LINES_KEYS[state.currentLine]);

        Component message = Component.literal(kitsuneName + ": ").append(line);
        player.sendSystemMessage(message);
    }


    private void startDialogWithNearbyMoonDeer(Level level, Player player) {
        MoonDeerEntity nearbyMoonDeer = null;
        double closestDistance = 16.0;

        for (MoonDeerEntity moonDeer : level.getEntitiesOfClass(MoonDeerEntity.class,
                player.getBoundingBox().inflate(16))) {
            if (moonDeer.isTamed()) {
                double distance = player.distanceTo(moonDeer);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    nearbyMoonDeer = moonDeer;
                }
            }
        }

        if (nearbyMoonDeer != null) {
            if (activeDialogs.containsKey(player.getUUID()) && activeDialogs.get(player.getUUID()).isActive) {
                return;
            }

            DialogState dialogState = new DialogState();
            activeDialogs.put(player.getUUID(), dialogState);

            sendNextMoonDeerDialogLine(level, player, nearbyMoonDeer, dialogState);

            if (level instanceof ServerLevel serverLevel) {
                scheduleNextMoonDeerDialog(serverLevel, player, nearbyMoonDeer, dialogState, 1);
            }
        }
    }

    private void scheduleNextMoonDeerDialog(ServerLevel level, Player player, MoonDeerEntity moonDeer, DialogState state, int nextLineIndex) {
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                return;
            }

            level.getServer().execute(() -> {
                if (!state.isActive || player.isRemoved() || !player.isAlive()) {
                    activeDialogs.remove(player.getUUID());
                    return;
                }

                state.currentLine = nextLineIndex;

                if (state.currentLine < MOON_DEER_DIALOG_LINES_KEYS.length) {
                    sendNextMoonDeerDialogLine(level, player, moonDeer, state);
                    scheduleNextMoonDeerDialog(level, player, moonDeer, state, state.currentLine + 1);
                } else {
                    state.isActive = false;
                    activeDialogs.remove(player.getUUID());
                }
            });
        }).start();
    }

    private void sendNextMoonDeerDialogLine(Level level, Player player, MoonDeerEntity moonDeer, DialogState state) {
        if (state.currentLine >= MOON_DEER_DIALOG_LINES_KEYS.length) return;

        String deerName = moonDeer.getCustomName() != null
                ? moonDeer.getCustomName().getString()
                : Component.translatable("entity.fantastikmod.moon_deer").getString();

        Component line = Component.translatable(MOON_DEER_DIALOG_LINES_KEYS[state.currentLine]);

        Component message = Component.literal(deerName + ": ").append(line);
        player.sendSystemMessage(message);
    }
}
package net.lisalaf.fantastikmod.block.custom;

import net.lisalaf.fantastikmod.block.custom.entity.RemainsGuardsBlockEntity;
import net.lisalaf.fantastikmod.entity.ModEntities;
import net.lisalaf.fantastikmod.entity.custom.GhostGuardsEntity;
import net.lisalaf.fantastikmod.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class RemainsGuardsBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 18.0, 14.0);
    private final Random random = new Random();

    public RemainsGuardsBlock(Properties properties) {
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
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RemainsGuardsBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return (lvl, pos, st, be) -> {
            if (be instanceof RemainsGuardsBlockEntity remains) {
                remains.serverTick((ServerLevel) lvl, pos, st);
            }
        };
    }
    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && !player.isCreative()) {
            popResource(level, pos, new ItemStack(Items.BONE, 1 + random.nextInt(3)));
            popResource(level, pos, new ItemStack(Items.BONE_MEAL, 2 + random.nextInt(4)));
            if (random.nextFloat() < 0.2f) {
                ItemStack[] rustyPieces = {
                        new ItemStack(ModItems.RUSTY_HELMET.get()),
                        new ItemStack(ModItems.RUSTY_CHESTPLATE.get()),
                        new ItemStack(ModItems.RUSTY_LEGGINGS.get()),
                        new ItemStack(ModItems.RUSTY_BOOTS.get())
                };
                popResource(level, pos, rustyPieces[random.nextInt(rustyPieces.length)]);
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state,
                              @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (!level.isClientSide) {
            level.playSound(null, pos, SoundEvents.SKELETON_DEATH, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (level instanceof ServerLevel serverLevel) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof RemainsGuardsBlockEntity remains) {
                    remains.killLinkedGhost(serverLevel);
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide) {
            level.playSound(null, pos, SoundEvents.SKELETON_HURT, SoundSource.BLOCKS, 0.8f, 1.0f);
        }
        super.attack(state, level, pos, player);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            level.playSound(null, pos, SoundEvents.SKELETON_AMBIENT, SoundSource.BLOCKS, 0.6f, 1.0f);
        }
        return InteractionResult.SUCCESS;
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

}
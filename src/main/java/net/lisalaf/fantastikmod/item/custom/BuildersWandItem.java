package net.lisalaf.fantastikmod.item.custom;

import net.lisalaf.fantastikmod.block.custom.RoofCapBlock;
import net.lisalaf.fantastikmod.block.custom.RoofTileBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StairsShape;

public class BuildersWandItem extends Item {

    public BuildersWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof RoofTileBlock) {
            if (!level.isClientSide) {
                StairsShape current = state.getValue(RoofTileBlock.SHAPE);
                StairsShape next = switch (current) {
                    case STRAIGHT -> StairsShape.INNER_LEFT;
                    case INNER_LEFT -> StairsShape.OUTER_LEFT;
                    case OUTER_LEFT -> StairsShape.OUTER_RIGHT;
                    case OUTER_RIGHT -> StairsShape.INNER_RIGHT;
                    default -> StairsShape.STRAIGHT;
                };
                level.setBlock(pos, state.setValue(RoofTileBlock.SHAPE, next), 3);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (state.getBlock() instanceof RoofCapBlock) {
            if (!level.isClientSide) {
                int current = state.getValue(RoofCapBlock.SHAPE);
                int next = (current + 1) % 3;
                level.setBlock(pos, state.setValue(RoofCapBlock.SHAPE, next), 3);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

}
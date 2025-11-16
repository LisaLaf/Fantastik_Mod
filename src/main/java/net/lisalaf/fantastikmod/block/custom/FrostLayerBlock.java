package net.lisalaf.fantastikmod.block.custom;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FrostLayerBlock extends SnowLayerBlock {
    public FrostLayerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return true; // Можно накладывать
    }
}

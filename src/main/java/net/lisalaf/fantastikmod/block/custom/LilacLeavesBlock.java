package net.lisalaf.fantastikmod.block.custom;

import net.lisalaf.fantastikmod.effect.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;

public class LilacLeavesBlock extends LeavesBlock {

    public static final BooleanProperty FLOWERING = BooleanProperty.create("flowering");

    public LilacLeavesBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(DISTANCE, 7)
                .setValue(PERSISTENT, false)
                .setValue(WATERLOGGED, false)
                .setValue(FLOWERING, false));
    }

    @Override
    protected boolean decaying(BlockState state) {
        return false;
    }
    @Override
    public void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
        super.spawnAfterBreak(state, level, pos, tool, dropExperience);
        if (tool.getItem() == Items.SHEARS) {
            popResource(level, pos, new ItemStack(this));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FLOWERING);
    }

    public static boolean isFloweringDay(Level level) {
        long day = level.getDayTime() / 24000L;
        long cycleDay = day % 120;
        return cycleDay < 30;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        boolean shouldFlower = isFloweringDay(level);
        if (state.getValue(FLOWERING) != shouldFlower) {
            level.setBlock(pos, state.setValue(FLOWERING, shouldFlower), 3);
        }

        // Эффект при каждом randomTick'е с долгой длительностью
        if (state.getValue(FLOWERING)) {
            AABB area = new AABB(pos).inflate(7);
            for (Player player : level.getEntitiesOfClass(Player.class, area)) {
                player.addEffect(new MobEffectInstance(ModEffects.LILAC_AROMA.get(), 1200, 0, false, false, true));
            }
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (level instanceof ServerLevel serverLevel) {
            level.scheduleTick(pos, this, 1);
        }
    }
}
package net.lisalaf.fantastikmod.block.custom.entity;

import net.lisalaf.fantastikmod.block.ModBlockEntities;
import net.lisalaf.fantastikmod.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

import static net.lisalaf.fantastikmod.block.ModBlockEntities.BLOCK_ENTITIES;

public class TapestryBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private String textureName = "tapestry_castle_0";

    public String getTextureName() { return textureName; }
    public void setTextureName(String name) { this.textureName = name; setChanged(); }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("TextureName", textureName);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        textureName = tag.getString("TextureName");
    }

    public TapestryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TAPESTRY.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<TapestryBlockEntity> state) {
        return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object blockEntity) {
        return RenderUtils.getCurrentTick();
    }

}
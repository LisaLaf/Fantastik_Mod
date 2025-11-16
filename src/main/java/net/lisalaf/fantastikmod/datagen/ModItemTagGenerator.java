package net.lisalaf.fantastikmod.datagen;

import net.lisalaf.fantastikmod.block.ModBlocks;
import net.lisalaf.fantastikmod.fantastikmod;
import net.lisalaf.fantastikmod.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends ItemTagsProvider {

    public ModItemTagGenerator(PackOutput output,
                               CompletableFuture<HolderLookup.Provider> lookupProvider,
                               CompletableFuture<TagLookup<Block>> blockTags,
                               @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, fantastikmod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        this.tag(ItemTags.TRIMMABLE_ARMOR)
                .add(ModItems.AURIPIGMENT_HELMET.get(),
                        ModItems.AURIPIGMENT_CHESTPLATE.get(),
                        ModItems.AURIPIGMENT_LEGGINGS.get(),
                        ModItems.AURIPIGMENT_BOOTS.get());

        this.tag(ItemTags.TRIMMABLE_ARMOR)
                .add(ModItems.HEART_ICE_DRAGON.get(),
                        ModItems.CHESTPLATE_FUR_ICE_DRAGON.get(),
                        ModItems.LEGGINGS_FUR_ICE_DRAGON.get(),
                        ModItems.BOOTS_FUR_ICE_DRAGON.get());

        this.tag(ItemTags.MUSIC_DISCS)
                .add(ModItems.TEA_CEREMONY_MUSIC_DISC.get());

        this.tag(ItemTags.PLANKS)
                .add(ModBlocks.MOON_PLANKS.get().asItem());

        this.tag(ItemTags.LOGS_THAT_BURN)
                .add(ModBlocks.TREE_MOON_LOG_BLOCK.get().asItem());


    }
}
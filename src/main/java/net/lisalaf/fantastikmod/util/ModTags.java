package net.lisalaf.fantastikmod.util;

import net.lisalaf.fantastikmod.fantastikmod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ModTags {
    @SuppressWarnings("removal")
    public static class Blocks {
        public static final TagKey<Block> NEEDS_AURIPIGMENT_TOOL = tag("needs_auripigment_tool");
        public static final TagKey<Block> NEEDS_SILVER_TOOL = tag("needs_silver_tool");
        public static final TagKey<Block> NEEDS_GEM_MOON_TOOL = tag("needs_gem_moon_tool");



        private static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(fantastikmod.MOD_ID, name));
        }
    }

    public static class Items {
        private static TagKey<Item> tag(String name) {
            //noinspection removal
            return ItemTags.create(new ResourceLocation(fantastikmod.MOD_ID, name));
        }
    }
}
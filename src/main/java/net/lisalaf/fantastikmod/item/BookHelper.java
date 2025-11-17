package net.lisalaf.fantastikmod.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BookHelper {

    public static ItemStack createNoteBook(String title, String pageText) {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        CompoundTag tag = new CompoundTag();

        ListTag pages = new ListTag();
        pages.add(StringTag.valueOf("{\"text\":\"" + pageText + "\"}"));

        tag.putString("title", title);
        tag.putString("author", "Villager");
        tag.put("pages", pages);
        tag.putBoolean("resolved", true);

        book.setTag(tag);
        return book;
    }

    public static ItemStack getNote1() {
        return createNoteBook("Trader Note",
                "This wandering trader tried to sell me some kind of moon flower from some Moon Forest or Blue Forest... I didn't even listen. For 5 emeralds, it's better to buy something useful than a glowing flower from some unknown forest.");
    }

    public static ItemStack getNote2() {
        return createNoteBook("Dragon Fur Note",
                "Recently a trader came and sold dragon fur for 20 EMERALDS... A really rare item... But the price... It seems easier to go search for it in the snow biomes myself than to buy enough for good armor.");
    }

    public static ItemStack getNote3() {
        return createNoteBook("Kitsune Note",
                "Kitsune... Extremely unusual foxes. They talk and even protect from monsters... I wonder where they came from? I heard from one traveler about a goddess, Inari I think she was called. They said it was she who sent the kitsune into this world. How true is this?");
    }

    public static ItemStack getNote4() {
        return createNoteBook("Night Sounds Note",
                "At night there was a terrible howl and the sound of hooves... Like horses, but not quite...");
    }

    public static ItemStack getNote5() {
        return createNoteBook("Prophecy Note",
                "And the end of all things will come when two brothers swallow the sun and moon... What was that traveler talking about?");
    }

    public static ItemStack getNote6() {
        return createNoteBook("Tofu Note",
                "Such powerful creatures and they adore simple tofu? I thought that the stronger and older they are - and these foxes have lived here twice as long as me - the more refined their tastes would be... But that's how it turns out.");
    }

    public static ItemStack getCorrespondingBook(Item item) {
        if (item == ModItems.NOTE_1.get()) return getNote1();
        if (item == ModItems.NOTE_2.get()) return getNote2();
        if (item == ModItems.NOTE_3.get()) return getNote3();
        if (item == ModItems.NOTE_4.get()) return getNote4();
        if (item == ModItems.NOTE_5.get()) return getNote5();
        if (item == ModItems.NOTE_6.get()) return getNote6();
        return ItemStack.EMPTY;
    }
}
package net.lisalaf.fantastikmod.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;

public class BookHelper {

    public static ItemStack createNoteBook(String titleKey, String pageTextKey) {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        CompoundTag tag = new CompoundTag();

        ListTag pages = new ListTag();

        String jsonText = "{\"text\":\"" + Component.translatable(pageTextKey).getString() + "\"}";
        pages.add(StringTag.valueOf(jsonText));

        tag.putString("title", Component.translatable(titleKey).getString());
        tag.putString("author", Component.translatable("book.fantastikmod.author").getString());
        tag.put("pages", pages);
        tag.putBoolean("resolved", true);

        book.setTag(tag);
        return book;
    }

    public static ItemStack createMultiPageBook(String titleKey, String... pageTextKeys) {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        CompoundTag tag = new CompoundTag();

        ListTag pages = new ListTag();

        for (String pageKey : pageTextKeys) {
            String jsonText = "{\"text\":\"" + Component.translatable(pageKey).getString() + "\"}";
            pages.add(StringTag.valueOf(jsonText));
        }

        tag.putString("title", Component.translatable(titleKey).getString());
        tag.putString("author", Component.translatable("book.fantastikmod.author").getString());
        tag.put("pages", pages);
        tag.putBoolean("resolved", true);

        book.setTag(tag);
        return book;
    }

    public static ItemStack getNote1() {
        return createNoteBook("book.fantastikmod.note1.title", "book.fantastikmod.note1.page1");
    }

    public static ItemStack getNote2() {
        return createNoteBook("book.fantastikmod.note2.title", "book.fantastikmod.note2.page1");
    }

    public static ItemStack getNote3() {
        return createNoteBook("book.fantastikmod.note3.title", "book.fantastikmod.note3.page1");
    }

    public static ItemStack getNote4() {
        return createNoteBook("book.fantastikmod.note4.title", "book.fantastikmod.note4.page1");
    }

    public static ItemStack getNote5() {
        return createNoteBook("book.fantastikmod.note5.title", "book.fantastikmod.note5.page1");
    }

    public static ItemStack getNote6() {
        return createNoteBook("book.fantastikmod.note6.title", "book.fantastikmod.note6.page1");
    }

    //ЗАПИСКИ ПРИНЦЕССЫ МИРАБЕЛЛЫ
    public static ItemStack getPrincessNotes() {
        return createMultiPageBook(
                "book.fantastikmod.princess_notes.title",
                "book.fantastikmod.princess_notes.page1",
                "book.fantastikmod.princess_notes.page2",
                "book.fantastikmod.princess_notes.page3"
        );
    }

    //ЗАПИСКИ КОРОЛЯ ВАЛЬДЕМАРА
    public static ItemStack getKingNotes() {
        return createMultiPageBook(
                "book.fantastikmod.king_notes.title",
                "book.fantastikmod.king_notes.page1",
                "book.fantastikmod.king_notes.page2",
                "book.fantastikmod.king_notes.page3"
        );
    }

    // ЗАПИСИ ИССЛЕДОВАТЕЛЯ ДРАКОНОВ
    public static ItemStack getDragonScholarNotes() {
        return createMultiPageBook(
                "book.fantastikmod.dragon_scholar_notes.title",
                "book.fantastikmod.dragon_scholar_notes.page1",
                "book.fantastikmod.dragon_scholar_notes.page2",
                "book.fantastikmod.dragon_scholar_notes.page3",
                "book.fantastikmod.dragon_scholar_notes.page4"
        );
    }

    public static ItemStack getCorrespondingBook(Item item) {
        if (item == ModItems.NOTE_1.get()) return getNote1();
        if (item == ModItems.NOTE_2.get()) return getNote2();
        if (item == ModItems.NOTE_3.get()) return getNote3();
        if (item == ModItems.NOTE_4.get()) return getNote4();
        if (item == ModItems.NOTE_5.get()) return getNote5();
        if (item == ModItems.NOTE_6.get()) return getNote6();
        if (item == ModItems.NOTE_PRINCESS.get()) return getPrincessNotes();
        if (item == ModItems.NOTE_KING.get()) return getKingNotes();
        if (item == ModItems.NOTE_DRAGON_SCHOLAR.get()) return getDragonScholarNotes();
        return ItemStack.EMPTY;
    }
}
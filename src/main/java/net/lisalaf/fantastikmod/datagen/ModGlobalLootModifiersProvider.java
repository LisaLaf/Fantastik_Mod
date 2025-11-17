package net.lisalaf.fantastikmod.datagen;

import net.lisalaf.fantastikmod.datagen.loot.AddItemModifier;
import net.lisalaf.fantastikmod.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;

public class ModGlobalLootModifiersProvider extends GlobalLootModifierProvider {
    public ModGlobalLootModifiersProvider(PackOutput output) {
        super(output, "fantastikmod");
    }

    @Override
    protected void start() {
        // Для каждой деревни создаем свой модификатор
        addVillageNotes("village_plains_notes", BuiltInLootTables.VILLAGE_PLAINS_HOUSE);
        addVillageNotes("village_desert_notes", BuiltInLootTables.VILLAGE_DESERT_HOUSE);
        addVillageNotes("village_savanna_notes", BuiltInLootTables.VILLAGE_SAVANNA_HOUSE);
        addVillageNotes("village_snowy_notes", BuiltInLootTables.VILLAGE_SNOWY_HOUSE);
        addVillageNotes("village_taiga_notes", BuiltInLootTables.VILLAGE_TAIGA_HOUSE);
    }

    private void addVillageNotes(String name, ResourceLocation lootTable) {
        add(name + "_1", new AddItemModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(lootTable).build(),
                        LootItemRandomChanceCondition.randomChance(0.3f).build()
                }, ModItems.NOTE_1.get()));

        add(name + "_2", new AddItemModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(lootTable).build(),
                        LootItemRandomChanceCondition.randomChance(0.3f).build()
                }, ModItems.NOTE_2.get()));

        add(name + "_3", new AddItemModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(lootTable).build(),
                        LootItemRandomChanceCondition.randomChance(0.3f).build()
                }, ModItems.NOTE_3.get()));

        add(name + "_4", new AddItemModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(lootTable).build(),
                        LootItemRandomChanceCondition.randomChance(0.3f).build()
                }, ModItems.NOTE_4.get()));

        add(name + "_5", new AddItemModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(lootTable).build(),
                        LootItemRandomChanceCondition.randomChance(0.3f).build()
                }, ModItems.NOTE_5.get()));

        add(name + "_6", new AddItemModifier(
                new LootItemCondition[] {
                        LootTableIdCondition.builder(lootTable).build(),
                        LootItemRandomChanceCondition.randomChance(0.3f).build()
                }, ModItems.NOTE_6.get()));
    }
}
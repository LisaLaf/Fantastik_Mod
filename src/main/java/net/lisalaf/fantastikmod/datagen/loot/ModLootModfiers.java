package net.lisalaf.fantastikmod.datagen.loot;

import com.mojang.serialization.Codec;
import net.lisalaf.fantastikmod.fantastikmod;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModLootModfiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>>  LOOT_MODIIFER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, fantastikmod.MOD_ID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_ITEM =
            LOOT_MODIIFER_SERIALIZERS.register("add_item", AddItemModifier.CODEC);

    public static void register(IEventBus eventBus) {
        LOOT_MODIIFER_SERIALIZERS.register(eventBus);
    }
}

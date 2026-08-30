package net.lisalaf.fantastikmod.potion;

import net.lisalaf.fantastikmod.effect.ModEffects;
import net.lisalaf.fantastikmod.fantastikmod;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(ForgeRegistries.POTIONS, fantastikmod.MOD_ID);

    public static final RegistryObject<Potion> LILAC_BRANCH = POTIONS.register("lilac_branch",
            () -> new Potion(
                    new MobEffectInstance(ModEffects.LILAC_AROMA.get(), 3600, 0),
                    new MobEffectInstance(MobEffects.POISON, 3200, 0)
            ));

    public static final RegistryObject<Potion> LILAC_BRANCH_LONG = POTIONS.register("lilac_branch_long",
            () -> new Potion("lilac_branch",
                    new MobEffectInstance(ModEffects.LILAC_AROMA.get(), 9600, 0)
            ));

    public static final RegistryObject<Potion> LILAC_BRANCH_SPLASH = POTIONS.register("lilac_branch_splash",
            () -> new Potion(
                    new MobEffectInstance(ModEffects.LILAC_AROMA.get(), 3600, 0)
            ));

    public static final RegistryObject<Potion> LILAC_BRANCH_LINGERING = POTIONS.register("lilac_branch_lingering",
            () -> new Potion(
                    new MobEffectInstance(ModEffects.LILAC_AROMA.get(), 1800, 0)
            ));

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}
package net.lisalaf.fantastikmod.effect;

import net.lisalaf.fantastikmod.fantastikmod;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, fantastikmod.MOD_ID);

    public static final RegistryObject<MobEffect> INARI_BLESSING = MOB_EFFECTS.register("inari_blessing",
            InariBlessingEffect::new);
    public static final RegistryObject<MobEffect> CATNIP_EFFECT = MOB_EFFECTS.register("catnip_effect",
            () -> new CatnipEffect());
    public static final RegistryObject<MobEffect> LILAC_AROMA = MOB_EFFECTS.register("lilac_aroma",
            LilacAromaEffect::new);
    public static final RegistryObject<MobEffect> FIRE_DRAGON_SCALES = MOB_EFFECTS.register("fire_dragon_scales",
            FireDragonScalesEffect::new);

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
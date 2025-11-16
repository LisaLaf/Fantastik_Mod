package net.lisalaf.fantastikmod.sound;

import net.lisalaf.fantastikmod.fantastikmod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


@SuppressWarnings("removal")
public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, fantastikmod.MOD_ID);

    public static final RegistryObject<SoundEvent> GEMKITSUNE_BLOCK_BREAK = registerSoundEvents("gemkitsune_block_break");
    public static final RegistryObject<SoundEvent> GEMKITSUNE_BLOCK_STEP = registerSoundEvents("gemkitsune_block_step");
    public static final RegistryObject<SoundEvent> GEMKITSUNE_BLOCK_FALL = registerSoundEvents("gemkitsune_block_fall");
    public static final RegistryObject<SoundEvent> GEMKITSUNE_BLOCK_PLACE = registerSoundEvents("gemkitsune_block_place");
    public static final RegistryObject<SoundEvent> GEMKITSUNE_BLOCK_HIT = registerSoundEvents("gemkitsune_block_hit");

    public static final RegistryObject<SoundEvent> TEA_CEREMONY = registerSoundEvents("tea_ceremony");
   //public static final RegistryObject<SoundEvent> TEA_CEREMONY = registerSoundEvents("music_disc.tea_ceremony");

    public static final RegistryObject<SoundEvent> KITSUNE_IDLE1 = registerSoundEvents("kitsune_idle1");
    public static final RegistryObject<SoundEvent> KITSUNE_IDLE2 = registerSoundEvents("kitsune_idle2");
    public static final RegistryObject<SoundEvent> KITSUNE_IDLE3 = registerSoundEvents("kitsune_idle3");
    public static final RegistryObject<SoundEvent> KITSUNE_IDLE4 = registerSoundEvents("kitsune_idle4");

    // Смех (laugh)
    public static final RegistryObject<SoundEvent> KITSUNE_LAUGH1 = registerSoundEvents("kitsune_laugh1");
    public static final RegistryObject<SoundEvent> KITSUNE_LAUGH2 = registerSoundEvents("kitsune_laugh2");
    public static final RegistryObject<SoundEvent> KITSUNE_LAUGH3 = registerSoundEvents("kitsune_laugh3");

    // Злость (angry)
    public static final RegistryObject<SoundEvent> KITSUNE_ANGRY1 = registerSoundEvents("kitsune_angry1");

    // Боль (hurt)
    public static final RegistryObject<SoundEvent> KITSUNE_HURT1 = registerSoundEvents("kitsune_hurt1");
    public static final RegistryObject<SoundEvent> KITSUNE_HURT2 = registerSoundEvents("kitsune_hurt2");

    public static final RegistryObject<SoundEvent> DRAGON_IDLE1 = registerSoundEvents("dragon_idle1");
    public static final RegistryObject<SoundEvent> DRAGON_IDLE2 = registerSoundEvents("dragon_idle2");
    public static final RegistryObject<SoundEvent> DRAGON_IDLE3 = registerSoundEvents("dragon_idle3");
    public static final RegistryObject<SoundEvent> DRAGON_AGGRESSIVE_ROAR = registerSoundEvents("aggressive_dragon_roar");

    public static final ForgeSoundType GEMKITSUNE_BLOCK_SOUNDS = new ForgeSoundType(1f, 1f,
            ModSounds.GEMKITSUNE_BLOCK_BREAK, ModSounds.GEMKITSUNE_BLOCK_FALL, ModSounds.GEMKITSUNE_BLOCK_HIT,
            ModSounds.GEMKITSUNE_BLOCK_STEP, ModSounds.GEMKITSUNE_BLOCK_PLACE);


    private static RegistryObject<SoundEvent> registerSoundEvents(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(fantastikmod.MOD_ID, name)));
    }

    public static void  register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}

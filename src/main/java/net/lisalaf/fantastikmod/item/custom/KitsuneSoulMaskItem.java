package net.lisalaf.fantastikmod.item.custom;

import net.lisalaf.fantastikmod.fantastikmod;
import net.lisalaf.fantastikmod.item.client.KitsuneSoulMaskRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("removal")
public class KitsuneSoulMaskItem extends ArmorItem implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final int colorVariant;

    public KitsuneSoulMaskItem(ArmorMaterial material, Properties properties, int colorVariant) {
        super(material, Type.HELMET, properties);
        this.colorVariant = colorVariant;
    }

    public int getColorVariant() { return colorVariant; }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}


    @Override
    public void initializeClient(Consumer<net.minecraftforge.client.extensions.common.IClientItemExtensions> consumer) {
        consumer.accept(new net.minecraftforge.client.extensions.common.IClientItemExtensions() {
            private KitsuneSoulMaskRenderer renderer;
            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack,
                                                          EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (renderer == null) renderer = new KitsuneSoulMaskRenderer();
                renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return renderer;
            }
        });
    }

    @Override
    public boolean isFoil(ItemStack stack) { return true; }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (!(player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof KitsuneSoulMaskItem)) return;

        if (!player.hasEffect(MobEffects.NIGHT_VISION))
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, false, false, true));
        if (!player.hasEffect(MobEffects.MOVEMENT_SPEED))
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 0, false, false, true));
        if (!player.hasEffect(MobEffects.DAMAGE_BOOST))
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 400, 0, false, false, true));
        if (!player.hasEffect(MobEffects.JUMP))
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, 400, 0, false, false, true));

        int charge = stack.getOrCreateTag().getInt("soulCharge");
        if (charge < 100 && player.getFoodData().getFoodLevel() > 0) {
            charge++;
            stack.getTag().putInt("soulCharge", charge);
            if (charge % 20 == 0) {
                player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel() - 1);
            }
            if (level instanceof ServerLevel sl && charge % 5 == 0) {
                sl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        player.getX(), player.getY() + 1.5, player.getZ(),
                        1, 0.3, 0.2, 0.3, 0.02);
            }
        }

        if (player.isShiftKeyDown() && charge >= 20) {
            doAttack(player, level, stack);
        }
    }


    private void doAttack(Player player, Level level, ItemStack stack) {
        int charge = stack.getOrCreateTag().getInt("soulCharge");
        if (charge < 20) return;

        float damage = charge / 20.0f;
        float radius = 3.0f + damage;

        AABB area = player.getBoundingBox().inflate(radius);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area,
                e -> e != player && e.isAlive());

        for (LivingEntity target : targets) {
            target.hurt(player.damageSources().magic(), damage);
            target.setSecondsOnFire((int)(damage * 2));
        }

        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 50; i++) {
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        player.getX() + (level.random.nextDouble() - 0.5) * radius * 2,
                        player.getY() + 1.5 + level.random.nextDouble() * 2,
                        player.getZ() + (level.random.nextDouble() - 0.5) * radius * 2,
                        1, 0, 0, 0, 0.1);
            }
        }

        stack.getOrCreateTag().putInt("soulCharge", 0);
    }
}
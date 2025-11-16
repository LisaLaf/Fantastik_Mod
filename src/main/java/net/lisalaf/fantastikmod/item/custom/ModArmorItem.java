package net.lisalaf.fantastikmod.item.custom;

import com.google.common.collect.ImmutableMap;
import com.ibm.icu.impl.Relation;
import net.lisalaf.fantastikmod.item.ModArmorMaterials;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("removal")
public class ModArmorItem extends ArmorItem {
    private static final Map<ArmorMaterial, List<MobEffectInstance>> MATERIAL_TO_EFFECTS_MAP = Map.of(
            ModArmorMaterials.AURIPIGMENT, List.of(
                    new MobEffectInstance(MobEffects.HEALTH_BOOST, -1, 0, false, false, true)
            ),
            ModArmorMaterials.FUR_ICE_DRAGON, List.of(
                    new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 0, false, false, true),
                    new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 0, false, false, true)
            )


    );


    public ModArmorItem(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);


    }

    public void onArmorTick(ItemStack stack, Level world, Player player) {
        // Явная проверка через instanceof
        if(world instanceof ClientLevel) {
            return;
        }

        // ИЗМЕНИТЬ: теперь работаем со списком эффектов
        for (Map.Entry<ArmorMaterial, List<MobEffectInstance>> entry : MATERIAL_TO_EFFECTS_MAP.entrySet()) {
            ArmorMaterial material = entry.getKey();
            List<MobEffectInstance> effects = entry.getValue(); // ← теперь List эффектов

            if(hasFullSuitOfArmorOn(player) && hasCorrectArmorOn(material, player)) {
                // Добавляем ВСЕ эффекты из списка
                for(MobEffectInstance effect : effects) {
                    addStatusEffectForMaterial(player, material, effect);
                }

                // ДОБАВЛЕНО: эффект ледяной походки только для FUR_ICE_DRAGON
                if(material == ModArmorMaterials.FUR_ICE_DRAGON) {
                    applyIceWalkEffect(player, world);
                }
            } else {
                // Удаляем ВСЕ эффекты из списка
                for(MobEffectInstance effect : effects) {
                    removeStatusEffectForMaterial(player, effect);
                }
            }
        }

        int silverPieces = 0;
        for (ItemStack armor : player.getArmorSlots()) {
            if (armor.getItem() instanceof ModArmorItem &&
                    ((ModArmorItem) armor.getItem()).getMaterial() == ModArmorMaterials.SILVER) {
                silverPieces++;
            }
        }

        if (silverPieces > 0) {
            int radius = silverPieces; // 1 блок за каждый элемент

            List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class,
                    player.getBoundingBox().inflate(radius));

            for (LivingEntity entity : entities) {
                if (entity instanceof Monster || isUndead(entity)) {
                    // Заставляем моба избегать игрока
                    if (entity instanceof PathfinderMob pathfinderMob) {
                        // Устанавливаем точку избегания
                        pathfinderMob.getNavigation().stop();

                        // Ищем безопасную позицию подальше от игрока
                        Vec3 awayFromPlayer = entity.position()
                                .subtract(player.position())
                                .normalize()
                                .scale(radius + 1); // +3 блока за радиусом

                        BlockPos fleePos = BlockPos.containing(entity.position().add(awayFromPlayer));
                        pathfinderMob.getNavigation().moveTo(fleePos.getX(), fleePos.getY(), fleePos.getZ(), 1.2);
                    }

                    // Урон при слишком близком подходе (если прорвутся)
                    if (entity.distanceTo(player) < 1.0) {
                        entity.hurt(player.damageSources().magic(), silverPieces * 0.5F);
                    }
                }
            }
        }
    }

    private boolean isUndead(LivingEntity entity) {
        return entity instanceof Zombie || entity instanceof Skeleton ||
                entity instanceof Phantom || entity instanceof WitherSkeleton ||
                entity instanceof ZombifiedPiglin || entity instanceof Drowned;
    }



    private void removeStatusEffectForMaterial(Player player, MobEffectInstance mapStatusEffect) {
        if(player.hasEffect(mapStatusEffect.getEffect())) {
            player.removeEffect(mapStatusEffect.getEffect());
        }
    }

    private void evaluateArmorEffects(Player player) {
        for (Map.Entry<ArmorMaterial, List<MobEffectInstance>> entry : MATERIAL_TO_EFFECTS_MAP.entrySet()) {
            ArmorMaterial mapArmorMaterial = entry.getKey();
            List<MobEffectInstance> mapStatusEffects = entry.getValue();

            if(hasCorrectArmorOn(mapArmorMaterial, player)) {
                for(MobEffectInstance effect : mapStatusEffects) {
                    addStatusEffectForMaterial(player, mapArmorMaterial, effect);
                }
            }
        }
    }
    private void addStatusEffectForMaterial(Player player, ArmorMaterial mapArmorMaterial,
                                            MobEffectInstance mapStatusEffect) {
        boolean hasPlayerEffect = player.hasEffect(mapStatusEffect.getEffect());

        if(hasCorrectArmorOn(mapArmorMaterial, player) && !hasPlayerEffect) {
            player.addEffect(new MobEffectInstance(mapStatusEffect));
        }
    }

    private boolean hasFullSuitOfArmorOn(Player player) {
        ItemStack boots = player.getInventory().getArmor(0);
        ItemStack leggings = player.getInventory().getArmor(1);
        ItemStack breastplate = player.getInventory().getArmor(2);
        ItemStack helmet = player.getInventory().getArmor(3);

        return !helmet.isEmpty() && !breastplate.isEmpty()
                && !leggings.isEmpty() && !boots.isEmpty();
    }

    private boolean hasCorrectArmorOn(ArmorMaterial material, Player player) {
        for (ItemStack armorStack : player.getInventory().armor) {
            if(!(armorStack.getItem() instanceof ArmorItem)) {
                return false;
            }
        }

        ArmorItem boots = ((ArmorItem)player.getInventory().getArmor(0).getItem());
        ArmorItem leggings = ((ArmorItem)player.getInventory().getArmor(1).getItem());
        ArmorItem breastplate = ((ArmorItem)player.getInventory().getArmor(2).getItem());
        ArmorItem helmet = ((ArmorItem)player.getInventory().getArmor(3).getItem());

        return helmet.getMaterial() == material && breastplate.getMaterial() == material &&
                leggings.getMaterial() == material && boots.getMaterial() == material;
    }

    // ДОБАВЛЕНО: метод для эффекта ледяной походки
    private void applyIceWalkEffect(Player player, Level level) {
        if (!player.onGround()) return;

        BlockPos pos = player.blockPosition().below();
        int radius = 1; // Уменьшил радиус для оптимизации

        for (BlockPos targetPos : BlockPos.betweenClosed(pos.offset(-radius, 0, -radius),
                pos.offset(radius, 0, radius))) {

            // Пропускаем блоки слишком далеко от игрока
            if (targetPos.distSqr(player.blockPosition()) > radius * radius) continue;

            // Только замораживаем воду на поверхности
            if (level.getBlockState(targetPos).getBlock() == Blocks.WATER &&
                    level.isEmptyBlock(targetPos.above())) {
                level.setBlockAndUpdate(targetPos, Blocks.FROSTED_ICE.defaultBlockState());
            }
        }
    }


}

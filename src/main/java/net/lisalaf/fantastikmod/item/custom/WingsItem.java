package net.lisalaf.fantastikmod.item.custom;

import net.lisalaf.fantastikmod.fantastikmod;
import net.lisalaf.fantastikmod.item.client.WingsRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Random;
import java.util.function.Consumer;

@SuppressWarnings("removal")
public class WingsItem extends ArmorItem implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Random random = new Random();
    private int idleTimer = 0;
    private boolean useAltIdle = false;
    private boolean isFlying = false;
    private boolean isSwimming = false;

    public WingsItem(ArmorMaterial material, Properties properties) {
        super(material, Type.CHESTPLATE, properties);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return fantastikmod.MOD_ID + ":textures/models/armor/wings.png";
    }

    public boolean canWalkOnPowderedSnow(ItemStack stack) {
        return false;
    }

    @Override
    public void initializeClient(Consumer<net.minecraftforge.client.extensions.common.IClientItemExtensions> consumer) {
        consumer.accept(new net.minecraftforge.client.extensions.common.IClientItemExtensions() {
            private WingsRenderer renderer;

            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack,
                                                          EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (renderer == null) {
                    renderer = new WingsRenderer();
                }
                renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<WingsItem> state) {
        if (isFlying) {
            return state.setAndContinue(RawAnimation.begin().thenLoop("fly"));
        }
        if (isSwimming) {
            return state.setAndContinue(RawAnimation.begin().thenLoop("swim"));
        }

        if (idleTimer <= 0) {
            idleTimer = 100 + random.nextInt(200);
            useAltIdle = random.nextFloat() < 0.3f;
        }
        idleTimer--;

        if (useAltIdle) {
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle2"));
        }
        return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
    }


    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        boolean isWearingWings = player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof WingsItem;

        if (level.isClientSide) {
            if (isWearingWings && !player.onGround() && !player.isInWater()) {
                isFlying = true;
                Vec3 motion = player.getDeltaMovement();
                double verticalSpeed = motion.y;

                int cycle = player.tickCount % 50;
                if (cycle < 20) {
                    verticalSpeed = -0.05;
                } else {
                    verticalSpeed = 0.033;
                }

                if (Minecraft.getInstance().options.keyJump.isDown()) {
                    verticalSpeed = 0.3;
                } else if (player.isShiftKeyDown()) {
                    verticalSpeed = -0.3;
                }

                player.setDeltaMovement(motion.x, verticalSpeed, motion.z);
                player.fallDistance = 0f;
            }

            else if (isWearingWings && player.isInWater()) {
                isFlying = false;
                isSwimming = true;
            }

            else {
                isFlying = false;
                isSwimming = false;
            }
        }
    }
}
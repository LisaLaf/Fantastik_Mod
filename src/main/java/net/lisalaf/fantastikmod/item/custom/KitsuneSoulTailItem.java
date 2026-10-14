package net.lisalaf.fantastikmod.item.custom;

import net.lisalaf.fantastikmod.item.client.KitsuneSoulTailRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
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
public class KitsuneSoulTailItem extends ArmorItem implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final int colorVariant;
    private final Random random = new Random();

    private static String currentAnim = "idle";

    public KitsuneSoulTailItem(ArmorMaterial material, Properties properties, int colorVariant) {
        super(material, Type.CHESTPLATE, properties);
        this.colorVariant = colorVariant;
    }

    public int getColorVariant() { return colorVariant; }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "fantastikmod:textures/models/armor/kitsune_soul_tail.png";
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private KitsuneSoulTailRenderer renderer;
            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack,
                                                          EquipmentSlot slot, HumanoidModel<?> original) {
                if (renderer == null) renderer = new KitsuneSoulTailRenderer();
                renderer.prepForRender(entity, stack, slot, original);
                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 3, this::predicate));
    }

    private PlayState predicate(AnimationState<KitsuneSoulTailItem> state) {
        return state.setAndContinue(RawAnimation.begin().thenLoop(currentAnim));
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (!level.isClientSide) return;
        if (!(player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof KitsuneSoulTailItem)) return;

        if (player.isSwimming() || player.isInWater()) {
            currentAnim = "swim";
        } else if (player.onGround() && player.getDeltaMovement().horizontalDistanceSqr() > 0.0015) {
            currentAnim = player.isSprinting() ? "run" : "walk";
        } else {
            currentAnim = "idle";
        }

        if (random.nextFloat() < 0.3f) {
            double angle = random.nextDouble() * Math.PI * 2;
            double radius = 0.15 + random.nextDouble() * 0.10;
            double x = player.getX() + + Math.sin(angle) * radius;
            double y = player.getY() + 0.3 + random.nextDouble() * 0.6;
            double z = player.getZ() + Math.sin(angle) * radius;

            level.addParticle(ParticleTypes.WHITE_ASH,
                    x, y, z,
                    (random.nextDouble() - 0.4) * 0.02,
                    0.01 + random.nextDouble() * 0.02,
                    (random.nextDouble() - 0.4) * 0.02);
        }

        if (random.nextFloat() < 0.05f) {
            level.addParticle(ParticleTypes.CRIT,
                    player.getX(), player.getY() + 0.5, player.getZ(),
                    0, 0.02, 0);
        }
    }
}
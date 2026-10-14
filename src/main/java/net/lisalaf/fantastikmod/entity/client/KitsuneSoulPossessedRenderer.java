package net.lisalaf.fantastikmod.entity.client;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.blaze3d.vertex.PoseStack;
import net.lisalaf.fantastikmod.entity.custom.KitsuneSoulPossessedEntity;
import net.lisalaf.fantastikmod.item.client.KitsuneSoulMaskRenderer;
import net.lisalaf.fantastikmod.item.client.KitsuneSoulTailRenderer;
import net.lisalaf.fantastikmod.item.custom.KitsuneSoulMaskItem;
import net.lisalaf.fantastikmod.item.custom.KitsuneSoulTailItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class KitsuneSoulPossessedRenderer
        extends HumanoidMobRenderer<KitsuneSoulPossessedEntity, PlayerModel<KitsuneSoulPossessedEntity>> {

    private ResourceLocation cachedSkin;
    private final KitsuneSoulMaskRenderer maskRenderer = new KitsuneSoulMaskRenderer();
    private final KitsuneSoulTailRenderer tailRenderer = new KitsuneSoulTailRenderer();

    public KitsuneSoulPossessedRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new PlayerModel<>(ctx.bakeLayer(ModelLayers.PLAYER), false), 0.5f);
        this.addLayer(new HumanoidArmorLayer<>(
                this,
                new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
                ctx.getModelManager()
        ));

        this.addLayer(new ItemInHandLayer<>(this, ctx.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(KitsuneSoulPossessedEntity entity) {
        if (cachedSkin != null) return cachedSkin;

        String textureValue = entity.getOwnerTexture();
        String textureSignature = entity.getOwnerTextureSlim();

        if (textureValue == null || textureValue.isEmpty()) {
            cachedSkin = DefaultPlayerSkin.getDefaultSkin(entity.getUUID());
            return cachedSkin;
        }

        GameProfile profile = new GameProfile(entity.getUUID(), entity.getOwnerName());
        if (textureSignature != null && !textureSignature.isEmpty()) {
            profile.getProperties().put("textures",
                    new Property("textures", textureValue, textureSignature));
        } else {
            profile.getProperties().put("textures",
                    new Property("textures", textureValue));
        }

        try {
            cachedSkin = Minecraft.getInstance().getSkinManager().getInsecureSkinLocation(profile);
        } catch (Throwable t) {
            cachedSkin = DefaultPlayerSkin.getDefaultSkin(entity.getUUID());
        }

        return cachedSkin;
    }

    @Override
    public void render(KitsuneSoulPossessedEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (entity.tickCount < 2) cachedSkin = null;

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        renderGeckoArmor(entity, poseStack, bufferSource, packedLight);
    }

    private void renderGeckoArmor(KitsuneSoulPossessedEntity entity, PoseStack poseStack,
                                  MultiBufferSource bufferSource, int packedLight) {

        ItemStack headStack = entity.getItemBySlot(EquipmentSlot.HEAD);
        if (headStack.getItem() instanceof KitsuneSoulMaskItem) {
            maskRenderer.prepForRender(entity, headStack, EquipmentSlot.HEAD, getModel());
        }


        ItemStack chestStack = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (chestStack.getItem() instanceof KitsuneSoulTailItem) {
            tailRenderer.prepForRender(entity, chestStack, EquipmentSlot.CHEST, getModel());
        }
    }
}
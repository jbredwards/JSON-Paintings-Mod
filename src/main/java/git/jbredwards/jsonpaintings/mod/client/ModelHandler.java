/*
 * Copyright (c) 2026. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.client;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import git.jbredwards.jsonpaintings.api.ActivePaintingInfo;
import git.jbredwards.jsonpaintings.api.PaintingHelper;
import git.jbredwards.jsonpaintings.mod.JSONPaintings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.Collections;

/**
 *
 * @author jbred
 *
 */
@Mod.EventBusSubscriber(modid = JSONPaintings.MODID, value = Side.CLIENT)
final class ModelHandler
{
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    static void init(@Nonnull final ModelRegistryEvent event) {
        ((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener((ISelectiveResourceReloadListener)(manager, predicate) -> {
            if(predicate.test(VanillaResourceType.MODELS)) PaintingModel.MODELS.invalidateAll();
        });
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    static void onModelBake(@Nonnull final ModelBakeEvent event) {
        @Nonnull final ModelResourceLocation location = ModelLoader.getInventoryVariant("painting");
        event.getModelRegistry().putObject(location, new PaintingModel(event.getModelManager().getModel(location)));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    static void onTextureStitch(@Nonnull final TextureStitchEvent.Pre event) {
        if(event.getMap() == Minecraft.getMinecraft().getTextureMapBlocks()) {
            for(@Nonnull final EntityPainting.EnumArt art : EntityPainting.EnumArt.values()) {
                @Nonnull final ActivePaintingInfo info = ActivePaintingInfo.get(art);
                if(info.itemModel != null) ModelLoaderRegistry
                        .getModelOrLogError(info.itemModel, "Could not load painting model for " + art.title)
                        .getTextures().forEach(event.getMap()::registerSprite);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    static final class PaintingModel extends BakedModelWrapper<IBakedModel>
    {
        @Nonnull
        public static final LoadingCache<ModelResourceLocation, IBakedModel> MODELS = CacheBuilder.newBuilder().build(CacheLoader.from(location -> {
            @Nonnull final IModel model = ModelLoaderRegistry.getModelOrLogError(location, "Could not find model for " + location);
            return model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());
        }));

        public PaintingModel(@Nonnull final IBakedModel originalModel) {
            super(originalModel);
        }

        @Nonnull
        @Override
        public Pair<? extends IBakedModel, Matrix4f> handlePerspective(@Nonnull final ItemCameraTransforms.TransformType cameraTransformType) {
            return Pair.of(this, super.handlePerspective(cameraTransformType).getRight());
        }

        @Nonnull
        @Override
        public ItemOverrideList getOverrides() {
            return new ItemOverrideList(Collections.emptyList()) {
                @Nonnull
                @Override
                public IBakedModel handleItemState(@Nonnull final IBakedModel originalModel, @Nonnull final ItemStack stack, @Nullable final World world, @Nullable final EntityLivingBase entity) {
                    @Nullable final EntityPainting.EnumArt art = PaintingHelper.read(stack);
                    if(art == null) return originalModel;

                    @Nullable final ModelResourceLocation model = ActivePaintingInfo.get(art).itemModel;
                    return model != null ? MODELS.getUnchecked(model) : originalModel;
                }
            };
        }
    }
}

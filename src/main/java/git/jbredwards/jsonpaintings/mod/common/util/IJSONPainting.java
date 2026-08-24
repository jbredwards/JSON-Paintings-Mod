/*
 * Copyright (c) 2025. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.common.util;

import git.jbredwards.jsonpaintings.api.ActivePaintingInfo;
import git.jbredwards.jsonpaintings.api.PaintingHelper;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.IRarity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Exists for legacy compat only. Use {@link ActivePaintingInfo} instead.
 * @see git.jbredwards.jsonpaintings.mod.asm.AccessorEnumArt
 * @author jbred
 *
 */
@Deprecated
public interface IJSONPainting
{
    @Nonnull
    @Deprecated default ResourceLocation getFrontTexture() { return ActivePaintingInfo.get((EntityPainting.EnumArt)(Object)this).frontTexture; }
    @Deprecated default void setFrontTexture(@Nonnull final ResourceLocation texture) { ActivePaintingInfo.get((EntityPainting.EnumArt)(Object)this).frontTexture = texture; }

    @Nonnull
    @Deprecated default ResourceLocation getBackTexture() { return PaintingHelper.getBackTexture(ActivePaintingInfo.get((EntityPainting.EnumArt)(Object)this)); }
    @Deprecated default void setBackTexture(@Nonnull final ResourceLocation texture) { ActivePaintingInfo.get((EntityPainting.EnumArt)(Object)this).backTexture = texture; }

    @Nonnull
    @Deprecated default ResourceLocation getSideTexture() { return PaintingHelper.getSideTexture(ActivePaintingInfo.get((EntityPainting.EnumArt)(Object)this)); }
    @Deprecated default void setSideTexture(@Nonnull final ResourceLocation texture) { ActivePaintingInfo.get((EntityPainting.EnumArt)(Object)this).sideTexture = texture; }

    @Nullable
    @Deprecated default String getModName() { return ActivePaintingInfo.get((EntityPainting.EnumArt)(Object)this).modName; }
    @Deprecated default void setModName(@Nullable final String modName) { ActivePaintingInfo.get((EntityPainting.EnumArt)(Object)this).modName = modName; }

    @Nullable
    @Deprecated default IRarity getRarity() { return ActivePaintingInfo.get((EntityPainting.EnumArt)(Object)this).rarity; }
    @Deprecated default void setRarity(@Nullable final IRarity rarity) { ActivePaintingInfo.get((EntityPainting.EnumArt)(Object)this).rarity = rarity; }

    @Deprecated default boolean isCreative() { return ActivePaintingInfo.get((EntityPainting.EnumArt)(Object)this).isTreasure; }
    @Deprecated default void setCreative(final boolean isCreative) { ActivePaintingInfo.get((EntityPainting.EnumArt)(Object)this).isTreasure = isCreative; }

    @Deprecated default boolean alwaysCapture() { return ActivePaintingInfo.get((EntityPainting.EnumArt)(Object)this).alwaysCapture; }
    @Deprecated default void setAlwaysCapture(final boolean alwaysCapture) { ActivePaintingInfo.get((EntityPainting.EnumArt)(Object)this).alwaysCapture = alwaysCapture; }

    @Deprecated default boolean hasBackTexture() { return ActivePaintingInfo.get((EntityPainting.EnumArt)(Object)this).backTexture != null; }
    @Deprecated default void setHasBackTexture(final boolean hasTexture) { /* NO-OP */ }

    @Deprecated default boolean hasSideTexture() { return ActivePaintingInfo.get((EntityPainting.EnumArt)(Object)this).sideTexture != null || hasBackTexture(); }
    @Deprecated default void setHasSideTexture(final boolean hasTexture) { /* NO-OP */ }

    @Deprecated default boolean useSpecialRenderer() { return ActivePaintingInfo.get((EntityPainting.EnumArt)(Object)this).frontTexture != null; }
    @Deprecated default void setUseSpecialRenderer(final boolean useSpecialRenderer) { /* NO-OP */ }

    @Nonnull
    @Deprecated static IJSONPainting from(@Nonnull final EntityPainting.EnumArt art) { return (IJSONPainting)(Object)art; }

    @Nonnull
    @Deprecated default String getModNameOrDefault() { return PaintingHelper.getModName(ActivePaintingInfo.get((EntityPainting.EnumArt)(Object)this)); }
}

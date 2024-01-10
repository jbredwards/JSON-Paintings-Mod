/*
 * Copyright (c) 2024. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.common.util;

import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.IRarity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * {@link net.minecraft.entity.item.EntityPainting.EnumArt EnumArt} implements that at runtime
 * @author jbred
 *
 */
public interface IJSONPainting
{
    // nonnull if this uses special rendering
    @Nonnull ResourceLocation getFrontTexture();
    void setFrontTexture(@Nonnull final ResourceLocation texture);

    // nonnull if this uses special rendering
    @Nonnull ResourceLocation getBackTexture();
    void setBackTexture(@Nonnull final ResourceLocation texture);

    // nonnull if this uses special rendering
    @Nonnull ResourceLocation getSideTexture();
    void setSideTexture(@Nonnull final ResourceLocation texture);

    // the name of the mod that adds this, used by Waila & TOP
    @Nullable String getModName();
    void setModName(@Nullable final String modName);

    // the rarity of the painting (cosmetic only)
    @Nullable IRarity getRarity();
    void setRarity(@Nullable final IRarity rarity);

    // if this should not be obtainable via painting cycling
    boolean isCreative();
    void setCreative(final boolean isCreative);

    // if this should always be captured when broken
    boolean alwaysCapture();
    void setAlwaysCapture(final boolean alwaysCapture);

    // if the back texture is specified, assume it has proper proportions
    boolean hasBackTexture();
    void setHasBackTexture(final boolean hasTexture);

    // if the side texture is specified, assume it has proper proportions
    boolean hasSideTexture();
    void setHasSideTexture(final boolean hasTexture);

    // only paintings added through this mod have this value set to true
    boolean useSpecialRenderer();
    void setUseSpecialRenderer(final boolean useSpecialRenderer);

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    static IJSONPainting from(@Nonnull final EntityPainting.EnumArt art) { return (IJSONPainting)(Object)art; }

    @Nonnull
    default String getModNameOrDefault() { return getModName() == null ? "Minecraft Forge" : getModName(); }
}

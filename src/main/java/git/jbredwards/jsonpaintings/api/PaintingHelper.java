/*
 * Copyright (c) 2026. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.api;

import com.google.common.base.Preconditions;
import git.jbredwards.jsonpaintings.mod.asm.transformer.EnumArtTransformer;
import git.jbredwards.jsonpaintings.mod.common.capability.IArtCapability;
import git.jbredwards.jsonpaintings.mod.common.util.JSONHandler;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.IRarity;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * Helpful utilities relating to {@code EnumArt} and {@code PaintingInfo}.
 * @author jbred
 *
 */
@ApiStatus.AvailableSince("1.5.0")
public final class PaintingHelper
{
    /**
     * An unmodifiable map containing all {@code EnumArt} instances, where the keys are their motives.
     * <br> This map is updated automatically whenever a new {@code EnumArt} is initialized.
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Nonnull
    public static final Map<String, EntityPainting.EnumArt> PAINTINGS = EnumArtTransformer.Hooks.paintings();

    /**
     * @return An {@code EnumArt} mapped to the provided motive, or null the motive has none mapped.
     * @throws NullPointerException If motive is null.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Nullable
    public static EntityPainting.EnumArt get(@Nonnull final String motive) {
        @Nullable final EntityPainting.EnumArt art = PaintingHelper.PAINTINGS.get(motive);
        return art != null ? art : JSONHandler.PAINTING_REMAPS.get(motive);
    }

    /**
     * Registers fallback motive names to the provided {@code EnumArt}.
     * <br>Useful if you remove a painting, and need to remap its old motive.
     * @throws NullPointerException If art or motives are null.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    public static void remap(@Nonnull final EntityPainting.EnumArt art, @Nonnull final String... motives) {
        Preconditions.checkNotNull(art);
        for(@Nonnull final String motive : motives) JSONHandler.PAINTING_REMAPS.put(motive, art);
    }

    /**
     * @return The {@code EnumArt} stored in the item, or null if none.
     * @throws NullPointerException If from is null.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Nullable
    public static EntityPainting.EnumArt read(@Nonnull final ItemStack from) {
        @Nullable final IArtCapability cap = IArtCapability.get(Preconditions.checkNotNull(from));
        return cap != null ? cap.getArt() : null;
    }

    /**
     * Writes the provided {@code EnumArt} to the item.
     * @throws NullPointerException If to is null.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Nonnull
    public static ItemStack write(@Nonnull final ItemStack to, @Nullable final EntityPainting.EnumArt art) {
        @Nullable final IArtCapability cap = IArtCapability.get(Preconditions.checkNotNull(to));
        if(cap != null) cap.setArt(art);
        return to;
    }

    /**
     * @return The actual {@link PaintingInfo#backTexture} used by JSON Paintings.
     * @throws NullPointerException If info is null.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Nonnull
    public static ResourceLocation getBackTexture(@Nonnull final PaintingInfo info) {
        return info.backTexture != null ? info.backTexture : JSONHandler.DEFAULT_BACK_TEXTURE;
    }

    /**
     * @return The actual {@link PaintingInfo#sideTexture} used by JSON Paintings.
     * @throws NullPointerException If info is null.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Nonnull
    public static ResourceLocation getSideTexture(@Nonnull final PaintingInfo info) {
        return info.sideTexture != null ? info.sideTexture : PaintingHelper.getBackTexture(info);
    }

    /**
     * @return The actual {@link PaintingInfo#modId} used by JSON Paintings.
     * @throws NullPointerException If info is null.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Nonnull
    public static String getModId(@Nonnull final PaintingInfo info) {
        return info.modId != null ? info.modId : ForgeVersion.MOD_ID;
    }

    /**
     * @return The actual {@link PaintingInfo#modName} used by JSON Paintings.
     * @throws NullPointerException If info is null.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Nonnull
    public static String getModName(@Nonnull final PaintingInfo info) {
        return info.modName != null ? info.modName : "Minecraft Forge";
    }

    /**
     * @return The actual {@link PaintingInfo#rarity} used by JSON Paintings.
     * @throws NullPointerException If info is null.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Nonnull
    public static IRarity getRarity(@Nonnull final PaintingInfo info) {
        return info.rarity != null ? info.rarity : EnumRarity.COMMON;
    }

    /**
     * @return The actual {@link PaintingInfo#title} used by JSON Paintings.
     * @throws NullPointerException If info or motive are null.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Nonnull
    public static ITextComponent getTitle(@Nonnull final PaintingInfo info, @Nonnull final String motive) {
        Preconditions.checkNotNull(motive);
        return info.title != null ? info.title
                : new TextComponentTranslation(motive).setStyle(new Style().setColor(TextFormatting.YELLOW));
    }
}

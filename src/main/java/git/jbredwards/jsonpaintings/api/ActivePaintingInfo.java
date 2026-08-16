/*
 * Copyright (c) 2026. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.api;

import git.jbredwards.jsonpaintings.mod.asm.AccessorEnumArt;
import net.minecraft.entity.item.EntityPainting;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * A {@code PaintingInfo} that's tied to an {@code EnumArt} instance.
 * <br> May also be defined using a paintings.json file in your mod's resources.
 *
 * @author jbred
 *
 */
@ApiStatus.AvailableSince("1.5.0")
public final class ActivePaintingInfo extends PaintingInfo
{
    /**
     * A reference to the owning {@code EnumArt} instance.
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Nonnull
    public final EntityPainting.EnumArt painting;
    private ActivePaintingInfo(@Nonnull final EntityPainting.EnumArt painting) {
        this.painting = Objects.requireNonNull(painting);
    }

    /**
     * @return The {@code ActivePaintingInfo} tied to the provided {@code EnumArt} instance.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Nonnull
    public static ActivePaintingInfo get(@Nonnull final EntityPainting.EnumArt painting) {
        @Nonnull final AccessorEnumArt accessor = (AccessorEnumArt)Objects.<Object>requireNonNull(painting);
        @Nullable ActivePaintingInfo info = accessor.jsonpaintings$info();

        if(info != null) return info;
        accessor.jsonpaintings$info(info = new ActivePaintingInfo(painting));
        return info;
    }

    /**
     * @return Width of the painting (in pixels).
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Override
    public int getWidth() {
        return this.painting.sizeX;
    }

    /**
     * Setter for {@link ActivePaintingInfo#getWidth()}.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Override
    public void setWidth(final int width) {
        this.painting.sizeX = width;
    }

    /**
     * @return Height of the painting (in pixels).
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Override
    public int getHeight() {
        return this.painting.sizeY;
    }

    /**
     * Setter for {@link ActivePaintingInfo#getHeight()}.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Override
    public void setHeight(final int height) {
        this.painting.sizeY = height;
    }

    /**
     * Unused if {@link PaintingInfo#useSpecialRenderer} is true.
     * @return X offset on the Vanilla painting atlas texture.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Override
    public int getXOffset() {
        return this.painting.offsetX;
    }

    /**
     * Setter for {@link ActivePaintingInfo#getXOffset()}.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Override
    public void setXOffset(final int offset) {
        this.painting.offsetX = offset;
    }

    /**
     * Unused if {@link PaintingInfo#useSpecialRenderer} is true.
     * @return Y offset on the Vanilla painting atlas texture.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Override
    public int getYOffset() {
        return this.painting.offsetY;
    }

    /**
     * Setter for {@link ActivePaintingInfo#getYOffset()}.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Override
    public void setYOffset(int offset) {
        this.painting.offsetY = offset;
    }
}

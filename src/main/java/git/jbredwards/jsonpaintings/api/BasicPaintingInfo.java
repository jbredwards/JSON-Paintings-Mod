/*
 * Copyright (c) 2026. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.api;

import org.jetbrains.annotations.ApiStatus;

/**
 * A basic {@code PaintingInfo} implementation.
 *
 * @author jbred
 *
 */
@ApiStatus.AvailableSince("1.5.0")
public class BasicPaintingInfo extends PaintingInfo
{
    @ApiStatus.Internal
    private int width, height, xOffset, yOffset;

    /**
     * @return Width of the painting (in pixels).
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Override
    public int getWidth() {
        return this.width;
    }

    /**
     * Setter for {@link BasicPaintingInfo#getWidth()}.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Override
    public void setWidth(final int width) {
        this.width = width;
    }

    /**
     * @return Height of the painting (in pixels).
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Override
    public int getHeight() {
        return this.height;
    }

    /**
     * Setter for {@link BasicPaintingInfo#getHeight()}.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Override
    public void setHeight(final int height) {
        this.height = height;
    }

    /**
     * Unused if {@link PaintingInfo#useSpecialRenderer} is true.
     * @return X offset on the Vanilla painting atlas texture.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Override
    public int getXOffset() {
        return this.xOffset;
    }

    /**
     * Setter for {@link BasicPaintingInfo#getXOffset()}.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Override
    public void setXOffset(final int offset) {
        this.xOffset = offset;
    }

    /**
     * Unused if {@link PaintingInfo#useSpecialRenderer} is true.
     * @return Y offset on the Vanilla painting atlas texture.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Override
    public int getYOffset() {
        return this.yOffset;
    }

    /**
     * Setter for {@link BasicPaintingInfo#getYOffset()}.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Override
    public void setYOffset(final int offset) {
        this.yOffset = offset;
    }
}

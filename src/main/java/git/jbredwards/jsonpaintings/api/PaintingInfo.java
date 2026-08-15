/*
 * Copyright (c) 2026. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.api;

import git.jbredwards.jsonpaintings.mod.common.util.RarityUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Holds new info for paintings.
 *
 * @author jbred
 * 
 * @see ActivePaintingInfo
 * @see BasicPaintingInfo
 *
 */
@ApiStatus.AvailableSince("1.5.0")
public abstract class PaintingInfo
{
    /**
     * True if this uses the special JSON Paintings renderer.
     * <br>While true, additional fields may be set, such as textures.
     */
    @ApiStatus.AvailableSince("1.5.0")
    public boolean useSpecialRenderer;

    /**
     * The texture rendered on the front face of the painting.
     * <br>Must be non-null if this uses special rendering.
     */
    @ApiStatus.AvailableSince("1.5.0")
    public ResourceLocation frontTexture;

    /**
     * The texture rendered on the back face of the painting.
     * <br>Uses Vanilla's generic painting back texture if null.
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Nullable
    public ResourceLocation backTexture;

    /**
     * The texture rendered on the side faces of the painting.
     * <br>Uses {@link ActivePaintingInfo#backTexture} if null.
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Nullable
    public ResourceLocation sideTexture;

    /**
     * The name of the mod that adds this, used by Waila & TOP.
     * <br> Uses "Minecraft Forge" if null.
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Nullable
    public String modName;

    /**
     * The rarity of the painting (cosmetic only).
     * <br> Uses {@link net.minecraft.item.EnumRarity#COMMON} if null.
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Nullable
    public IRarity rarity;

    /**
     * True if this should always be captured when broken.
     */
    @ApiStatus.AvailableSince("1.5.0")
    public boolean alwaysCapture;

    /**
     * True if this should not be obtainable via painting cycling.
     */
    @ApiStatus.AvailableSince("1.5.0")
    public boolean isTreasure;

    /**
     * @return Width of the painting (in pixels).
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    public abstract int getWidth();

    /**
     * Setter for {@link PaintingInfo#getWidth()}.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    public abstract void setWidth(final int width);

    /**
     * @return Height of the painting (in pixels).
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    public abstract int getHeight();

    /**
     * Setter for {@link PaintingInfo#getHeight()}.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    public abstract void setHeight(final int height);

    /**
     * Unused if {@link PaintingInfo#useSpecialRenderer} is true.
     * @return X offset on the Vanilla painting atlas texture.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    public abstract int getXOffset();

    /**
     * Setter for {@link PaintingInfo#getXOffset()}.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    public abstract void setXOffset(final int offset);

    /**
     * Unused if {@link PaintingInfo#useSpecialRenderer} is true.
     * @return Y offset on the Vanilla painting atlas texture.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    public abstract int getYOffset();

    /**
     * Setter for {@link PaintingInfo#getYOffset()}.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    public abstract void setYOffset(final int offset);

    /**
     * Copies everything from {@code other}.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    public void copyFrom(@Nonnull final PaintingInfo other) {
        this.setWidth(other.getWidth());
        this.setHeight(other.getHeight());
        this.setXOffset(other.getXOffset());
        this.setYOffset(other.getYOffset());

        this.useSpecialRenderer = other.useSpecialRenderer;
        this.frontTexture = other.frontTexture;
        this.backTexture = other.backTexture;
        this.sideTexture = other.sideTexture;
        this.modName = other.modName;
        this.rarity = other.rarity;
        this.alwaysCapture = other.alwaysCapture;
        this.isTreasure = other.isTreasure;
    }

    /**
     * Reads {@code PaintingInfo} from the supplied buffer.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    public void decode(@Nonnull final ByteBuf from) {
        this.setWidth(ByteBufUtils.readVarInt(from, 2));
        this.setHeight(ByteBufUtils.readVarInt(from, 2));
        this.setXOffset(ByteBufUtils.readVarInt(from, 2));
        this.setYOffset(ByteBufUtils.readVarInt(from, 2));

        this.useSpecialRenderer = from.readBoolean();
        this.frontTexture = from.readBoolean() ? new ResourceLocation(ByteBufUtils.readUTF8String(from)) : null;
        this.backTexture = from.readBoolean() ? new ResourceLocation(ByteBufUtils.readUTF8String(from)) : null;
        this.sideTexture = from.readBoolean() ? new ResourceLocation(ByteBufUtils.readUTF8String(from)) : null;
        this.modName = from.readBoolean() ? ByteBufUtils.readUTF8String(from) : null;
        this.rarity = from.readBoolean() ? RarityUtils.decode(from) : null;
        this.alwaysCapture = from.readBoolean();
        this.isTreasure = from.readBoolean();
    }

    /**
     * Writes {@code PaintingInfo} to the supplied buffer.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    public void encode(@Nonnull final ByteBuf to) {
        ByteBufUtils.writeVarInt(to, this.getWidth(), 2);
        ByteBufUtils.writeVarInt(to, this.getHeight(), 2);
        ByteBufUtils.writeVarInt(to, this.getXOffset(), 2);
        ByteBufUtils.writeVarInt(to, this.getYOffset(), 2);

        to.writeBoolean(this.useSpecialRenderer);
        to.writeBoolean(this.frontTexture != null); if(this.frontTexture != null) ByteBufUtils.writeUTF8String(to, this.frontTexture.toString());
        to.writeBoolean(this.backTexture != null); if(this.backTexture != null) ByteBufUtils.writeUTF8String(to, this.backTexture.toString());
        to.writeBoolean(this.sideTexture != null); if(this.sideTexture != null) ByteBufUtils.writeUTF8String(to, this.sideTexture.toString());
        to.writeBoolean(this.modName != null); if(this.modName != null) ByteBufUtils.writeUTF8String(to, this.modName);
        to.writeBoolean(this.rarity != null); if(this.rarity != null) RarityUtils.encode(to, this.rarity);
        to.writeBoolean(this.alwaysCapture);
        to.writeBoolean(this.isTreasure);
    }
}

/*
 * Copyright (c) 2026. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.api;

import git.jbredwards.jsonpaintings.mod.common.util.RarityUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
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
     * The texture rendered on the front face of the painting.
     * <br>Uses Vanilla's painting texture atlas if null.
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nullable
    public ResourceLocation frontTexture;

    /**
     * The texture rendered on the back face of the painting.
     * <br>Uses Vanilla's generic painting back texture if null.
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nullable
    public ResourceLocation backTexture;

    /**
     * The texture rendered on the side faces of the painting.
     * <br>Uses {@link ActivePaintingInfo#backTexture} if null.
     */
    @ApiStatus.AvailableSince("1.0.0")
    @Nullable
    public ResourceLocation sideTexture;

    /**
     * The item model used by painting items with this type stored.
     * <br> Uses the default painting item model if null.
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Nullable
    public ModelResourceLocation itemModel;

    /**
     * The id of the mod that adds this, used by item tooltips.
     * <br> Automatically set to the active mod for {@code ActivePaintingInfo} when an {@code EnumArt} is created.
     * <br> Uses "forge" if null.
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Nullable
    public String modId;

    /**
     * The name of the mod that adds this, used by Waila & TOP.
     * <br> Automatically set to the active mod for {@code ActivePaintingInfo} when an {@code EnumArt} is created.
     * <br> Uses "Minecraft Forge" if null.
     */
    @ApiStatus.AvailableSince("1.3.0")
    @Nullable
    public String modName;

    /**
     * The author of this painting, displayed in the painting item tooltip.
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Nullable
    public ITextComponent author;

    /**
     * The formatted title of this painting.
     * <br> Uses {@link net.minecraft.entity.item.EntityPainting.EnumArt#title} if null.
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Nullable
    public ITextComponent title;

    /**
     * The rarity of the painting (cosmetic only).
     * <br> Uses {@link net.minecraft.item.EnumRarity#COMMON} if null.
     */
    @ApiStatus.AvailableSince("1.3.0")
    @Nullable
    public IRarity rarity;

    /**
     * True if this should always be captured when broken.
     */
    @ApiStatus.AvailableSince("1.3.0")
    public boolean alwaysCapture;

    /**
     * True if this should not be obtainable via painting cycling.
     */
    @ApiStatus.AvailableSince("1.1.0")
    public boolean isTreasure;

    /**
     * @return Width of the painting (in pixels).
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    public abstract int getWidth();

    /**
     * Setter for {@link PaintingInfo#getWidth()}.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    public abstract void setWidth(final int width);

    /**
     * @return Height of the painting (in pixels).
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    public abstract int getHeight();

    /**
     * Setter for {@link PaintingInfo#getHeight()}.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.0.0")
    public abstract void setHeight(final int height);

    /**
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
    public void copyFrom(@Nonnull final PaintingInfo other, final boolean applyUnset) {
        if(applyUnset || other.getWidth() > 0) this.setWidth(other.getWidth());
        if(applyUnset || other.getHeight() > 0) this.setHeight(other.getHeight());
        if(applyUnset || other.getXOffset() >= 0) this.setXOffset(other.getXOffset());
        if(applyUnset || other.getYOffset() >= 0) this.setYOffset(other.getYOffset());

        if(applyUnset || other.frontTexture != null) this.frontTexture = other.frontTexture;
        if(applyUnset || other.backTexture != null) this.backTexture = other.backTexture;
        if(applyUnset || other.sideTexture != null) this.sideTexture = other.sideTexture;
        if(applyUnset || other.itemModel != null) this.itemModel = other.itemModel;
        if(applyUnset || other.modId != null) this.modId = other.modId;
        if(applyUnset || other.modName != null) this.modName = other.modName;
        if(applyUnset || other.rarity != null) this.rarity = other.rarity;
        this.alwaysCapture = other.alwaysCapture;
        this.isTreasure = other.isTreasure;

        if(applyUnset || other.author != null) this.author = other.author != null ? other.author.createCopy() : null;
        if(applyUnset || other.title != null) this.title = other.title != null ? other.title.createCopy() : null;
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

        this.frontTexture = from.readBoolean() ? new ResourceLocation(ByteBufUtils.readUTF8String(from)) : null;
        this.backTexture = from.readBoolean() ? new ResourceLocation(ByteBufUtils.readUTF8String(from)) : null;
        this.sideTexture = from.readBoolean() ? new ResourceLocation(ByteBufUtils.readUTF8String(from)) : null;
        this.itemModel = from.readBoolean() ? new ModelResourceLocation(ByteBufUtils.readUTF8String(from)) : null;
        this.modId = from.readBoolean() ? ByteBufUtils.readUTF8String(from) : null;
        this.modName = from.readBoolean() ? ByteBufUtils.readUTF8String(from) : null;
        this.rarity = from.readBoolean() ? RarityUtils.decode(from) : null;
        this.alwaysCapture = from.readBoolean();
        this.isTreasure = from.readBoolean();

        this.author = from.readBoolean() ? ITextComponent.Serializer.jsonToComponent(ByteBufUtils.readUTF8String(from)) : null;
        this.title = from.readBoolean() ? ITextComponent.Serializer.jsonToComponent(ByteBufUtils.readUTF8String(from)) : null;
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

        to.writeBoolean(this.frontTexture != null); if(this.frontTexture != null) ByteBufUtils.writeUTF8String(to, this.frontTexture.toString());
        to.writeBoolean(this.backTexture != null); if(this.backTexture != null) ByteBufUtils.writeUTF8String(to, this.backTexture.toString());
        to.writeBoolean(this.sideTexture != null); if(this.sideTexture != null) ByteBufUtils.writeUTF8String(to, this.sideTexture.toString());
        to.writeBoolean(this.itemModel != null); if(this.itemModel != null) ByteBufUtils.writeUTF8String(to, this.itemModel.toString());
        to.writeBoolean(this.modId != null); if(this.modId != null) ByteBufUtils.writeUTF8String(to, this.modId);
        to.writeBoolean(this.modName != null); if(this.modName != null) ByteBufUtils.writeUTF8String(to, this.modName);
        to.writeBoolean(this.rarity != null); if(this.rarity != null) RarityUtils.encode(to, this.rarity);
        to.writeBoolean(this.alwaysCapture);
        to.writeBoolean(this.isTreasure);

        to.writeBoolean(this.author != null); if(this.author != null) ByteBufUtils.writeUTF8String(to, ITextComponent.Serializer.componentToJson(this.author));
        to.writeBoolean(this.title != null); if(this.title != null) ByteBufUtils.writeUTF8String(to, ITextComponent.Serializer.componentToJson(this.title));
    }
}

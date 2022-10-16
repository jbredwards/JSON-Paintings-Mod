package git.jbredwards.jsonpaintings.common;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * {@link net.minecraft.entity.item.EntityPainting.EnumArt EnumArt} implements that at runtime
 */
public interface IJSONPainting
{
    //nonnull if this uses special rendering
    @Nonnull ResourceLocation getFrontTexture();
    void setFrontTexture(@Nonnull ResourceLocation texture);

    @Nonnull
    @SideOnly(Side.CLIENT)
    default TextureAtlasSprite getFrontSprite() {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(getFrontTexture().toString());
    }

    //nonnull if this uses special rendering
    @Nonnull ResourceLocation getBackTexture();
    void setBackTexture(@Nonnull ResourceLocation texture);

    @Nonnull
    @SideOnly(Side.CLIENT)
    default TextureAtlasSprite getBackSprite() {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(getBackTexture().toString());
    }

    //nonnull if this uses special rendering
    @Nonnull ResourceLocation getSideTexture();
    void setSideTexture(@Nonnull ResourceLocation texture);

    @Nonnull
    @SideOnly(Side.CLIENT)
    default TextureAtlasSprite getSideSprite() {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(getSideTexture().toString());
    }

    //only paintings added through this mod have this value set to true
    boolean useSpecialRenderer();
    void setUseSpecialRenderer(boolean useSpecialRenderer);

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    static IJSONPainting from(@Nonnull EntityPainting.EnumArt art) { return (IJSONPainting)(Object)art; }
}

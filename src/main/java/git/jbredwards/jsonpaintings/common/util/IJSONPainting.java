package git.jbredwards.jsonpaintings.common.util;

import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * {@link net.minecraft.entity.item.EntityPainting.EnumArt EnumArt} implements that at runtime
 */
public interface IJSONPainting
{
    //nonnull if this uses special rendering
    @Nonnull ResourceLocation getFrontTexture();
    void setFrontTexture(@Nonnull ResourceLocation texture);

    //nonnull if this uses special rendering
    @Nonnull ResourceLocation getBackTexture();
    void setBackTexture(@Nonnull ResourceLocation texture);

    //nonnull if this uses special rendering
    @Nonnull ResourceLocation getSideTexture();
    void setSideTexture(@Nonnull ResourceLocation texture);

    boolean isCreative();
    void setCreative(boolean isCreative);

    //only paintings added through this mod have this value set to true
    boolean useSpecialRenderer();
    void setUseSpecialRenderer(boolean useSpecialRenderer);

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    static IJSONPainting from(@Nonnull EntityPainting.EnumArt art) { return (IJSONPainting)(Object)art; }
}

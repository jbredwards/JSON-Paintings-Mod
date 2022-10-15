package git.jbredwards.jsonpaintings.common;

import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * {@link net.minecraft.entity.item.EntityPainting.EnumArt EnumArt} implements that at runtime
 */
public interface IJSONPainting
{
    //nonnull if this uses special rendering
    @Nonnull ResourceLocation getTexture();
    void setTexture(@Nonnull ResourceLocation texture);

    //nonnull if this uses special rendering
    @Nonnull ResourceLocation getBackTexture();
    void setBackTexture(@Nonnull ResourceLocation texture);

    int getBackOffsetX();
    void setBackOffsetX(int backOffsetX);

    int getBackOffsetY();
    void setBackOffsetY(int backOffsetY);

    //only paintings added through this mod have this value set to true
    boolean useSpecialRenderer();
    void setUseSpecialRenderer(boolean useSpecialRenderer);

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    static IJSONPainting from(@Nonnull EntityPainting.EnumArt art) { return (IJSONPainting)(Object)art; }
}

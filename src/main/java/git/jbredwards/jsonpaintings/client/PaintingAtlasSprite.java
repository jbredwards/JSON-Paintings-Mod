package git.jbredwards.jsonpaintings.client;

import net.minecraft.client.renderer.texture.PngSizeInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
@SideOnly(Side.CLIENT)
public class PaintingAtlasSprite extends TextureAtlasSprite
{
    public PaintingAtlasSprite(@Nonnull String spriteName) { super(spriteName); }

    @Override
    public void loadSprite(@Nonnull PngSizeInfo sizeInfo, boolean flag) {
        width = sizeInfo.pngWidth;
        height = sizeInfo.pngHeight;
        resetSprite();
    }
}

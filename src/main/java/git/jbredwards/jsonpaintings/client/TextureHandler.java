package git.jbredwards.jsonpaintings.client;

import git.jbredwards.jsonpaintings.common.IJSONPainting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityPainting;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
public final class TextureHandler
{
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    static void initTextures(@Nonnull TextureStitchEvent.Pre event) {
        if(event.getMap() == Minecraft.getMinecraft().getTextureMapBlocks()) {
            for(EntityPainting.EnumArt art : EntityPainting.EnumArt.values()) {
                final IJSONPainting painting = IJSONPainting.from(art);
                if(painting.useSpecialRenderer()) {
                    event.getMap().setTextureEntry(new PaintingAtlasSprite(painting.getFrontTexture().toString()));
                    event.getMap().setTextureEntry(new PaintingAtlasSprite(painting.getBackTexture().toString()));
                    event.getMap().setTextureEntry(new PaintingAtlasSprite(painting.getSideTexture().toString()));
                }
            }
        }
    }
}

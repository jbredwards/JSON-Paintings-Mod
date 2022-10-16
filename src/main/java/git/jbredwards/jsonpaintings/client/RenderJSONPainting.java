package git.jbredwards.jsonpaintings.client;

import git.jbredwards.jsonpaintings.common.IJSONPainting;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPainting;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author jbred
 *
 */
@SideOnly(Side.CLIENT)
public class RenderJSONPainting extends RenderPainting
{
    public RenderJSONPainting(@Nonnull RenderManager renderManagerIn) { super(renderManagerIn); }

    @Override
    public void doRender(@Nonnull EntityPainting entity, double x, double y, double z, float entityYaw, float partialTicks) {
        final IJSONPainting painting = IJSONPainting.from(entity.art);
        if(!painting.useSpecialRenderer()) {
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(180 - entityYaw, 0, 1, 0);
        GlStateManager.enableRescaleNormal();

        bindEntityTexture(entity);
        GlStateManager.scale(0.0625, 0.0625, 0.0625);

        if(renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(getTeamColor(entity));
        }

        renderPainting(entity, painting.getFrontSprite(), painting.getBackSprite(), painting.getSideSprite());

        if(renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        if(!renderOutlines) renderName(entity, x, y, z);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityPainting entity) {
        return IJSONPainting.from(entity.art).useSpecialRenderer()
                ? TextureMap.LOCATION_BLOCKS_TEXTURE
                : super.getEntityTexture(entity);
    }

    protected void renderPainting(@Nonnull EntityPainting entity, @Nonnull TextureAtlasSprite front, @Nonnull TextureAtlasSprite back, @Nonnull TextureAtlasSprite side) {
        final int width = entity.art.sizeX >> 4;
        final int height = entity.art.sizeY >> 4;
        final int centerX = -entity.art.sizeX >> 1;
        final int centerY = -entity.art.sizeY >> 1;
        final double factorX = 16.0 / width;
        final double factorY = 16.0 / height;

        //uv constants
        final float backMinU = back.getMinU();
        final float backMaxU = back.getMaxU();
        final float backMinV = back.getMinV();
        final float backMaxV = back.getMaxV();
        final float sideMinU = side.getMinU();
        final float sideMaxU = side.getMaxU();
        final float sideMinV = side.getMinV();
        final float sideMaxV = side.getMaxV();
        final float sideU = side.getInterpolatedU(1);
        final float sideV = side.getInterpolatedV(1);

        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                final float minX = centerX + (x << 4);
                final float maxX = centerX + ((x + 1) << 4);
                final float minY = centerY + (y << 4);
                final float maxY = centerY + ((y + 1) << 4);
                setLightmap(entity, (maxX + minX) / 2, (maxY + minY) / 2);

                final BufferBuilder buffer = Tessellator.getInstance().getBuffer();
                buffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

                //front
                final float frontMinU = front.getInterpolatedU(factorX * (width - x));
                final float frontMaxU = front.getInterpolatedU(factorX * (width - (x + 1)));
                final float frontMinV = front.getInterpolatedV(factorY * (height - y));
                final float frontMaxV = front.getInterpolatedV(factorY * (height - (y + 1)));
                buffer.pos(maxX, minY, -0.5).tex(frontMaxU, frontMinV).normal(0, 0, -1).endVertex();
                buffer.pos(minX, minY, -0.5).tex(frontMinU, frontMinV).normal(0, 0, -1).endVertex();
                buffer.pos(minX, maxY, -0.5).tex(frontMinU, frontMaxV).normal(0, 0, -1).endVertex();
                buffer.pos(maxX, maxY, -0.5).tex(frontMaxU, frontMaxV).normal(0, 0, -1).endVertex();

                //back
                buffer.pos(maxX, maxY, 0.5).tex(backMinU, backMinV).normal(0, 0, 1).endVertex();
                buffer.pos(minX, maxY, 0.5).tex(backMaxU, backMinV).normal(0, 0, 1).endVertex();
                buffer.pos(minX, minY, 0.5).tex(backMaxU, backMaxV).normal(0, 0, 1).endVertex();
                buffer.pos(maxX, minY, 0.5).tex(backMinU, backMaxV).normal(0, 0, 1).endVertex();

                //sides
                buffer.pos(maxX, maxY, -0.5).tex(sideMinU, sideMinV).normal(0, 1, 0).endVertex();
                buffer.pos(minX, maxY, -0.5).tex(sideMaxU, sideMinV).normal(0, 1, 0).endVertex();
                buffer.pos(minX, maxY, 0.5).tex(sideMaxU, sideV).normal(0, 1, 0).endVertex();
                buffer.pos(maxX, maxY, 0.5).tex(sideMinU, sideV).normal(0, 1, 0).endVertex();
                buffer.pos(maxX, minY, 0.5).tex(sideMinU, sideMinV).normal(0, -1, 0).endVertex();
                buffer.pos(minX, minY, 0.5).tex(sideMaxU, sideMinV).normal(0, -1, 0).endVertex();
                buffer.pos(minX, minY, -0.5).tex(sideMaxU, sideV).normal(0, -1, 0).endVertex();
                buffer.pos(maxX, minY, -0.5).tex(sideMinU, sideV).normal(0, -1, 0).endVertex();
                buffer.pos(maxX, maxY, 0.5).tex(sideU, sideMinV).normal(-1, 0, 0).endVertex();
                buffer.pos(maxX, minY, 0.5).tex(sideU, sideMaxV).normal(-1, 0, 0).endVertex();
                buffer.pos(maxX, minY, -0.5).tex(sideMinU, sideMaxV).normal(-1, 0, 0).endVertex();
                buffer.pos(maxX, maxY, -0.5).tex(sideMinU, sideMinV).normal(-1, 0, 0).endVertex();
                buffer.pos(minX, maxY, -0.5).tex(sideU, sideMinV).normal(1, 0, 0).endVertex();
                buffer.pos(minX, minY, -0.5).tex(sideU, sideMaxV).normal(1, 0, 0).endVertex();
                buffer.pos(minX, minY, 0.5).tex(sideMinU, sideMaxV).normal(1, 0, 0).endVertex();
                buffer.pos(minX, maxY, 0.5).tex(sideMinU, sideMinV).normal(1, 0, 0).endVertex();
                Tessellator.getInstance().draw();
            }
        }
    }
}

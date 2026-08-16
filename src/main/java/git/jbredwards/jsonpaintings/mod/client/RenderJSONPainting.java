/*
 * Copyright (c) 2025. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.client;

import git.jbredwards.jsonpaintings.api.ActivePaintingInfo;
import git.jbredwards.jsonpaintings.api.PaintingHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPainting;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
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
    public static boolean CALC_BRIGHTNESS = true;
    public RenderJSONPainting(@Nonnull RenderManager renderManagerIn) { super(renderManagerIn); }

    @Override
    public void doRender(@Nonnull final EntityPainting entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
        @Nonnull final ActivePaintingInfo painting = ActivePaintingInfo.get(entity.art);
        if(painting.frontTexture == null && painting.backTexture == null && painting.sideTexture == null) {
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

        renderPainting(entity, painting);

        if(renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        if(!renderOutlines) renderName(entity, x, y, z);
    }

    protected void renderPainting(@Nonnull final EntityPainting entity, @Nonnull final ActivePaintingInfo painting) {
        final int front = getGlTextureId(painting.frontTexture != null ? painting.frontTexture : getEntityTexture(entity));
        final int back = getGlTextureId(PaintingHelper.getBackTexture(painting));
        final int side = getGlTextureId(PaintingHelper.getSideTexture(painting));

        final int width = entity.art.sizeX >> 4;
        final int height = entity.art.sizeY >> 4;
        final int centerX = -entity.art.sizeX >> 1;
        final int centerY = -entity.art.sizeY >> 1;
        final BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        final float frontWidth = painting.frontTexture == null ? 16 : width;
        final float frontHeight = painting.frontTexture == null ? 16 : height;
        final float frontU = painting.frontTexture == null ? painting.getXOffset() / 16f : 0;
        final float frontV = painting.frontTexture == null ? painting.getYOffset() / 16f : 0;

        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                final float minX = centerX + (x << 4);
                final float maxX = centerX + ((x + 1) << 4);
                final float minY = centerY + (y << 4);
                final float maxY = centerY + ((y + 1) << 4);
                final float minU = (float)(width - x) / width;
                final float maxU = (float)(width - x - 1) / width;
                final float minV = (float)(height - y) / height;
                final float maxV = (float)(height - y - 1) / height;
                setLightmap(entity, (maxX + minX) / 2, (maxY + minY) / 2);

                //front
                final float frontMinU = (frontU + width - x) / frontWidth;
                final float frontMaxU = (frontU + width - x - 1) / frontWidth;
                final float frontMinV = (frontV + height - y) / frontHeight;
                final float frontMaxV = (frontV + height - y - 1) / frontHeight;
                GlStateManager.bindTexture(front);
                buffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
                buffer.pos(maxX, minY, -0.5).tex(frontMaxU, frontMinV).normal(0, 0, -1).endVertex();
                buffer.pos(minX, minY, -0.5).tex(frontMinU, frontMinV).normal(0, 0, -1).endVertex();
                buffer.pos(minX, maxY, -0.5).tex(frontMinU, frontMaxV).normal(0, 0, -1).endVertex();
                buffer.pos(maxX, maxY, -0.5).tex(frontMaxU, frontMaxV).normal(0, 0, -1).endVertex();
                Tessellator.getInstance().draw();

                //back
                final boolean hasBackTexture = painting.backTexture != null;
                final float backMinU = hasBackTexture ? minU : 1;
                final float backMaxU = hasBackTexture ? maxU : 0;
                final float backMinV = hasBackTexture ? minV : 1;
                final float backMaxV = hasBackTexture ? maxV : 0;
                GlStateManager.bindTexture(back);
                buffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
                buffer.pos(maxX, maxY, 0.5).tex(backMaxU, backMaxV).normal(0, 0, 1).endVertex();
                buffer.pos(minX, maxY, 0.5).tex(backMinU, backMaxV).normal(0, 0, 1).endVertex();
                buffer.pos(minX, minY, 0.5).tex(backMinU, backMinV).normal(0, 0, 1).endVertex();
                buffer.pos(maxX, minY, 0.5).tex(backMaxU, backMinV).normal(0, 0, 1).endVertex();
                Tessellator.getInstance().draw();

                //side values
                final boolean hasSideTexture = hasBackTexture || painting.sideTexture != null;
                final float sideMinU = hasSideTexture ? minU : 1;
                final float sideMaxU = hasSideTexture ? maxU : 0;
                final float sideMinV = hasSideTexture ? minV : 1;
                final float sideMaxV = hasSideTexture ? maxV : 0;
                final float sideWidth = hasSideTexture ? 1f / entity.art.sizeX : 0.0625f;
                final float sideHeight = hasSideTexture ? 1f / entity.art.sizeY : 0.0625f;
                boolean drawSide = false;

                //top
                if(y + 1 == height) {
                    drawSide = true;
                    GlStateManager.bindTexture(side);
                    buffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);


                    buffer.pos(maxX, maxY, -0.5).tex(sideMaxU, sideHeight).normal(0, 1, 0).endVertex();
                    buffer.pos(minX, maxY, -0.5).tex(sideMinU, sideHeight).normal(0, 1, 0).endVertex();
                    buffer.pos(minX, maxY, 0.5).tex(sideMinU, 0).normal(0, 1, 0).endVertex();
                    buffer.pos(maxX, maxY, 0.5).tex(sideMaxU, 0).normal(0, 1, 0).endVertex();
                }
                //bottom
                if(y == 0) {
                    if(!drawSide) {
                        drawSide = true;
                        GlStateManager.bindTexture(side);
                        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
                    }

                    buffer.pos(maxX, minY, 0.5).tex(sideMaxU, 1).normal(0, -1, 0).endVertex();
                    buffer.pos(minX, minY, 0.5).tex(sideMinU, 1).normal(0, -1, 0).endVertex();
                    buffer.pos(minX, minY, -0.5).tex(sideMinU, 1 - sideHeight).normal(0, -1, 0).endVertex();
                    buffer.pos(maxX, minY, -0.5).tex(sideMaxU, 1 - sideHeight).normal(0, -1, 0).endVertex();
                }
                //right
                if(x == 0) {
                    if(!drawSide) {
                        drawSide = true;
                        GlStateManager.bindTexture(side);
                        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
                    }

                    buffer.pos(minX, maxY, -0.5).tex(1 - sideWidth, sideMaxV).normal(1, 0, 0).endVertex();
                    buffer.pos(minX, minY, -0.5).tex(1 - sideWidth, sideMinV).normal(1, 0, 0).endVertex();
                    buffer.pos(minX, minY, 0.5).tex(1, sideMinV).normal(1, 0, 0).endVertex();
                    buffer.pos(minX, maxY, 0.5).tex(1, sideMaxV).normal(1, 0, 0).endVertex();
                }
                //left
                if(x + 1 == width) {
                    if(!drawSide) {
                        drawSide = true;
                        GlStateManager.bindTexture(side);
                        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
                    }

                    buffer.pos(maxX, maxY, 0.5).tex(0, sideMaxV).normal(-1, 0, 0).endVertex();
                    buffer.pos(maxX, minY, 0.5).tex(0, sideMinV).normal(-1, 0, 0).endVertex();
                    buffer.pos(maxX, minY, -0.5).tex(sideWidth, sideMinV).normal(-1, 0, 0).endVertex();
                    buffer.pos(maxX, maxY, -0.5).tex(sideWidth, sideMaxV).normal(-1, 0, 0).endVertex();
                }

                if(drawSide) Tessellator.getInstance().draw();
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    protected int getGlTextureId(@Nonnull final ResourceLocation location) {
        @Nullable ITextureObject texture = renderManager.renderEngine.getTexture(location);
        if(texture == null) {
            texture = new SimpleTexture(location);
            renderManager.renderEngine.loadTexture(location, texture);
        }

        return texture.getGlTextureId();
    }

    @Override
    public void setLightmap(@Nonnull final EntityPainting painting, final float x, final float y) {
        if(CALC_BRIGHTNESS) super.setLightmap(painting, x, y);
        else OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
    }
}

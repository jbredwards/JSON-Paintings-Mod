/*
 * Copyright (c) 2024. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.client;

import git.jbredwards.jsonpaintings.mod.common.util.IJSONPainting;
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

        renderPainting(entity, painting);

        if(renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        if(!renderOutlines) renderName(entity, x, y, z);
    }

    protected void renderPainting(@Nonnull final EntityPainting entity, @Nonnull final IJSONPainting painting) {
        final int front = getGlTextureId(painting.getFrontTexture());
        final int back = getGlTextureId(painting.getBackTexture());
        final int side = getGlTextureId(painting.getSideTexture());

        final int width = entity.art.sizeX >> 4;
        final int height = entity.art.sizeY >> 4;
        final int centerX = -entity.art.sizeX >> 1;
        final int centerY = -entity.art.sizeY >> 1;
        final BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                final float minX = centerX + (x << 4);
                final float maxX = centerX + ((x + 1) << 4);
                final float minY = centerY + (y << 4);
                final float maxY = centerY + ((y + 1) << 4);
                setLightmap(entity, (maxX + minX) / 2, (maxY + minY) / 2);

                //front
                final float frontMinU = (float)(width - x) / width;
                final float frontMaxU = (float)(width - x - 1) / width;
                final float frontMinV = (float)(height - y) / height;
                final float frontMaxV = (float)(height - y - 1) / height;
                GlStateManager.bindTexture(front);
                buffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
                buffer.pos(maxX, minY, -0.5).tex(frontMaxU, frontMinV).normal(0, 0, -1).endVertex();
                buffer.pos(minX, minY, -0.5).tex(frontMinU, frontMinV).normal(0, 0, -1).endVertex();
                buffer.pos(minX, maxY, -0.5).tex(frontMinU, frontMaxV).normal(0, 0, -1).endVertex();
                buffer.pos(maxX, maxY, -0.5).tex(frontMaxU, frontMaxV).normal(0, 0, -1).endVertex();
                Tessellator.getInstance().draw();

                //back
                final float backMinU = painting.hasBackTexture() ? frontMinU : 1;
                final float backMaxU = painting.hasBackTexture() ? frontMaxU : 0;
                final float backMinV = painting.hasBackTexture() ? frontMinV : 1;
                final float backMaxV = painting.hasBackTexture() ? frontMaxV : 0;
                GlStateManager.bindTexture(back);
                buffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
                buffer.pos(maxX, maxY, 0.5).tex(backMaxU, backMaxV).normal(0, 0, 1).endVertex();
                buffer.pos(minX, maxY, 0.5).tex(backMinU, backMaxV).normal(0, 0, 1).endVertex();
                buffer.pos(minX, minY, 0.5).tex(backMinU, backMinV).normal(0, 0, 1).endVertex();
                buffer.pos(maxX, minY, 0.5).tex(backMaxU, backMinV).normal(0, 0, 1).endVertex();
                Tessellator.getInstance().draw();

                //side values
                final float sideMinU = painting.hasSideTexture() ? frontMinU : 1;
                final float sideMaxU = painting.hasSideTexture() ? frontMaxU : 0;
                final float sideMinV = painting.hasSideTexture() ? frontMinV : 1;
                final float sideMaxV = painting.hasSideTexture() ? frontMaxV : 0;
                final float sideWidth = painting.hasSideTexture() ? 1f / entity.art.sizeX : 0.0625f;
                final float sideHeight = painting.hasSideTexture() ? 1f / entity.art.sizeY : 0.0625f;
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

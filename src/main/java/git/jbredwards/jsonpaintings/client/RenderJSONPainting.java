package git.jbredwards.jsonpaintings.client;

import git.jbredwards.jsonpaintings.common.IJSONPainting;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
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

        final int front = getGlTextureId(painting.getFrontTexture());
        final int back = getGlTextureId(painting.getBackTexture());
        final int side = getGlTextureId(painting.getSideTexture());
        renderPainting(entity, front, back, side);

        if(renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        if(!renderOutlines) renderName(entity, x, y, z);
    }

    protected void renderPainting(@Nonnull EntityPainting entity, int front, int back, int side) {
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
                GlStateManager.bindTexture(back);
                buffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
                buffer.pos(maxX, maxY, 0.5).tex(0, 0).normal(0, 0, 1).endVertex();
                buffer.pos(minX, maxY, 0.5).tex(1, 0).normal(0, 0, 1).endVertex();
                buffer.pos(minX, minY, 0.5).tex(1, 1).normal(0, 0, 1).endVertex();
                buffer.pos(maxX, minY, 0.5).tex(0, 1).normal(0, 0, 1).endVertex();
                Tessellator.getInstance().draw();

                //sides
                GlStateManager.bindTexture(side);
                buffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
                buffer.pos(maxX, maxY, -0.5).tex(0, 0).normal(0, 1, 0).endVertex();
                buffer.pos(minX, maxY, -0.5).tex(1, 0).normal(0, 1, 0).endVertex();
                buffer.pos(minX, maxY, 0.5).tex(1, 0.0625f).normal(0, 1, 0).endVertex();
                buffer.pos(maxX, maxY, 0.5).tex(0, 0.0625f).normal(0, 1, 0).endVertex();
                buffer.pos(maxX, minY, 0.5).tex(0, 0).normal(0, -1, 0).endVertex();
                buffer.pos(minX, minY, 0.5).tex(1, 0).normal(0, -1, 0).endVertex();
                buffer.pos(minX, minY, -0.5).tex(1, 0.0625f).normal(0, -1, 0).endVertex();
                buffer.pos(maxX, minY, -0.5).tex(0, 0.0625f).normal(0, -1, 0).endVertex();
                buffer.pos(maxX, maxY, 0.5).tex(0.0625f, 0).normal(-1, 0, 0).endVertex();
                buffer.pos(maxX, minY, 0.5).tex(0.0625f, 1).normal(-1, 0, 0).endVertex();
                buffer.pos(maxX, minY, -0.5).tex(0, 1).normal(-1, 0, 0).endVertex();
                buffer.pos(maxX, maxY, -0.5).tex(0, 0).normal(-1, 0, 0).endVertex();
                buffer.pos(minX, maxY, -0.5).tex(0.0625f, 0).normal(1, 0, 0).endVertex();
                buffer.pos(minX, minY, -0.5).tex(0.0625f, 1).normal(1, 0, 0).endVertex();
                buffer.pos(minX, minY, 0.5).tex(0, 1).normal(1, 0, 0).endVertex();
                buffer.pos(minX, maxY, 0.5).tex(0, 0).normal(1, 0, 0).endVertex();
                Tessellator.getInstance().draw();
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    protected int getGlTextureId(@Nonnull ResourceLocation location) {
        @Nullable ITextureObject texture = renderManager.renderEngine.getTexture(location);
        if(texture == null) {
            texture = new SimpleTexture(location);
            renderManager.renderEngine.loadTexture(location, texture);
        }

        return texture.getGlTextureId();
    }
}

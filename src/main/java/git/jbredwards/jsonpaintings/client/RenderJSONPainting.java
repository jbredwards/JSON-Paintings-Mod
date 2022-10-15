package git.jbredwards.jsonpaintings.client;

import git.jbredwards.jsonpaintings.common.IJSONPainting;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPainting;
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
        if(!IJSONPainting.from(entity.art).useSpecialRenderer()) {
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(180 - entityYaw, 0, 1, 0);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(0.0625, 0.0625, 0.0625);

        if(renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(getTeamColor(entity));
        }

        renderPainting(entity);

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
        final IJSONPainting painting = IJSONPainting.from(entity.art);
        return painting.useSpecialRenderer() ? painting.getTexture()
                : super.getEntityTexture(entity);
    }

    protected void renderPainting(@Nonnull EntityPainting entity) {
        final int width = entity.art.sizeX;
        final int height = entity.art.sizeY;
        //I actually have no idea what these do
        //so the names are probably terrible
        final float originX = -width / 2f;
        final float originY = -height / 2f;

        for(int x = 0; x < width >> 4; x++) {
            for(int y = 0; y < height >> 4; y++) {
                final float minX = originX + ((x + 1) << 4);
                final float maxX = originX + (x << 4);
                final float minY = originY + ((y + 1) << 4);
                final float maxY = originY + (y << 4);
                setLightmap(entity, (minX + maxX) / 2, (minY + maxY) / 2);
                BufferBuilder buffer;

                //front
                bindEntityTexture(entity);
                buffer = Tessellator.getInstance().getBuffer();
                buffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

                Tessellator.getInstance().draw();

                //back & sides
                bindTexture(IJSONPainting.from(entity.art).getBackTexture());
                buffer = Tessellator.getInstance().getBuffer();
                buffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

                Tessellator.getInstance().draw();
            }
        }
    }
}

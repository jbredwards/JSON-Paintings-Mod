/*
 * Copyright (c) 2025. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.common.compat.top;

import git.jbredwards.jsonpaintings.api.ActivePaintingInfo;
import git.jbredwards.jsonpaintings.api.PaintingHelper;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.config.Config;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 *
 * @author jbred
 *
 */
public final class TOPHandler
{
    public static int TEXT_COMPONENT_ELEMENT_ID;
    public static void initialize() {
        TEXT_COMPONENT_ELEMENT_ID = TheOneProbe.theOneProbeImp.registerElementFactory(buf -> {
            try { return new ElementTextComponent(buf); }
            catch(IOException e) { throw new RuntimeException(e); }
        });

        TheOneProbe.theOneProbeImp.registerEntityDisplayOverride((mode, probeInfo, player, world, entity, data) -> {
            if(entity instanceof EntityPainting) {
                @Nonnull final EntityPainting.EnumArt art = ((EntityPainting)entity).art;
                @Nonnull final ActivePaintingInfo info = ActivePaintingInfo.get(art);

                @Nonnull final ItemStack painting = PaintingHelper.write(new ItemStack(Items.PAINTING), art);
                @Nonnull final ITextComponent name = new TextComponentTranslation(painting.getTranslationKey() + ".name")
                        .setStyle(new Style().setColor(PaintingHelper.getRarity(info).getColor()));
                @Nonnull final ITextComponent motive = PaintingHelper.getTitle(info, art.title);

                // main info + mod name
                final boolean modName = Tools.show(mode, Config.getRealConfig().getShowModName());
                if(modName) probeInfo.horizontal()
                        .item(painting)
                        .vertical()
                        .element(new ElementTextComponent(TextStyleClass.INFO, name))
                        .element(new ElementTextComponent(TextStyleClass.INFO, new TextComponentTranslation("jsonpaintings.wailaMotive", motive)));

                // main info
                else probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                        .item(painting)
                        .element(new ElementTextComponent(TextStyleClass.INFO, name))
                        .element(new ElementTextComponent(TextStyleClass.INFO, new TextComponentTranslation("jsonpaintings.wailaMotive", motive)));

                // author
                if(info.author != null) probeInfo.element(new ElementTextComponent(TextStyleClass.INFO, info.author));
                if(modName) probeInfo.element(new ElementTextComponent(TextStyleClass.MODNAME, new TextComponentString(PaintingHelper.getModName(info))));

                // sneak to capture
                if(!player.isCreative() && !player.isSpectator()) {
                    final boolean v = Config.harvestStyleVanilla;
                    final int offs = v ? 16 : 0;
                    final int dim = v ? 13 : 16;

                    probeInfo = probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                            .icon(new ResourceLocation(TheOneProbe.MODID, "textures/gui/icons.png"), player.isSneaking() ? 0 : 16, offs, dim, dim,
                                    probeInfo.defaultIconStyle().width(v ? 18 : 20).height(v ? 14 : 16).textureWidth(32).textureHeight(32))
                            .element(new ElementTextComponent(player.isSneaking() ? TextStyleClass.OK : TextStyleClass.WARNING, new TextComponentTranslation("jsonpaintings.wailaSneakToCapture")));
                }

                // exclusive
                if(info.isTreasure) probeInfo.element(new ElementTextComponent(TextStyleClass.INFO, new TextComponentTranslation("jsonpaintings.wailaExclusive")));
                return true;
            }

            return false;
        });
    }
}

/*
 * Copyright (c) 2025. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.common.compat.top;

import git.jbredwards.jsonpaintings.mod.common.util.IJSONPainting;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.config.Config;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Optional;

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
                @Nonnull final ITextComponent motive = new TextComponentTranslation(art.title)
                        .setStyle(new Style().setColor(Optional.ofNullable(IJSONPainting.from(art).getRarity())
                        .orElseGet(() -> IJSONPainting.from(art).isCreative() ? EnumRarity.EPIC : EnumRarity.UNCOMMON).getColor()));

                // main info + mod name
                if(Tools.show(mode, Config.getRealConfig().getShowModName())) probeInfo.horizontal()
                        .item(new ItemStack(Items.PAINTING))
                        .vertical()
                        .element(new ElementTextComponent(TextStyleClass.INFO, new TextComponentTranslation("jsonpaintings.wailaMotive", motive)))
                        .element(new ElementTextComponent(TextStyleClass.MODNAME, new TextComponentString(IJSONPainting.from(art).getModNameOrDefault())));

                // main info
                else probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                        .item(new ItemStack(Items.PAINTING))
                        .element(new ElementTextComponent(TextStyleClass.INFO, new TextComponentTranslation("jsonpaintings.wailaMotive", motive)));

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
                if(IJSONPainting.from(art).isCreative()) probeInfo.element(new ElementTextComponent(TextStyleClass.INFO, new TextComponentTranslation("jsonpaintings.wailaExclusive")));
                return true;
            }

            return false;
        });
    }
}

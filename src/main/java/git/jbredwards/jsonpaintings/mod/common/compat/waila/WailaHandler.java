/*
 * Copyright (c) 2025. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.common.compat.waila;

import com.google.common.base.Strings;
import git.jbredwards.jsonpaintings.api.ActivePaintingInfo;
import git.jbredwards.jsonpaintings.api.PaintingHelper;
import git.jbredwards.jsonpaintings.mod.JSONPaintings;
import mcp.mobius.waila.api.*;
import mcp.mobius.waila.config.FormattingConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.util.List;

/**
 *
 * @author jbred
 *
 */
@WailaPlugin
public final class WailaHandler implements IWailaPlugin
{
    @Override
    public void register(@Nonnull final IWailaRegistrar registrar) {
        registrar.addConfig(JSONPaintings.MODID, "showMotive", true);
        registrar.addConfig(JSONPaintings.MODID, "showExclusive", true);
        registrar.addConfig(JSONPaintings.MODID, "showSneakToCapture", true);
        registrar.registerHeadProvider(Provider.INSTANCE, EntityPainting.class);
        registrar.registerBodyProvider(Provider.INSTANCE, EntityPainting.class);
        registrar.registerTailProvider(Provider.INSTANCE, EntityPainting.class);
        registrar.registerTooltipRenderer(JSONPaintings.MODID + ":component", TTRenderComponent.INSTANCE);
    }

    enum Provider implements IWailaEntityProvider
    {
        INSTANCE;

        @Nonnull
        @Override
        public List<String> getWailaHead(@Nonnull final Entity entity, @Nonnull final List<String> currentTip, @Nonnull final IWailaEntityAccessor accessor, @Nonnull final IWailaConfigHandler config) {
            if(entity instanceof EntityPainting && !currentTip.isEmpty()) {
                @Nonnull final ActivePaintingInfo info = ActivePaintingInfo.get(((EntityPainting)entity).art);
                currentTip.set(currentTip.size() - 1, PaintingHelper.getRarity(info).getColor() + currentTip.get(currentTip.size() - 1).split("\u00a7f")[1]);
            }

            return currentTip;
        }

        @Nonnull
        @Override
        public List<String> getWailaBody(@Nonnull final Entity entity, @Nonnull final List<String> currentTip, @Nonnull final IWailaEntityAccessor accessor, @Nonnull final IWailaConfigHandler config) {
            if(entity instanceof EntityPainting) {
                @Nonnull final EntityPainting.EnumArt art = ((EntityPainting)entity).art;
                @Nonnull final ActivePaintingInfo info = ActivePaintingInfo.get(art);
                if(config.getConfig("showMotive")) {
                    currentTip.add(TTRenderComponent.toString(new TextComponentTranslation("jsonpaintings.wailaMotive", PaintingHelper.getTitle(info, art.title))));
                    if(info.author != null) currentTip.add(TTRenderComponent.toString(info.author));
                }

                currentTip.add("");
                if(!accessor.getPlayer().isCreative() && !accessor.getPlayer().isSpectator() && !info.alwaysCapture && config.getConfig("showSneakToCapture"))
                    currentTip.add(TTRenderComponent.toString(new TextComponentTranslation("jsonpaintings.wailaSneakToCapture").setStyle(new Style().setColor(TextFormatting.DARK_GREEN))));

                if(info.isTreasure && config.getConfig("showExclusive"))
                    currentTip.add(TTRenderComponent.toString(new TextComponentTranslation("jsonpaintings.wailaExclusive")));
            }

            return currentTip;
        }

        @Nonnull
        @Override
        public List<String> getWailaTail(@Nonnull final Entity entity, @Nonnull final List<String> currentTip, @Nonnull final IWailaEntityAccessor accessor, @Nonnull final IWailaConfigHandler config) {
            if(entity instanceof EntityPainting && !Strings.isNullOrEmpty(FormattingConfig.modNameFormat)) {
                currentTip.remove(currentTip.size() - 1);
                currentTip.add(String.format(FormattingConfig.modNameFormat, PaintingHelper.getModName(ActivePaintingInfo.get(((EntityPainting)entity).art))));
            }

            return currentTip;
        }
    }
}

/*
 * Copyright (c) 2024. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.common.compat.waila;

import com.google.common.base.Strings;
import git.jbredwards.jsonpaintings.mod.common.util.IJSONPainting;
import mcp.mobius.waila.api.*;
import mcp.mobius.waila.config.FormattingConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.IRarity;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

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
        registrar.registerHeadProvider(Provider.INSTANCE, EntityPainting.class);
        registrar.registerBodyProvider(Provider.INSTANCE, EntityPainting.class);
        registrar.registerTailProvider(Provider.INSTANCE, EntityPainting.class);
        registrar.addConfig("jsonpaintings", "showMotive", true);
        registrar.addConfig("jsonpaintings", "showExclusive", true);
        registrar.addConfig("jsonpaintings", "showSneakToCapture", true);
    }

    enum Provider implements IWailaEntityProvider
    {
        INSTANCE;

        @Nonnull
        @Override
        public List<String> getWailaHead(@Nonnull final Entity entity, @Nonnull final List<String> currentTip, @Nonnull final IWailaEntityAccessor accessor, @Nonnull final IWailaConfigHandler config) {
            if(entity instanceof EntityPainting && !currentTip.isEmpty()) {
                @Nonnull final EntityPainting.EnumArt art = ((EntityPainting)entity).art;
                currentTip.set(currentTip.size() - 1, Optional.ofNullable(IJSONPainting.from(art).getRarity()).map(IRarity::getColor)
                        .orElseGet(() -> (IJSONPainting.from(art).isCreative() ? EnumRarity.EPIC : EnumRarity.UNCOMMON).getColor())
                        + currentTip.get(currentTip.size() - 1).split("\u00a7f")[1]);
            }

            return currentTip;
        }

        @Nonnull
        @Override
        public List<String> getWailaBody(@Nonnull final Entity entity, @Nonnull final List<String> currentTip, @Nonnull final IWailaEntityAccessor accessor, @Nonnull final IWailaConfigHandler config) {
            if(entity instanceof EntityPainting) {
                @Nonnull final EntityPainting.EnumArt art = ((EntityPainting)entity).art;
                if(config.getConfig("showMotive"))
                    currentTip.add(I18n.translateToLocalFormatted("jsonpaintings.wailaMotive", TextFormatting.GRAY + art.title));

                currentTip.add("");
                if(!accessor.getPlayer().isCreative() && !accessor.getPlayer().isSpectator() && !IJSONPainting.from(art).alwaysCapture() && config.getConfig("showSneakToCapture"))
                    currentTip.add(TextFormatting.DARK_GREEN + I18n.translateToLocal("jsonpaintings.wailaSneakToCapture"));

                if(IJSONPainting.from(art).isCreative() && config.getConfig("showExclusive"))
                    currentTip.add(I18n.translateToLocal("jsonpaintings.wailaExclusive"));
            }

            return currentTip;
        }

        @Nonnull
        @Override
        public List<String> getWailaTail(@Nonnull final Entity entity, @Nonnull final List<String> currentTip, @Nonnull final IWailaEntityAccessor accessor, @Nonnull final IWailaConfigHandler config) {
            if(entity instanceof EntityPainting && !Strings.isNullOrEmpty(FormattingConfig.modNameFormat)) {
                currentTip.remove(currentTip.size() - 1);
                currentTip.add(String.format(FormattingConfig.modNameFormat, IJSONPainting.from(((EntityPainting)entity).art).getModNameOrDefault()));
            }

            return currentTip;
        }
    }
}

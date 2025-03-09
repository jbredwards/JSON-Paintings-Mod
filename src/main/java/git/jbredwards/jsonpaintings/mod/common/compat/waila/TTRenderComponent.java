/*
 * Copyright (c) 2025. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.common.compat.waila;

import git.jbredwards.jsonpaintings.mod.JSONPaintings;
import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.api.SpecialChars;
import mcp.mobius.waila.config.OverlayConfig;
import mcp.mobius.waila.overlay.DisplayUtil;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.awt.Dimension;
import java.util.Optional;

/**
 *
 * @author jbred
 *
 */
public enum TTRenderComponent implements IWailaTooltipRenderer
{
    INSTANCE;

    @Nonnull
    @Override
    public Dimension getSize(@Nonnull final String[] params, @Nonnull final IWailaCommonAccessor accessor) {
        @Nonnull final String text = fromString(params);
        return new Dimension(DisplayUtil.getDisplayWidth(text), text.equals("") ? 0 : 8);
    }

    @Override
    public void draw(@Nonnull final String[] params, @Nonnull final IWailaCommonAccessor accessor) {
        DisplayUtil.drawString(fromString(params), 0, 0, OverlayConfig.fontcolor, true);
    }

    @Nonnull
    public static String fromString(@Nonnull final String[] params) {
        return Optional.ofNullable(ITextComponent.Serializer.jsonToComponent(params[0])).map(ITextComponent::getFormattedText).orElse(params[0]);
    }

    @Nonnull
    public static String toString(@Nonnull final ITextComponent component) {
        return SpecialChars.getRenderString(JSONPaintings.MODID + ":component", ITextComponent.Serializer.componentToJson(component));
    }
}

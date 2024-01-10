/*
 * Copyright (c) 2024. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.common.compat.top;

import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.api.TextStyleClass;
import mcjty.theoneprobe.apiimpl.client.ElementTextRender;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 *
 * @author jbred
 *
 */
public class ElementTextComponent implements IElement
{
    @Nonnull protected final TextStyleClass style;
    @Nonnull protected final ITextComponent component;

    public ElementTextComponent(@Nonnull final TextStyleClass styleIn, @Nonnull final ITextComponent componentIn) {
        style = styleIn;
        component = componentIn;
    }

    @Override
    public void toBytes(@Nonnull final ByteBuf buf) { new PacketBuffer(buf).writeEnumValue(style).writeTextComponent(component); }
    public ElementTextComponent(@Nonnull final ByteBuf buf) throws IOException {
        this(new PacketBuffer(buf).readEnumValue(TextStyleClass.class), new PacketBuffer(buf).readTextComponent());
    }

    @Override
    public void render(final int x, final int y) { ElementTextRender.render(style + component.getFormattedText(), x, y); }

    @Override
    public int getWidth() { return Minecraft.getMinecraft().fontRenderer.getStringWidth(component.getFormattedText()); }

    @Override
    public int getHeight() { return 10; }

    @Override
    public int getID() { return TOPHandler.TEXT_COMPONENT_ELEMENT_ID; }
}

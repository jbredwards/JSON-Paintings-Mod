/*
 * Copyright (c) 2025. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.client;

import com.google.common.collect.Sets;
import git.jbredwards.jsonpaintings.mod.JSONPaintings;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.ResourcePackFileNotFoundException;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLContainerHolder;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * Allows modpack developers to add their own painting textures
 * @author jbred
 *
 */
@SideOnly(Side.CLIENT)
public class PaintingsResourcePack extends FolderResourcePack implements FMLContainerHolder
{
    @Nonnull
    public static final File LOCATION = new File(Launch.minecraftHome, "paintings");

    @Nonnull
    protected final ModContainer container;
    public PaintingsResourcePack(@Nonnull final ModContainer containerIn) {
        super(LOCATION);
        container = containerIn;
    }

    @Nonnull
    @Override
    public ModContainer getFMLContainer() {
        return container;
    }

    @Nonnull
    @Override
    public Set<String> getResourceDomains() {
        return Sets.newHashSet(JSONPaintings.MODID);
    }

    @Override
    public boolean resourceExists(@Nonnull final ResourceLocation location) {
        return location.getNamespace().equals(JSONPaintings.MODID) && hasResourceName(location.getPath());
    }

    @Nonnull
    @Override
    public InputStream getInputStream(@Nonnull final ResourceLocation location) throws IOException {
        if(location.getNamespace().equals(JSONPaintings.MODID)) return getInputStreamByName(location.getPath());
        else throw new ResourcePackFileNotFoundException(resourcePackFile, "Invalid modid");
    }

    @Nonnull
    @Override
    protected InputStream getInputStreamByName(@Nonnull final String name) throws IOException {
        return !"pack.mcmeta".equals(name) ? super.getInputStreamByName(name) : new ByteArrayInputStream((
                "{\n" +
                " \"pack\": {\n"+
                "   \"description\": \"Reads assets for player-defined paintings by JSON Paintings.\",\n"+
                "   \"pack_format\": 2\n"+
                "}\n" +
                "}").getBytes(StandardCharsets.UTF_8));
    }
}

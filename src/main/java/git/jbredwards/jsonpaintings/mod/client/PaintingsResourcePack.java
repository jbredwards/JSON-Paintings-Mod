/*
 * Copyright (c) 2025. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.client;

import com.google.common.collect.Sets;
import git.jbredwards.jsonpaintings.mod.JSONPaintings;
import git.jbredwards.jsonpaintings.mod.asm.ASMHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPainting;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.ResourcePackFileNotFoundException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLContainerHolder;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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
    protected final ModContainer container;
    public PaintingsResourcePack(@Nonnull final ModContainer containerIn) {
        super(ASMHandler.paintingsLocation.toFile());
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
        return location.getNamespace().equals(JSONPaintings.MODID) && (
                "textures/paintings/back.png".equals(location.getPath()) ||
                "pack.mcmeta".equals(location.getPath()) || hasResourceName(location.getPath()));
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
        switch(name) {
            // Pack data.
            case "pack.mcmeta": return new ByteArrayInputStream((
                "{\n" +
                "  \"pack\": {\n"+
                "    \"description\": \"Reads assets for player-defined paintings by JSON Paintings.\",\n"+
                "    \"pack_format\": 2\n"+
                "  }\n" +
                "}").getBytes(StandardCharsets.UTF_8));
            // Dynamically generate painting's back texture using the Vanilla painting atlas.
            case "textures/paintings/back.png": {
                @Nonnull final IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
                @Nonnull final ByteArrayOutputStream out = new ByteArrayOutputStream();

                @Nonnull final BufferedImage atlas;
                try(@Nonnull final IResource resource = manager.getResource(RenderPainting.KRISTOFFER_PAINTING_TEXTURE)) {
                    atlas = ImageIO.read(resource.getInputStream());
                }

                final int width = atlas.getWidth() >> 4, height = atlas.getHeight() >> 4;
                ImageIO.write(atlas.getSubimage(atlas.getWidth() - width, 0, width, height), "PNG", out);
                return new ByteArrayInputStream(out.toByteArray());
            }
            // Search folder.
            default: return super.getInputStreamByName(name);
        }
    }
}

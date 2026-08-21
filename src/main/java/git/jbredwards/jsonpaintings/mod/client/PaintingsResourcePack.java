/*
 * Copyright (c) 2025. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import git.jbredwards.jsonpaintings.mod.JSONPaintings;
import git.jbredwards.jsonpaintings.mod.asm.ASMHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPainting;
import net.minecraft.client.resources.*;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLContainerHolder;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Allows modpack developers to add their own painting textures
 * @author jbred
 *
 */
@SideOnly(Side.CLIENT)
public class PaintingsResourcePack extends FolderResourcePack implements FMLContainerHolder, Closeable
{
    @Nonnull protected final List<IResourcePack> paintingPacks;
    @Nonnull protected final ModContainer container;

    public PaintingsResourcePack(@Nonnull final ModContainer containerIn) {
        super(ASMHandler.paintingsLocation.toFile());
        paintingPacks = new ArrayList<>();
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
        close();

        @Nonnull final Set<String> domains = new HashSet<>();
        @Nullable final File[] packs = ASMHandler.paintingsLocation.resolve("packs").toFile().listFiles();

        if(packs != null) for(@Nonnull final File pack : packs) {
            @Nonnull final IResourcePack resourcePack = pack.isDirectory() ? new FolderResourcePack(pack) : new FileResourcePack(pack);
            @Nonnull final Set<String> packDomains = resourcePack.getResourceDomains();

            domains.addAll(packDomains);
            if(!packDomains.isEmpty()) paintingPacks.add(resourcePack);
            else if(resourcePack instanceof Closeable) IOUtils.closeQuietly((Closeable)resourcePack);
        }

        domains.add(JSONPaintings.MODID);
        return domains;
    }

    @Override
    public boolean resourceExists(@Nonnull final ResourceLocation location) {
        @Nullable final ResourceLocation langLocation = toModernLang(location);
        for(@Nonnull final IResourcePack pack : paintingPacks) {
            if(pack.resourceExists(location) || langLocation != null && pack.resourceExists(langLocation)) return true;
        }

        return location.getNamespace().equals(JSONPaintings.MODID) && (
                "textures/paintings/back.png".equals(location.getPath()) ||
                "pack.mcmeta".equals(location.getPath()) || hasResourceName(location.getPath()));
    }

    @Nonnull
    @Override
    public InputStream getInputStream(@Nonnull final ResourceLocation location) throws IOException {
        // Convert json-based lang format to 1.12's format.
        @Nullable final ResourceLocation langLocation = toModernLang(location);
        for(@Nonnull final IResourcePack pack : paintingPacks) {
            if(pack.resourceExists(location)) return pack.getInputStream(location);
            else if(langLocation != null && pack.resourceExists(langLocation)) {
                @Nonnull final JsonObject modernLang;
                try(@Nonnull final Reader reader = new InputStreamReader(pack.getInputStream(langLocation))) {
                    modernLang = new JsonParser().parse(reader).getAsJsonObject();
                }

                @Nonnull final StringBuilder builder = new StringBuilder();
                for(@Nonnull final Map.Entry<String, JsonElement> entry : modernLang.entrySet()) {
                    builder.append(entry.getKey()).append('=').append(JsonUtils.getString(entry.getValue(), entry.getKey())).append("\n");
                }

                return new ByteArrayInputStream(builder.toString().getBytes(StandardCharsets.UTF_8));
            }
        }

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

    @Override
    public void close() {
        for(@Nonnull final IResourcePack pack : paintingPacks) if(pack instanceof Closeable) IOUtils.closeQuietly((Closeable)pack);
        paintingPacks.clear();
    }

    @Nullable
    private static ResourceLocation toModernLang(@Nonnull final ResourceLocation location) {
        @Nonnull final String path = location.getPath();
        return path.endsWith(".lang") ? new ResourceLocation(location.getNamespace(), path.substring(0, path.length() - 5) + ".json") : null;
    }
}

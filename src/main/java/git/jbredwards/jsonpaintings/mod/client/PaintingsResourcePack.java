/*
 * Copyright (c) 2024. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.client;

import git.jbredwards.jsonpaintings.mod.JSONPaintings;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLFileResourcePack;
import net.minecraftforge.fml.client.FMLFolderResourcePack;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Set;

/**
 * Allows modpack developers to add their own painting textures
 * @author jbred
 *
 */
@SideOnly(Side.CLIENT)
public class PaintingsResourcePack implements IResourcePack
{
    @Nonnull
    protected final IResourcePack modResourcePack, runResourcePack;
    public PaintingsResourcePack(@Nonnull ModContainer containerIn) {
        //handle normal resourcepack
        modResourcePack = containerIn.getSource().isDirectory()
                ? new FMLFolderResourcePack(containerIn)
                : new FMLFileResourcePack(containerIn);

        //handle run/paintings/textures resourcepack
        runResourcePack = new FolderResourcePack(new File(Launch.minecraftHome, "paintings")) {
            @Nonnull
            @Override
            public InputStream getInputStream(@Nonnull ResourceLocation location) throws IOException {
                if(!location.getNamespace().equals(JSONPaintings.MODID)) throw new FileNotFoundException("Invalid modid");
                else return getInputStreamByName(location.getPath());
            }

            @Override
            public boolean resourceExists(@Nonnull ResourceLocation location) {
                return location.getNamespace().equals(JSONPaintings.MODID) && hasResourceName(location.getPath());
            }
        };
    }

    @Nonnull
    @Override
    public InputStream getInputStream(@Nonnull ResourceLocation location) throws IOException {
        try { return modResourcePack.getInputStream(location); }
        catch(IOException e) { return runResourcePack.getInputStream(location); }
    }

    @Override
    public boolean resourceExists(@Nonnull ResourceLocation location) {
        if(modResourcePack.resourceExists(location)) return true;
        else return runResourcePack.resourceExists(location);
    }

    @Nullable
    @Override
    public <T extends IMetadataSection> T getPackMetadata(@Nonnull MetadataSerializer serializer, @Nonnull String sectionName) throws IOException {
        return modResourcePack.getPackMetadata(serializer, sectionName);
    }

    @Nonnull
    @Override
    public BufferedImage getPackImage() throws IOException { return modResourcePack.getPackImage(); }

    @Nonnull
    @Override
    public Set<String> getResourceDomains() { return modResourcePack.getResourceDomains(); }

    @Nonnull
    @Override
    public String getPackName() { return modResourcePack.getPackName(); }
}

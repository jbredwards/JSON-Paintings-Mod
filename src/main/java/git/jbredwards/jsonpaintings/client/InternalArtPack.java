package git.jbredwards.jsonpaintings.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ModContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

/**
 * Allows modpack developers to add their own painting textures
 * @author jbred
 *
 */
public class InternalArtPack extends FolderResourcePack
{
    @Nonnull
    public static final Set<String> DOMAIN = Collections.singleton("jsonpaintings");
    public InternalArtPack(@Nullable ModContainer unused) { super(new File(Launch.minecraftHome, "paintings")); }

    @Nonnull
    @Override
    public InputStream getInputStream(@Nonnull ResourceLocation location) throws IOException {
        if(!location.getNamespace().equals("jsonpaintings")) throw new FileNotFoundException("Invalid modid");
        return getInputStreamByName("paintings/" + location.getPath());
    }

    @Override
    public boolean resourceExists(@Nonnull ResourceLocation location) {
        if(!location.getNamespace().equals("jsonpaintings")) return false;
        return hasResourceName("paintings/" + location.getPath());
    }

    @Nonnull
    @Override
    public Set<String> getResourceDomains() { return DOMAIN; }

    @Nonnull
    @Override
    public <T extends IMetadataSection> T getPackMetadata(@Nonnull MetadataSerializer metadataSerializer, @Nonnull String metadataSectionName) {
        return metadataSerializer.parseMetadataSection(metadataSectionName, (JsonObject)(new JsonParser().parse(
            "{\n" +
            "    \"pack\": {\n" +
            "        \"description\": \"Includes painting textures added by the modpack.\",\n" +
            "        \"pack_format\": 3\n" +
            "    }\n" +
            "}"
        )));
    }
}

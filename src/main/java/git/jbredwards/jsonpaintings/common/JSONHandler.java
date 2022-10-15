package git.jbredwards.jsonpaintings.common;

import com.google.gson.*;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 *
 * @author jbred
 *
 */
public final class JSONHandler
{
    @Nonnull
    public static final ResourceLocation DEFAULT_BACK_TEXTURE = new ResourceLocation(
            "jsonpaintings", "textures/paintings/back.png");

    @Nonnull
    static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(EntityPainting.EnumArt.class, ArtDeserializer.INSTANCE)
            .create();

    //reads the minecraft run folder
    public static void readInstance() throws IOException {
        final File file = new File("paintings", "paintings.json");
        if(file.exists()) {
            ArtDeserializer.INSTANCE.modName = "jsonpaintings";
            ArtDeserializer.INSTANCE.defaultTexture = "textures/";
            GSON.fromJson(IOUtils.toString(new FileInputStream(file), Charset.defaultCharset()), EntityPainting.EnumArt[].class);
        }
    }

    //reads each mod
    public static void readMods() throws IOException {
        for(String modId : Loader.instance().getIndexedModList().keySet()) {
            final @Nullable InputStream file = Loader.class.getResourceAsStream(String.format("/assets/%s/paintings/paintings.json", modId));
            if(file != null) {
                ArtDeserializer.INSTANCE.modName = modId;
                ArtDeserializer.INSTANCE.defaultTexture = "textures/paintings/";
                GSON.fromJson(IOUtils.toString(file, Charset.defaultCharset()), EntityPainting.EnumArt[].class);
            }
        }
    }

    enum ArtDeserializer implements JsonDeserializer<EntityPainting.EnumArt>
    {
        INSTANCE;

        String modName;
        String defaultTexture;

        @Nullable
        @Override
        public EntityPainting.EnumArt deserialize(@Nonnull JsonElement json, @Nonnull Type typeOfT, @Nonnull JsonDeserializationContext context) throws JsonParseException {
            try {
                final NBTTagCompound nbt = JsonToNBT.getTagFromJson(json.toString());
                final String name = nbt.getString("name").toLowerCase();
                if(!name.isEmpty()) {
                    //builds the art enum
                    final EntityPainting.EnumArt art = EnumHelper.addArt(
                            name.toUpperCase(),
                            nbt.hasKey("title", Constants.NBT.TAG_STRING)
                                    ? nbt.getString("title")
                                    : StringUtils.capitalize(name),
                            Math.min(nbt.getInteger("width") << 4, 16),
                            Math.min(nbt.getInteger("height") << 4, 16),
                            nbt.getInteger("offsetX") << 4,
                            nbt.getInteger("offsetY") << 4);

                    //enum already exists with the name
                    if(art == null) throw new IllegalArgumentException(
                            "A critical error has occurred while creating painting with the name: " + name);

                    //assign texture
                    IJSONPainting.from(art).setTexture(
                            nbt.hasKey("front", Constants.NBT.TAG_STRING)
                                    ? toLocation(nbt.getString("front"))
                                    : new ResourceLocation(modName, String.format("%s/%s.png", defaultTexture, name)));

                    //assign back texture
                    IJSONPainting.from(art).setBackTexture(
                            nbt.hasKey("back", Constants.NBT.TAG_STRING)
                                    ? toLocation(nbt.getString("back"))
                                    : DEFAULT_BACK_TEXTURE);

                    IJSONPainting.from(art).setBackOffsetX(nbt.getInteger("backOffsetX") << 4);
                    IJSONPainting.from(art).setBackOffsetY(nbt.getInteger("backOffsetY") << 4);
                    IJSONPainting.from(art).setUseSpecialRenderer(true);
                    return art;
                }

                return null;
            }
            //likely a bad json
            catch (NBTException e) { throw new JsonParseException(e); }
        }

        @Nonnull
        ResourceLocation toLocation(@Nonnull String texture) {
            if(texture.indexOf(':') != -1) return new ResourceLocation(texture);
            else return new ResourceLocation(modName, texture);
        }
    }
}

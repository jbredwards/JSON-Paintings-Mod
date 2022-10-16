package git.jbredwards.jsonpaintings.common;

import com.google.gson.*;
import git.jbredwards.jsonpaintings.Constants;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.io.IOUtils;

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
    public static final ResourceLocation DEFAULT_BACK_TEXTURE = new ResourceLocation(Constants.MODID, "textures/paintings/back.png");
    static int paintingsCreated = 0;

    @Nonnull
    static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(EntityPainting.EnumArt.class, ArtDeserializer.INSTANCE)
            .create();

    //reads the minecraft run folder
    public static void readInstance() throws IOException {
        final File file = new File("paintings", "paintings.json");
        if(file.exists()) {
            ArtDeserializer.INSTANCE.modName = Constants.MODID;
            ArtDeserializer.INSTANCE.isModded = false;
            GSON.fromJson(IOUtils.toString(new FileInputStream(file), Charset.defaultCharset()), EntityPainting.EnumArt[].class);
        }
    }

    //reads each mod
    public static void readMods() throws IOException {
        for(String modId : Loader.instance().getIndexedModList().keySet()) {
            final @Nullable InputStream file = Loader.class.getResourceAsStream(String.format("/assets/%s/paintings/paintings.json", modId));
            if(file != null) {
                ArtDeserializer.INSTANCE.modName = modId;
                ArtDeserializer.INSTANCE.isModded = true;
                GSON.fromJson(IOUtils.toString(file, Charset.defaultCharset()), EntityPainting.EnumArt[].class);
            }
        }
    }

    enum ArtDeserializer implements JsonDeserializer<EntityPainting.EnumArt>
    {
        INSTANCE;

        String modName;
        boolean isModded;

        @Nullable
        @Override
        public EntityPainting.EnumArt deserialize(@Nonnull JsonElement json, @Nonnull Type typeOfT, @Nonnull JsonDeserializationContext context) throws JsonParseException {
            try {
                final NBTTagCompound nbt = JsonToNBT.getTagFromJson(json.toString());
                final String motive = nbt.getString("motive");
                if(!motive.isEmpty()) {
                    //look for existing painting to override
                    @Nullable EntityPainting.EnumArt art = null;
                    for(EntityPainting.EnumArt artIn : EntityPainting.EnumArt.values()) {
                        if(artIn.title.equals(motive)) {
                            art = artIn;
                            art.offsetX = 0;
                            art.offsetY = 0;
                            break;
                        }
                    }

                    //create new painting if it's not an override
                    if(art == null) art = EnumHelper.addArt(
                            "JSON_PAINTINGS_GENERATED_ID" + paintingsCreated++, motive,
                            Math.max(nbt.getInteger("width") << 4, 16),
                            Math.max(nbt.getInteger("height") << 4, 16), 0, 0);

                    //should never pass, but exists cause EnumHelper method is nullable
                    if(art == null) {
                        paintingsCreated--;
                        throw new IllegalArgumentException(
                                "A critical error has occurred while creating painting with the name: " + motive);
                    }

                    //assign texture
                    final NBTTagCompound textures = nbt.getCompoundTag("textures");
                    final IJSONPainting painting = IJSONPainting.from(art);
                    painting.setFrontTexture(textures.hasKey("front", NBT.TAG_STRING)
                            ? buildLocation(textures.getString("front"))
                            : buildLocation(modName + ":" + (isModded
                                    ? "paintings/" + motive.toLowerCase()
                                    : motive.toLowerCase())));

                    //assign back texture
                    painting.setBackTexture(textures.hasKey("back", NBT.TAG_STRING)
                            ? buildLocation(textures.getString("back"))
                            : DEFAULT_BACK_TEXTURE);

                    //assign side texture
                    painting.setSideTexture(textures.hasKey("side", NBT.TAG_STRING)
                            ? buildLocation(textures.getString("side"))
                            : painting.getBackTexture());

                    //fix server issue with painting name sizes
                    if(art.title.length() > EntityPainting.EnumArt.MAX_NAME_LENGTH)
                        EntityPainting.EnumArt.MAX_NAME_LENGTH = art.title.length();

                    painting.setUseSpecialRenderer(true);
                    return art;
                }

                else System.out.println("JSON Paintings: A painting has been skipped! Missing name!");
                return null;
            }
            //likely a bad json
            catch (NBTException e) { throw new JsonParseException(e); }
        }

        @Nonnull
        static ResourceLocation buildLocation(@Nonnull String str) {
            final ResourceLocation loc = new ResourceLocation(str);
            return new ResourceLocation(loc.getNamespace(), "textures/" + loc.getPath() + ".png");
        }
    }
}

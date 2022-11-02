package git.jbredwards.jsonpaintings.common.util;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import git.jbredwards.jsonpaintings.Constants;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;

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
    public static void readMods() {
        for(String modId : Loader.instance().getIndexedModList().keySet()) {
            final @Nullable InputStream file = Loader.class.getResourceAsStream(String.format("/assets/%s/paintings/paintings.json", modId));
            if(file != null) {
                try {
                    ArtDeserializer.INSTANCE.modName = modId;
                    ArtDeserializer.INSTANCE.isModded = true;
                    GSON.fromJson(IOUtils.toString(file, Charset.defaultCharset()), EntityPainting.EnumArt[].class);
                }
                //catch here as to not skip the other paintings
                catch(IOException | RuntimeException e) { e.printStackTrace(); }
            }
        }
    }

    enum ArtDeserializer implements JsonDeserializer<EntityPainting.EnumArt>
    {
        INSTANCE;

        String modName;
        boolean isModded;

        @Nonnull
        @Override
        public EntityPainting.EnumArt deserialize(@Nonnull JsonElement jsonIn, @Nonnull Type typeOfT, @Nonnull JsonDeserializationContext context) throws JsonParseException {
            final JsonObject json = jsonIn.getAsJsonObject();
            Validate.isTrue(json.has("motive"), "Cannot create painting without motive!");

            final String motive = json.get("motive").getAsString();
            //look for existing painting to override
            @Nullable EntityPainting.EnumArt art = null;
            for(EntityPainting.EnumArt artIn : EntityPainting.EnumArt.values()) {
                if(artIn.title.equals(motive)) {
                    art = artIn;
                    art.offsetX = 0;
                    art.offsetY = 0;
                    //override width, this is not recommended
                    if(json.has("width")) art.sizeX = Math.max(json.get("width").getAsInt() << 4, 16);
                    //override height, this is not recommended
                    if(json.has("height")) art.sizeY = Math.max(json.get("height").getAsInt() << 4, 16);
                    //reset misc json painting properties
                    IJSONPainting.from(art).setCreative(false);
                    IJSONPainting.from(art).setHasBackTexture(false);
                    IJSONPainting.from(art).setHasSideTexture(false);

                    break;
                }
            }

            //create new painting if it's not an override
            if(art == null) art = EnumHelper.addArt(
                    "JSON_PAINTINGS_GENERATED_ID" + paintingsCreated++, motive,
                    json.has("width") ? Math.max(json.get("width").getAsInt() << 4, 16) : 16,
                    json.has("height") ? Math.max(json.get("height").getAsInt() << 4, 16) : 16, 0, 0);

            //should never pass, but exists cause EnumHelper method is nullable
            if(art == null) {
                paintingsCreated--;
                throw new IllegalArgumentException(
                        "A critical error has occurred while creating painting with the motive: " + motive);
            }

            //assign textures
            final IJSONPainting painting = IJSONPainting.from(art);
            if(json.has("textures")) {
                final JsonObject textures = json.getAsJsonObject("textures");
                final String front = textures.has("front")
                        ? textures.get("front").getAsString()
                        : modName + ":" + (isModded
                                ? "paintings/" + motive.toLowerCase()
                                : motive.toLowerCase());

                final String back;
                if(!textures.has("back")) back = DEFAULT_BACK_TEXTURE.toString();
                else {
                    back = textures.get("back").getAsString();
                    painting.setHasBackTexture(true);
                    painting.setHasSideTexture(true);
                }

                final String side;
                if(!textures.has("side")) side = back;
                else {
                    side = textures.get("side").getAsString();
                    painting.setHasSideTexture(true);
                }

                //allow for texture locations to reference other textures (example -> "back": "#front")
                final ImmutableMap<String, String> textureMap = ImmutableMap.of("front", front, "back", back, "side", side);
                painting.setFrontTexture(buildLocation(front.charAt(0) == '#' ? textureMap.get(front.substring(1)) : front));
                painting.setBackTexture(buildLocation(back.charAt(0) == '#' ? textureMap.get(back.substring(1)) : back));
                painting.setSideTexture(buildLocation(side.charAt(0) == '#' ? textureMap.get(side.substring(1)) : side));
            }

            //assign default textures
            else {
                painting.setFrontTexture(buildLocation(modName + ":" + (isModded
                        ? "paintings/" + motive.toLowerCase()
                        : motive.toLowerCase())));
                painting.setBackTexture(DEFAULT_BACK_TEXTURE);
                painting.setSideTexture(DEFAULT_BACK_TEXTURE);
            }

            //fix server issue with painting title sizes
            if(motive.length() > EntityPainting.EnumArt.MAX_NAME_LENGTH)
                EntityPainting.EnumArt.MAX_NAME_LENGTH = motive.length();

            if(json.has("isCreative")) painting.setCreative(json.get("isCreative").getAsBoolean());
            painting.setUseSpecialRenderer(true);
            return art;
        }

        @Nonnull
        static ResourceLocation buildLocation(@Nonnull String str) {
            final ResourceLocation loc = new ResourceLocation(str);
            return new ResourceLocation(loc.getNamespace(), "textures/" + loc.getPath() + ".png");
        }
    }
}

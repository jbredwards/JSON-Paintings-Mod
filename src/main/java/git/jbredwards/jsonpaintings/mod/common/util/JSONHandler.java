/*
 * Copyright (c) 2024. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.common.util;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import git.jbredwards.jsonpaintings.mod.JSONPaintings;
import net.minecraft.command.CommandException;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 *
 * @author jbred
 *
 */
public final class JSONHandler
{
    @Nonnull public static final Map<EntityPainting.EnumArt, String> MODID_LOOKUP = new HashMap<>();
    @Nonnull public static final Map<String, EntityPainting.EnumArt> PAINTING_REMAPS = new HashMap<>();
    static int nextPaintingId = 0;

    @Nonnull public static final ResourceLocation DEFAULT_BACK_TEXTURE = new ResourceLocation(JSONPaintings.MODID, "textures/paintings/back.png");
    @Nonnull static final Set<String> newMotives = new HashSet<>();

    // reads each mod
    public static void readMods() {
        for(@Nonnull final ModContainer container : Loader.instance().getModList()) {
            @Nullable final InputStream file = Loader.class.getResourceAsStream(String.format("/assets/%s/paintings/paintings.json", container.getModId()));
            if(file != null) {
                try { read(new InputStreamReader(file), container, true, false); }
                //catch here as to not skip other mods' paintings
                catch(@Nonnull final Exception e) { e.printStackTrace(); }
            }
        }
    }

    // reads the minecraft run folder
    public static void readInstance(final boolean isReload) throws Exception {
        final File file = new File("paintings", "paintings.json");
        if(file.exists()) read(new FileReader(file), Loader.instance().getIndexedModList().get(JSONPaintings.MODID), false, isReload);
    }

    static void read(@Nonnull final Reader reader, @Nonnull final ModContainer container, final boolean isModded, final boolean isReload) throws Exception {
        @Nonnull final JsonArray jsonArray = new JsonParser().parse(reader).getAsJsonArray();
        // check that all old paintings are present in the file, as removing paintings is impossible
        if(isReload && !newMotives.isEmpty() && !StreamSupport.stream(jsonArray.spliterator(), false)
                .map(JsonElement::getAsJsonObject)
                .map(json -> JsonUtils.getString(json, "motive"))
                .collect(Collectors.toSet()).containsAll(newMotives))
            throw new CommandException("jsonpaintings.command.removedPainting");

        // read paintings from file
        jsonArray.forEach(jsonIn -> {
            @Nonnull final JsonObject json = jsonIn.getAsJsonObject();
            @Nonnull final String motive = JsonUtils.getString(json, "motive");
            boolean setModId = true;

            // look for existing painting to override
            @Nullable EntityPainting.EnumArt art = null;
            for(@Nonnull final EntityPainting.EnumArt artIn : EntityPainting.EnumArt.values()) {
                if(artIn.title.equals(motive)) {
                    art = artIn;
                    art.offsetX = 0;
                    art.offsetY = 0;
                    setModId = false;
                    // override width, this is not recommended
                    if(json.has("width")) art.sizeX = Math.max(JsonUtils.getInt(json.get("width"), "width") << 4, 16);
                    // override height, this is not recommended
                    if(json.has("height")) art.sizeY = Math.max(JsonUtils.getInt(json.get("height"), "height") << 4, 16);
                    // reset misc json painting properties
                    IJSONPainting.from(art).setCreative(false);
                    IJSONPainting.from(art).setHasBackTexture(false);
                    IJSONPainting.from(art).setHasSideTexture(false);
                    break;
                }
            }

            // create new painting if it's not an override
            if(art == null) {
                if((art = EnumHelper.addArt(
                        "JSON_PAINTINGS_GENERATED_ID" + nextPaintingId++, motive,
                        json.has("width") ? Math.max(JsonUtils.getInt(json.get("width"), "width") << 4, 16) : 16,
                        json.has("height") ? Math.max(JsonUtils.getInt(json.get("height"), "height") << 4, 16) : 16, 0, 0)) != null)
                    newMotives.add(motive);

                // should never pass, but exists because EnumHelper is nullable
                else {
                    nextPaintingId--;
                    throw new IllegalArgumentException(
                            "A critical error has occurred while creating painting with the motive: " + motive);
                }
            }

            // assign textures
            @Nonnull final IJSONPainting painting = IJSONPainting.from(art);
            @Nonnull final String frontTexturePath = isModded ? "paintings/" + motive.toLowerCase() : motive.toLowerCase();
            if(json.has("textures")) {
                @Nonnull final JsonObject textures = JsonUtils.getJsonObject(json.get("textures"), "textures");
                @Nonnull final String front = textures.has("front")
                        ? JsonUtils.getString(textures.get("front"), "front")
                        : container.getModId() + ":" + frontTexturePath;

                @Nonnull final String back;
                if(!textures.has("back")) back = JSONPaintings.MODID + ":paintings/back";
                else {
                    back = JsonUtils.getString(textures.get("back"), "back");
                    painting.setHasBackTexture(true);
                    painting.setHasSideTexture(true);
                }

                @Nonnull final String side;
                if(!textures.has("side")) side = back;
                else {
                    side = JsonUtils.getString(textures.get("side"), "side");
                    painting.setHasSideTexture(true);
                }

                // allow for texture locations to reference other textures (example -> "back": "#front")
                @Nonnull final ImmutableMap<String, String> textureMap = ImmutableMap.of("front", front, "back", back, "side", side);
                painting.setFrontTexture(buildLocation(front.charAt(0) == '#' ? textureMap.get(front.substring(1)) : front));
                painting.setBackTexture(buildLocation(back.charAt(0) == '#' ? textureMap.get(back.substring(1)) : back));
                painting.setSideTexture(buildLocation(side.charAt(0) == '#' ? textureMap.get(side.substring(1)) : side));
            }

            // assign default textures
            else {
                painting.setFrontTexture(buildLocation(container.getModId() + ":" + frontTexturePath));
                painting.setBackTexture(DEFAULT_BACK_TEXTURE);
                painting.setSideTexture(DEFAULT_BACK_TEXTURE);
            }

            // fix server issue with painting title sizes
            if(motive.length() > EntityPainting.EnumArt.MAX_NAME_LENGTH)
                EntityPainting.EnumArt.MAX_NAME_LENGTH = motive.length();

            // exclusive paintings
            if(json.has("isCreative")) {
                painting.setCreative(JsonUtils.getBoolean(json.get("isCreative"), "isCreative"));
                if(painting.isCreative()) painting.setAlwaysCapture(true);
            }

            // hardcoded paintings
            else painting.setAlwaysCapture(false);
            if(json.has("alwaysCapture")) painting.setAlwaysCapture(JsonUtils.getBoolean(json.get("alwaysCapture"), "alwaysCapture"));

            // mod id (or JSON Paintings if added via main config file)
            if(setModId) {
                painting.setModName(container.getName());
                MODID_LOOKUP.put(art, container.getModId());
            }

            // painting rarity
            if(json.has("rarity")) {
                @Nonnull final JsonElement rarity = json.get("rarity");

                // built-in rarity value
                if(rarity.isJsonPrimitive()) Arrays.stream(EnumRarity.values())
                        .filter(enumRarity -> rarity.getAsString().equalsIgnoreCase(enumRarity.getName()))
                        .findFirst()
                        .ifPresent(painting::setRarity);

                // custom rarity value
                else painting.setRarity(new IRarity() {
                    @Nonnull final String name = JsonUtils.getString(JsonUtils.getJsonObject(rarity, "rarity"), "name");
                    @Nonnull public String getName() { return name; }

                    @Nonnull final TextFormatting color = getFormatColor(rarity.getAsJsonObject());
                    @Nonnull public TextFormatting getColor() { return color; }
                });
            }

            // painting mapping
            if(json.has("mapping")) {
                @Nonnull final JsonElement mappingJson = json.get("mapping");
                if(mappingJson.isJsonPrimitive()) PAINTING_REMAPS.put(mappingJson.getAsString(), art);
                else {
                    @Nonnull final EntityPainting.EnumArt finalArt = art;
                    JsonUtils.getJsonArray(mappingJson, "mapping").forEach(mapping -> PAINTING_REMAPS.put(mapping.getAsString(), finalArt));
                }
            }

            // special mod name (i.e. the name of the modpack adding the painting)
            // instead of this functionality being added, try adding your paintings via Resource Mod Loader: https://www.curseforge.com/minecraft/mc-mods/resource-mod-loader
            // if(json.has("modName")) painting.setModName(JsonUtils.getString(json.get("modName"), "modName"));

            painting.setUseSpecialRenderer(true);
        });
    }

    @Nonnull
    static ResourceLocation buildLocation(@Nonnull String str) {
        final ResourceLocation loc = new ResourceLocation(str);
        return new ResourceLocation(loc.getNamespace(), "textures/" + loc.getPath() + ".png");
    }

    @Nonnull
    static TextFormatting getFormatColor(@Nonnull final JsonObject json) {
        @Nonnull final String colorKey = JsonUtils.getString(json, "color");
        return Optional.ofNullable(TextFormatting.getValueByName(colorKey)).orElseGet(() -> Arrays.stream(TextFormatting.values()).filter(format -> format.toString().equals(colorKey)).findFirst()
                .orElseThrow(() -> new NullPointerException("Unknown color: \"" + colorKey + "\", see the following page for a list of all valid colors: https://minecraft.wiki/w/Formatting_codes#Color_codes")));
    }
}

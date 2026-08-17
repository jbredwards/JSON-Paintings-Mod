/*
 * Copyright (c) 2025. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.common.util;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import git.jbredwards.jsonpaintings.api.BasicPaintingInfo;
import git.jbredwards.jsonpaintings.api.PaintingInfo;
import git.jbredwards.jsonpaintings.mod.JSONPaintings;
import git.jbredwards.jsonpaintings.mod.asm.ASMHandler;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author jbred
 *
 */
public final class JSONHandler
{
    @Nonnull public static final ResourceLocation DEFAULT_BACK_TEXTURE = new ResourceLocation(JSONPaintings.MODID, "textures/paintings/back.png");
    @Nonnull public static final Map<String, EntityPainting.EnumArt> PAINTING_REMAPS = new HashMap<>();

    @Nullable private static String activeMotive;
    @Nullable private static ModContainer activeMod;

    @Nonnull public static final List<JsonPaintingInfo> FROM_MOD_JSON = new ArrayList<>(), FROM_USER_JSON = new ArrayList<>();
    @Nonnull public static final JsonDeserializer<JsonPaintingInfo> DESERIALIZER = (jsonIn, type, ctx) -> {
        @Nonnull final JsonObject json = jsonIn.getAsJsonObject();
        @Nonnull final PaintingInfo info = new BasicPaintingInfo();

        info.modId = activeMod != null ? activeMod.getModId() : JSONPaintings.MODID;
        info.modName = activeMod != null ? activeMod.getName() : JSONPaintings.NAME;

        @Nonnull final String motive = activeMotive != null ? activeMotive : JsonUtils.getString(json, "motive");
        @Nonnull final String frontTexturePath = frontTexturePath(info, motive);

        if(json.has("textures")) {
            @Nonnull final JsonObject textures = JsonUtils.getJsonObject(json.get("textures"), "textures");
            @Nonnull final String front = JsonUtils.getString(textures, "front", frontTexturePath);
            @Nonnull final String back = JsonUtils.getString(textures, "back", JSONPaintings.MODID + ":paintings/back");
            @Nonnull final String side = JsonUtils.getString(textures, "side", back);

            // Allow for texture locations to reference other textures. (example -> "back": "#front")
            @Nonnull final ImmutableMap<String, String> textureMap = ImmutableMap.of("front", front, "back", back, "side", side);
            info.frontTexture = buildLocation(front.charAt(0) == '#' ? textureMap.get(front.substring(1)) : front);
            info.backTexture = buildLocation(back.charAt(0) == '#' ? textureMap.get(back.substring(1)) : back);
            info.sideTexture = buildLocation(side.charAt(0) == '#' ? textureMap.get(side.substring(1)) : side);
        }

        // Front texture definition for modern paintings.
        else if(json.has("asset_id")) info.frontTexture = buildLocation(JsonUtils.getString(json.get("asset_id"), "asset_id"), "textures/painting");

        // Sets the painting dimensions.
        // If not present, we assume this PaintingInfo will be used to override an existing painting.
        if(json.has("width")) info.setWidth(Math.max(16, JsonUtils.getInt(json.get("width"), "width") << 4));
        if(json.has("height")) info.setHeight(Math.max(16, JsonUtils.getInt(json.get("height"), "height") << 4));

        // Miscellaneous client-side stuff.
        if(json.has("author")) info.author = ctx.deserialize(json.get("author"), ITextComponent.class);
        if(json.has("title")) info.title = ctx.deserialize(json.get("title"), ITextComponent.class);
        if(json.has("item_model")) {
            @Nonnull final String itemModel = JsonUtils.getString(json.get("item_model"), "item_model");
            info.itemModel = itemModel.indexOf('#') == -1 ? new ModelResourceLocation(itemModel, "inventory") : new ModelResourceLocation(itemModel);
        }

        // Exclusive paintings.
        @Nullable final Boolean isTreasure;
        if(json.has("isCreative")) isTreasure = JsonUtils.getBoolean(json.get("isCreative"), "isCreative");
        else if(json.has("is_creative")) isTreasure = JsonUtils.getBoolean(json.get("is_creative"), "is_creative");
        else if(json.has("is_treasure")) isTreasure = JsonUtils.getBoolean(json.get("is_treasure"), "is_treasure");
        else if(activeMotive != null) isTreasure = true; // 1.21 controls this though a "placeable.json" file.
        else isTreasure = null;

        // Hardcoded paintings.
        @Nullable final Boolean alwaysCapture;
        if(json.has("alwaysCapture")) alwaysCapture = JsonUtils.getBoolean(json.get("alwaysCapture"), "alwaysCapture");
        else if(json.has("always_capture")) alwaysCapture = JsonUtils.getBoolean(json.get("always_capture"), "always_capture");
        else alwaysCapture = null;

        // Allow removed/renamed paintings to be remapped to this one.
        @Nullable final String[] remapping;
        if(!json.has("mapping")) remapping = null;
        else {
            @Nonnull final JsonElement mapping = json.get("mapping");
            if(mapping.isJsonPrimitive()) remapping = new String[] {mapping.getAsString()};
            else {
                remapping = new String[JsonUtils.getJsonArray(mapping, "mapping").size()];
                for(int i = 0; i < remapping.length; i++) remapping[i] = JsonUtils.getString(mapping.getAsJsonArray().get(i), "mapping["+i+']');
            }
        }

        return new JsonPaintingInfo(motive, info, remapping, json.get("rarity"), isTreasure, alwaysCapture);
    };

    @Nonnull
    private static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer())
            .registerTypeHierarchyAdapter(Style.class, new Style.Serializer())
            .registerTypeAdapterFactory(new EnumTypeAdapterFactory())
            .registerTypeAdapter(JsonPaintingInfo.class, DESERIALIZER)
            .create();

    static int nextPaintingId = 0;
    static void error(@Nonnull final Object... args) {
        JSONPaintings.LOGGER.error("An error occurred while reading %s in %s with id %s. Skipping file...", args);
    }

    public static void construct() {
        // Effectively remove the character limit for motives.
        EntityPainting.EnumArt.MAX_NAME_LENGTH = Short.MAX_VALUE;
        // Read painting infos.
        readMods();
        readInstance(false);
        // Turn painting infos into enums.
        FROM_MOD_JSON.forEach(JsonPaintingInfo::construct);
        FROM_USER_JSON.forEach(JsonPaintingInfo::construct);
    }

    public static void postInit() {
        // Apply rarities.
        // This is done later so that all new rarity enums hopefully exist.
        FROM_MOD_JSON.forEach(JsonPaintingInfo::postInit);
        FROM_USER_JSON.forEach(JsonPaintingInfo::postInit);
    }

    // Reads each mod.
    public static void readMods() { readMods(Loader.instance().getModList(), FROM_MOD_JSON, "mod"); }
    public static void readMods(@Nonnull final List<ModContainer> containers, @Nonnull final List<JsonPaintingInfo> infos, @Nonnull final String type) {
        for(@Nonnull final ModContainer container : containers) {
            // Thank you CraftingHelper!
            CraftingHelper.findFiles(container, "assets/" + container.getModId() + "/paintings/paintings.json", path -> {
                try(@Nonnull final Reader reader = Files.newBufferedReader(path)) {
                    activeMod = container;
                    infos.addAll(Arrays.asList(GSON.fromJson(reader, JsonPaintingInfo[].class)));
                    return true;
                }
                catch(@Nonnull final IOException | JsonParseException e) {
                    error(path, type, container.getModId(), e);
                    return false;
                }
                finally {
                    activeMod = null;
                }
            }, null, false, false);
            // Support 1.21 painting format.
            @Nonnull final Map<String, JsonPaintingInfo> datapack = new HashMap<>();
            CraftingHelper.findFiles(container, "data/" + container.getModId() + "/painting_variant", null, (root, file) -> {
                if(Files.isDirectory(file)) return false;
                try(@Nonnull final Reader reader = Files.newBufferedReader(file)) {
                    activeMod = container;
                    activeMotive = container.getModId() + ':' + FilenameUtils.removeExtension(file.getFileName().toString());
                    @Nonnull final JsonPaintingInfo info = GSON.fromJson(reader, JsonPaintingInfo.class);
                    datapack.put(activeMotive, info);
                    infos.add(info);
                    return true;
                }
                catch(@Nonnull final IOException | JsonParseException e) {
                    error(file, type, container.getModId(), e);
                    return false;
                }
                finally {
                    activeMod = null;
                    activeMotive = null;
                }
            }, true, true);
            // 1.21 uses a tag equivalent of an inverse "is_treasure".
            CraftingHelper.findFiles(container, "data/minecraft/tags/painting_variant/placeable.json", path -> {
                try(@Nonnull final Reader reader = Files.newBufferedReader(path)) {
                    @Nonnull final JsonArray values = JsonUtils.getJsonArray(new JsonParser().parse(reader).getAsJsonObject(), "values");
                    for(int i = 0; i < values.size(); i++) {
                        @Nullable final JsonPaintingInfo info = datapack.get(JsonUtils.getString(values.get(i), "values["+i+']'));
                        if(info != null) info.isTreasure = null;
                    }

                    return true;
                }
                catch(@Nonnull final IOException | JsonParseException e) {
                    error(path, type, container.getModId(), e);
                    return false;
                }
            }, null, false, false);
        }
    }

    // Reads each pack.
    public static void readPacks() {
        @Nonnull final List<ModContainer> dummyContainers = new ArrayList<>();
        try {
            @Nullable final File[] packs = ASMHandler.paintingsLocation.resolve("packs").toFile().listFiles();
            if(packs != null) for(@Nonnull final File pack : packs) {
                for(@Nonnull final String domain : gatherPackDomains(pack)) {
                    @Nonnull final String modId = domain.substring(0, domain.length() - 1);
                    // Using dummy mod containers, so I can re-use the `readMods()` logic.
                    dummyContainers.add(new DummyModContainer() {
                        @Nonnull
                        @Override
                        public String getModId() {
                            return modId;
                        }

                        @Nonnull
                        @Override
                        public String getName() {
                            return JSONPaintings.NAME;
                        }

                        @Nonnull
                        @Override
                        public File getSource() {
                            return pack;
                        }
                    });
                }
            }
        }

        catch(@Nonnull final IOException e) { JSONPaintings.LOGGER.error(e); }
        readMods(dummyContainers, FROM_USER_JSON, "pack");
    }

    // Reads the minecraft run folder.
    public static void readInstance(final boolean isReload) {
        FROM_USER_JSON.clear();
        readPacks();

        @Nonnull final Path cfg = ASMHandler.paintingsLocation.resolve("paintings.json");
        if(Files.exists(cfg)) {
            try(@Nonnull final Reader reader = Files.newBufferedReader(cfg)) {
                FROM_USER_JSON.addAll(Arrays.asList(GSON.fromJson(reader, JsonPaintingInfo[].class)));
            }
            catch(@Nonnull final IOException | JsonParseException e) {
                JSONPaintings.LOGGER.error(e);
            }
        }

        if(isReload) FROM_USER_JSON.forEach(Runnable::run);
    }

    @Nonnull
    static ResourceLocation buildLocation(@Nonnull final String str) {
        return buildLocation(str, "textures");
    }

    @Nonnull
    static ResourceLocation buildLocation(@Nonnull final String str, @Nonnull final String parent) {
        final ResourceLocation loc = new ResourceLocation(str);
        return new ResourceLocation(loc.getNamespace(), parent + '/' + loc.getPath() + ".png");
    }

    @Nonnull
    static String frontTexturePath(@Nonnull final PaintingInfo info, @Nonnull final String motive) {
        return info.modId + ':' + (!JSONPaintings.MODID.equals(info.modId) ? "paintings/" + motive.toLowerCase() : motive.toLowerCase());
    }

    @Nonnull
    static TextFormatting getFormatColor(@Nonnull final JsonObject json) {
        @Nonnull final String colorKey = JsonUtils.getString(json, "color");
        return Optional.ofNullable(TextFormatting.getValueByName(colorKey)).orElseGet(() -> Arrays.stream(TextFormatting.values()).filter(format -> format.toString().equals(colorKey)).findFirst()
                .orElseThrow(() -> new JsonParseException("Unknown color: \"" + colorKey + "\", see the following page for a list of all valid colors: https://minecraft.wiki/w/Formatting_codes#Color_codes")));
    }

    @Nonnull
    private static List<String> gatherPackDomains(@Nonnull final File pack) throws IOException {
        @Nonnull final List<String> domains = new ArrayList<>();
        if(pack.isDirectory()) {
            @Nullable final File[] domainFiles = new File(pack, "data").listFiles();
            if(domainFiles != null) for(@Nonnull final File domain : domainFiles) if(domain.isDirectory()) domains.add(domain.getName());
        }
        else try(@Nonnull final ZipFile packZip = new ZipFile(pack)) {
            for(@Nonnull final Enumeration<? extends ZipEntry> it = packZip.entries(); it.hasMoreElements();) {
                @Nonnull final ZipEntry entry = it.nextElement();
                if(entry.isDirectory() && entry.getName().length() > 5 && entry.getName().startsWith("data/")) {
                    @Nonnull final String domain = entry.getName().substring(5);
                    if(domain.indexOf('/') == domain.length() - 1) domains.add(domain);
                }
            }
        }
        return domains;
    }
}

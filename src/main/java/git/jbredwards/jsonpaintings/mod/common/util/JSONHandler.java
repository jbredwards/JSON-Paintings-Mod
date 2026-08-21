/*
 * Copyright (c) 2025. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.common.util;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import git.jbredwards.jsonpaintings.api.ActivePaintingInfo;
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
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author jbred
 *
 */
@ApiStatus.Internal
public final class JSONHandler
{
    @Nonnull public static final ResourceLocation DEFAULT_BACK_TEXTURE = new ResourceLocation(JSONPaintings.MODID, "textures/paintings/back.png");
    @Nonnull public static final Map<String, EntityPainting.EnumArt> PAINTING_REMAPS = new HashMap<>();
    @Nonnull public static EntityPainting.EnumArt[] NON_TREASURE_PAINTINGS = new EntityPainting.EnumArt[0];

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
            @Nonnull final ImmutableMap<String, String> textureMap = ImmutableMap.of("#front", front, "#back", back, "#side", side);
            info.frontTexture = buildLocation(front.charAt(0) == '#' ? textureMap.get(front) : front);
            info.backTexture = buildLocation(back.charAt(0) == '#' ? textureMap.get(back) : back);
            info.sideTexture = buildLocation(side.charAt(0) == '#' ? textureMap.get(side) : side);
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
            @Nonnull String itemModel = JsonUtils.getString(json.get("item_model"), "item_model");
            if(itemModel.indexOf(':') == -1) itemModel = info.modId + ':' + itemModel;
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
            .registerTypeHierarchyAdapter(Style.class, new Style.Serializer() {
                @Nullable
                @Override
                public Style deserialize(@Nonnull final JsonElement json, @Nonnull final Type type, @Nonnull final JsonDeserializationContext ctx) throws JsonParseException {
                    try { return super.deserialize(json, type, ctx); }
                    catch(@Nonnull final Exception e) {
                        JSONPaintings.LOGGER.error(e);
                        return null;
                    }
                }
            })
            .registerTypeAdapterFactory(new EnumTypeAdapterFactory())
            .registerTypeAdapter(JsonPaintingInfo.class, DESERIALIZER)
            .create();

    static int nextPaintingId = 0;
    static void error(@Nonnull final Object... args) {
        JSONPaintings.LOGGER.error("An error occurred while reading %s in %s with id %s. Skipping file...", args);
    }

    public static void construct() {
        createFolders();
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

    public static void loadComplete() {
        // Cache survival-only paintings.
        NON_TREASURE_PAINTINGS = Arrays.stream(EntityPainting.EnumArt.values()).filter(art -> !ActivePaintingInfo.get(art).isTreasure).toArray(EntityPainting.EnumArt[]::new);
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
                @Nonnull final String modName = gatherPackName(pack);
                for(@Nonnull final String domain : gatherPackDomains(pack)) {
                    // Using dummy mod containers, so I can re-use the `readMods()` logic.
                    dummyContainers.add(new DummyModContainer() {
                        @Nonnull
                        @Override
                        public String getModId() {
                            return domain;
                        }

                        @Nonnull
                        @Override
                        public String getName() {
                            return modName;
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

        if(isReload) {
            FROM_USER_JSON.forEach(Runnable::run);
            loadComplete();
        }
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
        @Nonnull final ResourceLocation loc = new ResourceLocation(motive);
        return info.modId + ':' + (!JSONPaintings.MODID.equals(info.modId) ? "paintings/" + loc.getPath() : loc.getPath());
    }

    @Nonnull
    static TextFormatting getFormatColor(@Nonnull final JsonObject json) {
        @Nonnull final String colorKey = JsonUtils.getString(json, "color");
        return Optional.ofNullable(TextFormatting.getValueByName(colorKey)).orElseGet(() -> Arrays.stream(TextFormatting.values()).filter(format -> format.toString().equals(colorKey)).findFirst()
                .orElseThrow(() -> new JsonParseException("Unknown color: \"" + colorKey + "\", see the following page for a list of all valid colors: https://minecraft.wiki/w/Formatting_codes#Color_codes")));
    }

    @Nonnull
    private static Collection<String> gatherPackDomains(@Nonnull final File pack) throws IOException {
        @Nonnull final Set<String> domains = new HashSet<>();
        if(pack.isDirectory()) {
            @Nullable final File[] domainFiles = new File(pack, "data").listFiles();
            if(domainFiles != null) for(@Nonnull final File domain : domainFiles) if(domain.isDirectory()) domains.add(domain.getName());
        }
        else try(@Nonnull final ZipFile packZip = new ZipFile(pack)) {
            for(@Nonnull final Enumeration<? extends ZipEntry> it = packZip.entries(); it.hasMoreElements();) {
                @Nonnull final ZipEntry entry = it.nextElement();
                if(entry.getName().length() > 5 && entry.getName().startsWith("data/")) {
                    @Nonnull final String domain = entry.getName().substring(5);
                    final int idx = domain.indexOf('/');
                    if(idx != -1) domains.add(domain.substring(0, idx));
                }
            }
        }
        return domains;
    }

    @Nonnull
    private static String gatherPackName(@Nonnull final File pack) throws IOException {
        if(pack.isDirectory()) {
            @Nonnull final String name = pack.getName();
            return name.substring(0, name.length() - 1);
        }
        // Try getting the name from mod file.
        else try(@Nonnull final ZipFile packZip = new ZipFile(pack)) {
            // Fabric mod.
            @Nullable ZipEntry modInfo = packZip.getEntry("fabric.mod.json");
            if(modInfo != null) try(@Nonnull final Reader reader = new InputStreamReader(packZip.getInputStream(modInfo))) {
                @Nonnull final JsonObject json = new JsonParser().parse(reader).getAsJsonObject();
                try { return JsonUtils.getString(json, "name"); }
                catch(@Nonnull final JsonParseException ignored) {}
            }
            // Forge mod.
            else {
                modInfo = packZip.getEntry("META-INF/neoforge.mods.toml");
                if(modInfo == null) modInfo = packZip.getEntry("META-INF/mods.toml");
                if(modInfo != null) try(@Nonnull final InputStream is = packZip.getInputStream(modInfo)) {
                    for(@Nonnull final String line : IOUtils.readLines(is, StandardCharsets.UTF_8)) {
                        final int begin = line.indexOf('=');
                        if(begin != -1 && line.trim().startsWith("displayName")) {
                            @Nonnull final JsonElement json = new JsonParser().parse(line.substring(begin + 1));
                            try { return JsonUtils.getString(json, "displayName"); }
                            catch(@Nonnull final JsonParseException ignored) { break; }
                        }
                    }
                }
            }
            // Unknown mod type (probably a datapack). Use file name.
            return FilenameUtils.removeExtension(pack.getName());
        }
    }

    private static void createFolders() {
        if(ASMHandler.paintingsLocation.toFile().mkdirs()) try {
            Files.createDirectory(ASMHandler.paintingsLocation.resolve("packs"));
            Files.createDirectory(ASMHandler.paintingsLocation.resolve("textures"));
            Files.write(ASMHandler.paintingsLocation.resolve("paintings.json"), Collections.singleton("[\n\n]"),
                    StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
            Files.write(ASMHandler.paintingsLocation.resolve("README.txt"), Collections.singleton(
                    "Thank you for downloading JSON Paintings!\n" +
                    "\n" +
                    "The \"paintings\" folder is where you can define custom paintings, or add painting packs!\n" +
                    "See https://github.com/jbredwards/JSON-Paintings-Mod/blob/1.12.2/README.md for more info.\n" +
                    "\n" +
                    "If you add paintings while the game is loaded, run the \"/jsonpaintings reload\" command.\n" +
                    "If you add painting packs while the game is loaded, run the \"/jsonpaintings reload\"\n" +
                    "command then press F3 + T to load their internal resource packs.\n" +
                    "\n" +
                    "Once you're done with this README.txt file, it can be deleted."
            ));
        }

        catch(@Nonnull final IOException e) { JSONPaintings.LOGGER.error("Could not generate directories.", e); }
    }
}

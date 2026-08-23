package git.jbredwards.jsonpaintings.mod.common.commands;

import com.google.gson.*;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import git.jbredwards.jsonpaintings.mod.JSONPaintings;
import git.jbredwards.jsonpaintings.mod.asm.ASMHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author jbred
 *
 */
final class CommandGeneratePack extends CommandBase
{
    @Nonnull
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    public final boolean trimmed;
    public CommandGeneratePack(final boolean trimmedIn) {
        trimmed = trimmedIn;
    }

    @Nonnull
    @Override
    public String getName() {
        return "pack";
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull final ICommandSender sender) {
        return trimmed ? "jsonpaintings.command.pack.usage.trimmed" : "jsonpaintings.command.pack.usage";
    }

    @Override
    public void execute(@Nonnull final MinecraftServer server, @Nonnull final ICommandSender sender, @Nonnull final String[] args) throws CommandException {
        if(args.length != 2) throw new WrongUsageException(getUsage(sender));
        sender.sendMessage(new TextComponentTranslation("jsonpaintings.command.pack.start"));

        @Nonnull final String id = args[0];
        @Nonnull final String name = args[1];

        @Nonnull JsonArray pack = null;
        try(@Nonnull final Reader reader = Files.newBufferedReader(ASMHandler.paintingsLocation.resolve("paintings.json"))) {
            pack = fixFrontTextures(new JsonParser().parse(IOUtils.toString(reader).replaceAll(JSONPaintings.MODID + ":", id + ":")).getAsJsonArray(), id);
        } catch(@Nonnull final IOException | JsonParseException e) { error(e, null); }

        // Create pack file.
        @Nonnull final Path out = getTimestampedPath();
        try {
            Files.createDirectories(out.getParent());
            try(@Nonnull final ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(out))) { zip.finish(); }
        } catch(@Nonnull final IOException e) { error(e, out); }

        // Compile pack.
        try(@Nonnull final FileSystem fs = FileSystems.newFileSystem(out, null)) {
            Files.write(fs.getPath(JSONPaintings.MODID + ".pack.json"), Collections.singleton("{\"name\":\"" + name + "\",\"id\":\"" + id + "\"}"));
            // assets
            @Nonnull final Path assets = fs.getPath("assets", id);
            Files.createDirectories(assets);
            // paintings.json
            @Nonnull final Path paintingsJson = assets.resolve("paintings/paintings.json");
            Files.createDirectories(paintingsJson.getParent());
            try(@Nonnull final JsonWriter writer = new JsonWriter(Files.newBufferedWriter(paintingsJson))) {
                Streams.write(pack, writer);
            }
            // directories
            copyTextures(assets);
            copyDirectory(assets, id, "blockstates");
            copyDirectory(assets, id, "models");
        }

        // Error during compiling.
        catch(@Nonnull final IOException | RuntimeException e) {
            error(e instanceof RuntimeException ? e.getCause() : e, out);
        }

        // Success.
        @Nullable final ClickEvent event;
        @Nullable final Entity entity = sender.getCommandSenderEntity();
        event = entity instanceof EntityPlayerMP && ((EntityPlayerMP)entity).connection.netManager.isLocalChannel() ? new ClickEvent(ClickEvent.Action.OPEN_FILE, out.toAbsolutePath().toString()) : null;
        notifyCommandListener(sender, this, "jsonpaintings.command.pack.success", new TextComponentString(out.getFileName().toString()).setStyle(new Style().setUnderlined(Boolean.TRUE).setClickEvent(event)));
    }

    @Nonnull
    private static JsonArray fixFrontTextures(@Nonnull final JsonArray pack, @Nonnull final String id) {
        for(@Nonnull final JsonElement element : pack) {
            @Nonnull final JsonObject painting = element.getAsJsonObject();

            boolean specifiesFront = false;
            if(painting.has("textures")) {
                @Nonnull final JsonObject textures = painting.getAsJsonObject("textures");
                if(textures.has("front")) specifiesFront = true;
            }

            else if(painting.has("asset_id")) specifiesFront = true;
            if(!specifiesFront) {
                @Nonnull final JsonObject textures;
                if(painting.has("textures")) textures = painting.getAsJsonObject("textures");
                else painting.add("textures", textures = new JsonObject());

                @Nonnull final String motive = painting.getAsJsonPrimitive("motive").getAsString();
                textures.addProperty("front", id + ':' + new ResourceLocation(motive).getPath());
            }
        }

        return pack;
    }

    private static void copy(@Nonnull final Path root, @Nonnull final String directory, @Nonnull final BiConsumer<Path, Path> action) throws IOException {
        @Nonnull final Path fromRoot = ASMHandler.paintingsLocation.resolve(directory);
        if(Files.exists(fromRoot)) {
            @Nonnull final Path toRoot = root.resolve(directory);
            try(@Nonnull final Stream<Path> files = Files.walk(fromRoot).filter(Files::isRegularFile)) {
                for(@Nonnull final Iterator<Path> it = files.iterator(); it.hasNext();) {
                    @Nonnull final Path from = it.next();
                    @Nonnull final Path to = toRoot.resolve(fromRoot.relativize(from).toString());

                    Files.createDirectories(to.getParent());
                    action.accept(from, to);
                }
            }
        }
    }

    private static void copyDirectory(@Nonnull final Path root, @Nonnull final String id, @Nonnull final String directory) throws IOException {
        copy(root, directory, (from, to) -> {
            try(@Nonnull final Reader reader = Files.newBufferedReader(from)) {
                Files.write(to, Collections.singleton(IOUtils.toString(reader).replaceAll(JSONPaintings.MODID + ":", id + ":")));
            }
            catch(@Nonnull final IOException e) { throw new RuntimeException(e); }
        });
    }

    private static void copyTextures(@Nonnull final Path root) throws IOException {
        copy(root, "textures", (from, to) -> {
            try { Files.copy(from, to); }
            catch(@Nonnull final IOException e) { throw new RuntimeException(e); }
        });
    }

    private static void error(@Nonnull final Throwable e, @Nullable final Path out) throws CommandException {
        JSONPaintings.LOGGER.error(e);
        if(out != null) {
            try { Files.delete(out); }
            catch(@Nonnull final IOException ignored) {}
        }

        throw new CommandException("jsonpaintings.command.pack.error");
    }

    @Nonnull
    private static Path getTimestampedPath() {
        @Nonnull final String date = DATE_FORMAT.format(new Date());
        for(int i = 1; true; i++) {
            @Nonnull final Path path = ASMHandler.paintingsLocation.resolve("out/pack_" + date + (i == 1 ? "" : "_" + i) + ".zip");
            if(!Files.exists(path)) return path;
        }
    }
}

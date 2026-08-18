/*
 * Copyright (c) 2026. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.common.commands;

import git.jbredwards.jsonpaintings.api.PaintingHelper;
import git.jbredwards.jsonpaintings.mod.JSONPaintings;
import git.jbredwards.jsonpaintings.mod.common.util.JSONHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author jbred
 *
 */
final class CommandListPaintings extends CommandBase
{
    public final boolean trimmed;
    public CommandListPaintings(final boolean trimmedIn) {
        trimmed = trimmedIn;
    }

    @Nonnull
    @Override
    public String getName() {
        return "list";
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull final ICommandSender sender) {
        return trimmed ? "jsonpaintings.command.list.usage.trimmed" : "jsonpaintings.command.list.usage";
    }

    @Override
    public void execute(@Nonnull final MinecraftServer server, @Nonnull final ICommandSender sender, @Nonnull final String[] args) throws CommandException {
        if(args.length != 0) throw new WrongUsageException(getUsage(sender));
        JSONPaintings.LOGGER.info(PaintingHelper.PAINTINGS.keySet().stream()
                .sorted()
                .collect(Collectors.joining("\n", "Paintings:\n[\n", "\n]")));
        JSONPaintings.LOGGER.info(JSONHandler.PAINTING_REMAPS.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + " -> " + e.getValue().title)
                .collect(Collectors.joining("\n", "Mappings:\n[\n", "\n]")));
        notifyCommandListener(sender, this, "jsonpaintings.command.list.success",
                new TextComponentTranslation("debug.prefix").setStyle(new Style().setColor(TextFormatting.YELLOW).setBold(Boolean.TRUE)));
    }
}

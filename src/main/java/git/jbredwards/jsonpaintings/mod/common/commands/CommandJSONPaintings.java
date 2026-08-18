/*
 * Copyright (c) 2026. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.common.commands;

import git.jbredwards.jsonpaintings.mod.JSONPaintings;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;
import net.minecraftforge.server.command.CommandTreeHelp;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
public final class CommandJSONPaintings extends CommandTreeBase
{
    public final boolean trimmed;
    public CommandJSONPaintings(final boolean trimmedIn) {
        trimmed = trimmedIn;
        super.addSubcommand(trimmedIn ? new CommandReloadPaintings.Trimmed() : new CommandReloadPaintings());
        super.addSubcommand(new CommandTreeHelp(this));
    }

    @Nonnull
    @Override
    public String getName() {
        return trimmed ? "paintings" :  JSONPaintings.MODID;
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull final ICommandSender sender) {
        return trimmed ? "jsonpaintings.command.tree.usage.trimmed" : "jsonpaintings.command.tree.usage";
    }

    @Override
    public void addSubcommand(@Nonnull final ICommand command) {
        throw new UnsupportedOperationException("Don't add sub-commands to this command, create your own command.");
    }
}

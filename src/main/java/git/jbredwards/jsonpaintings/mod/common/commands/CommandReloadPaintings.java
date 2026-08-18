/*
 * Copyright (c) 2025. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.common.commands;

import git.jbredwards.jsonpaintings.mod.JSONPaintings;
import git.jbredwards.jsonpaintings.mod.common.compat.jei.JEIHandler;
import git.jbredwards.jsonpaintings.mod.common.util.JSONHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
class CommandReloadPaintings extends CommandBase
{
    @Nonnull
    @Override
    public String getName() { return "reload"; }

    @Nonnull
    @Override
    public String getUsage(@Nonnull final ICommandSender sender) { return "jsonpaintings.command.usage"; }

    @Override
    public void execute(@Nonnull final MinecraftServer server, @Nonnull final ICommandSender sender, @Nonnull final String[] args) throws CommandException {
        if(args.length != 0) throw new WrongUsageException(getUsage(sender));
        if(!server.isDedicatedServer()) {
            if(server.getPlayerList().getCurrentPlayerCount() > 1) throw new CommandException("jsonpaintings.command.playerExceeded");

            reloadPaintings();
            notifyCommandListener(sender, this, "jsonpaintings.command.success");
        }

        else throw new CommandException("jsonpaintings.command.dedicatedServer");
    }

    protected synchronized void reloadPaintings() throws CommandException {
        try {
            JSONHandler.readInstance(true);
            FMLCommonHandler.instance().reloadSearchTrees();
            if(JSONPaintings.IS_JEI_INSTALLED) JEIHandler.addNewPaintings();
        }
        catch(@Nonnull final Exception e) { throw new CommandException(e.getMessage()); }
    }

    // an alternate command that performs the same function
    static class Trimmed extends CommandReloadPaintings
    {
        @Nonnull
        @Override
        public String getUsage(@Nonnull ICommandSender sender) { return "jsonpaintings.command.usage.trimmed"; }
    }
}

/*
 * Copyright (c) 2024. jbredwards
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
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author jbred
 *
 */
public class CommandJSONPaintings extends CommandBase
{
    @Nonnull
    @Override
    public String getName() { return JSONPaintings.MODID; }

    @Nonnull
    @Override
    public String getUsage(@Nonnull final ICommandSender sender) { return "jsonpaintings.command.usage"; }

    @Override
    public void execute(@Nonnull final MinecraftServer server, @Nonnull final ICommandSender sender, @Nonnull final String[] args) throws CommandException {
        if(args.length != 1 || !args[0].equals("reload")) throw new WrongUsageException(getUsage(sender));
        if(!server.isDedicatedServer()) {
            if(server.getPlayerList().getCurrentPlayerCount() > 1) throw new CommandException("jsonpaintings.command.playerExceeded");

            reloadPaintings();
            notifyCommandListener(sender, this, "jsonpaintings.command.success");
        }

        else throw new CommandException("jsonpaintings.command.dedicatedServer");
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(@Nonnull final MinecraftServer server, @Nonnull final ICommandSender sender, @Nonnull final String[] args, @Nullable final BlockPos targetPos) {
        return args.length < 2 ? getListOfStringsMatchingLastWord(args, args.length == 0 ? getName() : "reload") : Collections.emptyList();
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
    public static class Trimmed extends CommandJSONPaintings
    {
        @Nonnull
        @Override
        public String getName() { return "paintings"; }

        @Nonnull
        @Override
        public String getUsage(@Nonnull ICommandSender sender) { return "jsonpaintings.command.usage.trimmed"; }
    }
}

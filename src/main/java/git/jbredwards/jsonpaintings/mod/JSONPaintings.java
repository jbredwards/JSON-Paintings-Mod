/*
 * Copyright (c) 2024. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import git.jbredwards.jsonpaintings.mod.asm.ASMHandler;
import git.jbredwards.jsonpaintings.mod.client.PaintingsResourcePack;
import git.jbredwards.jsonpaintings.mod.client.RenderJSONPainting;
import git.jbredwards.jsonpaintings.mod.common.EventHandler;
import git.jbredwards.jsonpaintings.mod.common.capability.IArtCapability;
import git.jbredwards.jsonpaintings.mod.common.commands.CommandJSONPaintings;
import git.jbredwards.jsonpaintings.mod.common.compat.top.TOPHandler;
import git.jbredwards.jsonpaintings.mod.common.util.IJSONPainting;
import git.jbredwards.jsonpaintings.mod.common.util.JSONHandler;
import net.minecraft.entity.item.EntityPainting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DependencyParser;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author jbred
 *
 */
@SuppressWarnings({"UnstableApiUsage", "unused"})
public final class JSONPaintings extends DummyModContainer
{
    @Nonnull
    public static final String MODID = "jsonpaintings", NAME = "JSON Paintings", VERSION = "1.3.0";
    public static boolean IS_PSG_INSTALLED, IS_JEI_INSTALLED;

    @Nonnull
    public static final Logger LOGGER = LogManager.getFormatterLogger(NAME);
    public JSONPaintings() {
        super(new ModMetadata());
        getMetadata().modId = MODID;
        getMetadata().name = NAME;
        getMetadata().version = VERSION;
        getMetadata().url = "https://github.com/jbredwards/JSON-Paintings-Mod";
        getMetadata().description = "Easily add custom paintings through a json file!";
        getMetadata().authorList = Collections.singletonList("jbredwards");
        // due to https://github.com/mezz/JustEnoughItems/issues/1549
        getMetadata().dependencies.addAll(new DependencyParser(getModId(), FMLCommonHandler.instance().getSide()).parseDependencies("after:jei@[4.15.0.276,);").dependencies);
    }

    @Override
    public boolean registerBus(@Nonnull final EventBus bus, @Nonnull final LoadController controller) {
        bus.register(this);
        return true;
    }

    @Nonnull
    @Override
    public File getSource() { return ASMHandler.modLocation; }

    @Nonnull
    @SideOnly(Side.CLIENT)
    @Override
    public Class<?> getCustomResourcePackClass() { return PaintingsResourcePack.class; }

    @SideOnly(Side.CLIENT)
    @Subscribe
    public void preInitClient(@Nonnull FMLPreInitializationEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityPainting.class, RenderJSONPainting::new);
    }

    @Subscribe
    public void preInit(@Nonnull final FMLPreInitializationEvent event) {
        CapabilityManager.INSTANCE.register(IArtCapability.class, IArtCapability.Storage.INSTANCE, IArtCapability.Impl::new);
        MinecraftForge.EVENT_BUS.register(IArtCapability.class);
        MinecraftForge.EVENT_BUS.register(EventHandler.class);
        IS_PSG_INSTALLED = Loader.isModLoaded("paintingselgui");
        IS_JEI_INSTALLED = Loader.isModLoaded("jei");
    }

    @Subscribe
    public void init(@Nonnull final FMLInitializationEvent event) {
        if(Loader.isModLoaded("theoneprobe")) TOPHandler.initialize();
    }

    @Subscribe
    public void postInit(@Nonnull final FMLPostInitializationEvent event) throws Exception {
        JSONHandler.readMods();
        JSONHandler.readInstance(false);
        // set modid & mod name for vanilla's paintings
        for(int i = 0; i < 26; i++) {
            JSONHandler.MODID_LOOKUP.put(EntityPainting.EnumArt.values()[i], "minecraft");
            IJSONPainting.from(EntityPainting.EnumArt.values()[i]).setModName("Minecraft");
        }
    }

    @Subscribe
    public void serverStarting(@Nonnull final FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandJSONPaintings());
        event.registerServerCommand(new CommandJSONPaintings.Trimmed());
    }

    @Nonnull
    @Override
    public List<ArtifactVersion> getDependencies() { return getMetadata().dependencies; }
}

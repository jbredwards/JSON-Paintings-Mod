/*
 * Copyright (c) 2025. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod;

import git.jbredwards.jsonpaintings.mod.client.RenderJSONPainting;
import git.jbredwards.jsonpaintings.mod.common.capability.IArtCapability;
import git.jbredwards.jsonpaintings.mod.common.commands.CommandJSONPaintings;
import git.jbredwards.jsonpaintings.mod.common.compat.top.TOPHandler;
import git.jbredwards.jsonpaintings.mod.common.util.IJSONPainting;
import git.jbredwards.jsonpaintings.mod.common.util.JSONHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.item.EntityPainting;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
@Mod(modid = JSONPaintings.MODID, name = JSONPaintings.NAME, version = JSONPaintings.VERSION,
dependencies = "after:jei@[4.15.0.276,);") // due to https://github.com/mezz/JustEnoughItems/issues/1549
public final class JSONPaintings
{
    @Nonnull public static final String MODID = Tags.MOD_ID, NAME = Tags.NAME, VERSION = Tags.VERSION;
    @Nonnull public static final Logger LOGGER = LogManager.getFormatterLogger(NAME);
    public static boolean IS_PSG_INSTALLED, IS_JEI_INSTALLED;

    @Mod.EventHandler
    public void preInit(@Nonnull final FMLPreInitializationEvent event) {
        CapabilityManager.INSTANCE.register(IArtCapability.class, IArtCapability.Storage.INSTANCE, () -> {throw new UnsupportedOperationException();});
        MinecraftForge.EVENT_BUS.register(IArtCapability.class);
        IS_PSG_INSTALLED = Loader.isModLoaded("paintingselgui");
        IS_JEI_INSTALLED = Loader.isModLoaded("jei");
    }

    @SideOnly(Side.CLIENT)
    @Mod.EventHandler
    public void preInitClient(@Nonnull final FMLPreInitializationEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityPainting.class, RenderJSONPainting::new);
        // remove "disable" button in mod gui
        ReflectionHelper.setPrivateValue(FMLModContainer.class, (FMLModContainer)Loader.instance().activeModContainer(), ModContainer.Disableable.NEVER, "disableability");
        // allow this mod's description and credits to be translated
        @Nonnull final ModMetadata metadata = event.getModMetadata();
        @Nonnull final String credits = metadata.credits, desc = metadata.description;
        @Nonnull final String creditsKey = "mod." + MODID + ".credits", descKey = "mod." + MODID + ".description";
        ((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener((ISelectiveResourceReloadListener)(manager, condition) -> {
            if(condition.test(VanillaResourceType.LANGUAGES)) {
                metadata.credits = I18n.hasKey(creditsKey) ? I18n.format(creditsKey).replace("\\n", "\n") : credits;
                metadata.description = I18n.hasKey(descKey) ? I18n.format(descKey).replace("\\n", "\n") : desc;
            }
        });
    }

    @Mod.EventHandler
    public void init(@Nonnull final FMLInitializationEvent event) {
        if(Loader.isModLoaded("theoneprobe")) TOPHandler.initialize();
    }

    @Mod.EventHandler
    public void postInit(@Nonnull final FMLPostInitializationEvent event) throws Exception {
        JSONHandler.readMods();
        JSONHandler.readInstance(false);
        // set modid & mod name for vanilla's paintings
        for(int i = 0; i < 26; i++) {
            JSONHandler.MODID_LOOKUP.put(EntityPainting.EnumArt.values()[i], "minecraft");
            IJSONPainting.from(EntityPainting.EnumArt.values()[i]).setModName("Minecraft");
        }
    }

    @Mod.EventHandler
    public void serverStarting(@Nonnull final FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandJSONPaintings());
        event.registerServerCommand(new CommandJSONPaintings.Trimmed());
    }
}

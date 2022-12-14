package git.jbredwards.jsonpaintings;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import git.jbredwards.jsonpaintings.asm.ASMHandler;
import git.jbredwards.jsonpaintings.client.PaintingsResourcePack;
import git.jbredwards.jsonpaintings.client.RenderJSONPainting;
import git.jbredwards.jsonpaintings.common.EventHandler;
import git.jbredwards.jsonpaintings.common.capability.IArtCapability;
import git.jbredwards.jsonpaintings.common.util.JSONHandler;
import net.minecraft.entity.item.EntityPainting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

/**
 *
 * @author jbred
 *
 */
@SuppressWarnings({"UnstableApiUsage", "unused"})
public final class Main extends DummyModContainer
{
    public static boolean IS_PSG_INSTALLED;

    public Main() {
        super(new ModMetadata());
        final ModMetadata md = getMetadata();
        md.modId = Constants.MODID;
        md.name = Constants.NAME;
        md.version = Constants.VERSION;
        md.url = "https://github.com/jbredwards/JSON-Paintings-Mod";
        md.description = "Easily add custom paintings through a json file!";
        md.authorList = Collections.singletonList("jbredwards");
    }

    @Override
    public boolean registerBus(@Nonnull EventBus bus, @Nonnull LoadController controller) {
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
    public void clientPreInit(@Nonnull FMLPreInitializationEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityPainting.class, RenderJSONPainting::new);
    }

    @Subscribe
    public void commonPreInit(@Nonnull FMLPreInitializationEvent event) {
        CapabilityManager.INSTANCE.register(IArtCapability.class, IArtCapability.Storage.INSTANCE, IArtCapability.Impl::new);
        MinecraftForge.EVENT_BUS.register(IArtCapability.class);
        MinecraftForge.EVENT_BUS.register(EventHandler.class);
        IS_PSG_INSTALLED = Loader.isModLoaded("paintingselgui");
    }

    @Subscribe
    public void postInit(@Nonnull FMLPostInitializationEvent event) throws IOException {
        JSONHandler.readMods();
        JSONHandler.readInstance();
    }
}

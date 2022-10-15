package git.jbredwards.jsonpaintings;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import git.jbredwards.jsonpaintings.client.InternalArtPack;
import git.jbredwards.jsonpaintings.client.RenderJSONPainting;
import git.jbredwards.jsonpaintings.common.JSONHandler;
import net.minecraft.entity.item.EntityPainting;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
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
    public Main() {
        super(new ModMetadata());
        final ModMetadata md = getMetadata();
        md.modId = "jsonpaintings";
        md.name = "JSON Paintings";
        md.url = "https://github.com/jbredwards/JSON-Paintings-Mod";
        md.description = "Easily add custom paintings through json files!";
        md.authorList = Collections.singletonList("jbredwards");
        md.version = "1.0";
    }

    @Override
    public Class<?> getCustomResourcePackClass() { return InternalArtPack.class; }

    @Override
    public boolean registerBus(@Nonnull EventBus bus, @Nonnull LoadController controller) {
        bus.register(this);
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Subscribe
    public void clientPreInit(@Nonnull FMLPreInitializationEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityPainting.class, RenderJSONPainting::new);
    }

    @Subscribe
    public void commonPreInit(@Nonnull FMLPreInitializationEvent event) throws IOException {
        JSONHandler.readInstance();
        JSONHandler.readMods();
    }
}

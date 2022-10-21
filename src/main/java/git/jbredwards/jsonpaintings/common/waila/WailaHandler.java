package git.jbredwards.jsonpaintings.common.waila;

import git.jbredwards.jsonpaintings.common.util.IJSONPainting;
import mcp.mobius.waila.api.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.text.translation.I18n;

import javax.annotation.Nonnull;
import java.util.List;

@WailaPlugin
public final class WailaHandler implements IWailaPlugin
{
    @Override
    public void register(@Nonnull IWailaRegistrar registrar) {
        registrar.registerBodyProvider(Provider.INSTANCE, EntityPainting.class);
        registrar.addConfig("jsonpaintings", "showMotive", true);
        registrar.addConfig("jsonpaintings", "showExclusive", true);
        registrar.addConfig("jsonpaintings", "showSneakToCapture", true);
    }

    enum Provider implements IWailaEntityProvider
    {
        INSTANCE;

        @Nonnull
        @Override
        public List<String> getWailaBody(@Nonnull Entity entity, @Nonnull List<String> currentTip, @Nonnull IWailaEntityAccessor accessor, @Nonnull IWailaConfigHandler config) {
            if(entity instanceof EntityPainting) {
                final EntityPainting.EnumArt art = ((EntityPainting)entity).art;
                if(config.getConfig("showMotive"))
                    currentTip.add(I18n.translateToLocalFormatted("jsonpaintings.wailaMotive", art.title));

                currentTip.add("");
                if(!accessor.getPlayer().isCreative()) {
                    if(config.getConfig("showSneakToCapture"))
                        currentTip.add(I18n.translateToLocal("jsonpaintings.wailaSneakToCapture"));

                    if(IJSONPainting.from(art).isCreative() && config.getConfig("showExclusive"))
                        currentTip.add(I18n.translateToLocal("jsonpaintings.wailaExclusive"));
                }
            }

            return currentTip;
        }
    }
}

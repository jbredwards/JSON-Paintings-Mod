/*
 * Copyright (c) 2024. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.common.compat.jei;

import git.jbredwards.jsonpaintings.mod.common.capability.IArtCapability;
import mezz.jei.JustEnoughItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.gui.textures.Textures;
import mezz.jei.startup.JeiStarter;
import mezz.jei.startup.ProxyCommonClient;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 *
 * @author jbred
 *
 */
@JEIPlugin
public final class JEIHandler implements IModPlugin
{
    @SuppressWarnings("deprecation")
    public static void addNewPaintings() {
        // TODO: find a way to add the paintings to JEI without restarting all of JEI
        Minecraft.getMinecraft().addScheduledTask(() -> {
            @Nonnull final ProxyCommonClient instance = (ProxyCommonClient)JustEnoughItems.getProxy();
            @Nonnull final List<IModPlugin> plugins = ReflectionHelper.getPrivateValue(ProxyCommonClient.class, instance, "plugins");
            @Nonnull final JeiStarter starter = ReflectionHelper.getPrivateValue(ProxyCommonClient.class, instance, "starter");
            @Nonnull final Textures textures = ReflectionHelper.getPrivateValue(ProxyCommonClient.class, instance, "textures");

            starter.start(plugins, textures);
        });
    }

    @Override
    public void registerItemSubtypes(@Nonnull final ISubtypeRegistry subtypeRegistryIn) {
        subtypeRegistryIn.registerSubtypeInterpreter(Items.PAINTING, stack -> {
            @Nullable final IArtCapability cap = IArtCapability.get(stack);
            return cap != null && cap.hasArt() ? cap.getArt().title : "Any";
        });
    }
}

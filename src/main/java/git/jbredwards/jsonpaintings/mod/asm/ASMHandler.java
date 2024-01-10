/*
 * Copyright (c) 2024. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.Map;

/**
 *
 * @author jbred
 *
 */
@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.Name("JSON Paintings Plugin")
public final class ASMHandler implements IFMLLoadingPlugin
{
    public static File modLocation;

    @Nonnull
    @Override
    public String[] getASMTransformerClass() {
        return new String[] {
                "git.jbredwards.jsonpaintings.mod.asm.transformer.EntityPaintingTransformer",
                "git.jbredwards.jsonpaintings.mod.asm.transformer.EnumArtTransformer",
                "git.jbredwards.jsonpaintings.mod.asm.transformer.ItemTransformer",
                //mod compat
                "git.jbredwards.jsonpaintings.mod.asm.transformer.PSGRevampedClientTransformer",
                "git.jbredwards.jsonpaintings.mod.asm.transformer.PSGRevampedServerTransformer",
                "git.jbredwards.jsonpaintings.mod.asm.transformer.TwilightForestTransformer"
        };
    }

    @Nonnull
    @Override
    public String getModContainerClass() { return "git.jbredwards.jsonpaintings.mod.JSONPaintings"; }

    @Override
    public void injectData(@Nonnull Map<String, Object> data) { modLocation = (File)data.get("coremodLocation"); }

    @Nullable
    @Override
    public String getSetupClass() { return null; }

    @Nullable
    @Override
    public String getAccessTransformerClass() { return null; }
}

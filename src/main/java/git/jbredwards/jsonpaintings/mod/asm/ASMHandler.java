/*
 * Copyright (c) 2025. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.asm;

import com.google.common.collect.Lists;
import git.jbredwards.jsonpaintings.mod.asm.transformer.*;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
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
    @SuppressWarnings("unused")
    public static final class Transformer implements IClassTransformer
    {
        @Nonnull
        public static final List<IClassTransformer> TRANSFORMERS = Lists.newArrayList(
                new EntityPaintingTransformer(),
                new EnumHelperTransformer(),
                new EnumArtTransformer(),
                new ItemTransformer(),
                //mod compat
                new HwylaTransformer(),
                new PSGRevampedClientTransformer(),
                new PSGRevampedServerTransformer(),
                new TwilightForestTransformer()
        );

        @Nullable
        @Override
        public byte[] transform(@Nullable final String name, @Nullable final String transformedName, @Nullable final byte[] basicClass) {
            if(basicClass == null || transformedName == null) return basicClass;
            else return TRANSFORMERS.stream().reduce(basicClass, (b, ct) -> ct.transform(name, transformedName, b), (b1, b2) -> b2);
        }
    }

    public static File modLocation;
    public static Path paintingsLocation;

    @Override
    public void injectData(@Nonnull final Map<String, Object> data) {
        modLocation = (File)data.get("coremodLocation");
        paintingsLocation = ((File)data.get("mcLocation")).toPath().resolve("paintings");
    }

    @Nonnull
    @Override
    public String[] getASMTransformerClass() {
        return new String[] {"git.jbredwards.jsonpaintings.mod.asm.ASMHandler$Transformer"};
    }

    @Nonnull
    @Override
    public String getModContainerClass() {
        return "git.jbredwards.jsonpaintings.mod.JSONPaintingsCompanion";
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Nullable
    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}

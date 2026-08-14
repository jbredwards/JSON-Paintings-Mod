/*
 * Copyright (c) 2026. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod;

import com.google.common.eventbus.EventBus;
import git.jbredwards.jsonpaintings.mod.asm.ASMHandler;
import git.jbredwards.jsonpaintings.mod.client.PaintingsResourcePack;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.io.File;

/**
 * Split from base mod, so I can take advantage of benefits from the @Mod annotation.
 * @author jbred
 *
 */
@SuppressWarnings({"UnstableApiUsage", "unused"})
public final class JSONPaintingsCompanion extends DummyModContainer
{
    public JSONPaintingsCompanion() {
        super(new ModMetadata());
        getMetadata().parent = Tags.MOD_ID;
        getMetadata().modId = Tags.MOD_ID + "_companion";
        getMetadata().name = Tags.NAME + " (Companion)";
        getMetadata().version = Tags.VERSION;
    }

    @Override
    public boolean registerBus(@Nonnull final EventBus bus, @Nonnull final LoadController controller) {
        return true;
    }

    @Nonnull
    @Override
    public File getSource() {
        return ASMHandler.modLocation;
    }

    @Nonnull
    @SideOnly(Side.CLIENT)
    @Override
    public Class<?> getCustomResourcePackClass() {
        return PaintingsResourcePack.class;
    }
}

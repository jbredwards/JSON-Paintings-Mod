/*
 * Copyright (c) 2026. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.asm;

import git.jbredwards.jsonpaintings.api.ActivePaintingInfo;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * {@link net.minecraft.entity.item.EntityPainting.EnumArt} implements this at runtime.
 * @author jbred
 *
 */
@ApiStatus.Internal
public interface AccessorEnumArt
{
    @Nullable
    ActivePaintingInfo jsonpaintings$info();
    void jsonpaintings$info(@Nonnull final ActivePaintingInfo info);
}

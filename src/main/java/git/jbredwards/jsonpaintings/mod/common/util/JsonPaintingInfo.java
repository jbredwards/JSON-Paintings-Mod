/*
 * Copyright (c) 2026. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.common.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import git.jbredwards.jsonpaintings.api.ActivePaintingInfo;
import git.jbredwards.jsonpaintings.api.PaintingHelper;
import git.jbredwards.jsonpaintings.api.PaintingInfo;
import git.jbredwards.jsonpaintings.mod.JSONPaintings;
import net.minecraft.entity.item.EntityPainting;
import net.minecraftforge.common.util.EnumHelper;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Holds info for creating actual painting types down the line.
 * @author jbred
 *
 */
@ApiStatus.Internal
public final class JsonPaintingInfo implements Runnable
{
    @Nonnull public final String motive;
    @Nonnull public final PaintingInfo info;

    @Nullable public final String[] remapping;
    @Nullable public final JsonElement rarity;

    @Nullable public Boolean isTreasure;
    @Nullable public Boolean alwaysCapture;

    public JsonPaintingInfo(@Nonnull final String motiveIn, @Nonnull final PaintingInfo infoIn,
                            @Nullable final String[] remappingIn, @Nullable final JsonElement rarityIn,
                            @Nullable final Boolean isTreasureIn, @Nullable final Boolean alwaysCaptureIn) {
        motive = motiveIn;
        info = infoIn;
        remapping = remappingIn;
        rarity = rarityIn;
        isTreasure = isTreasureIn;
        alwaysCapture = alwaysCaptureIn;
    }

    @Override
    public void run() {
        construct();
        postInit();
    }

    public void construct() {
        @Nullable final EntityPainting.EnumArt existing = PaintingHelper.get(motive);
        if(existing == null) create();
        else modify(existing);
    }

    public void postInit() {
        if(rarity != null) {
            @Nullable final EntityPainting.EnumArt art = PaintingHelper.get(motive);
            if(art != null) { // Should always pass.
                try { ActivePaintingInfo.get(art).rarity = RarityUtils.parse(rarity); }
                catch(@Nonnull final JsonParseException e) {
                    JSONPaintings.LOGGER.error("An error occurred while parsing rarity for %s", motive, e);
                }
            }
        }
    }

    private void create() {
        info.setWidth(Math.max(16, info.getWidth()));
        info.setHeight(Math.max(16, info.getHeight()));
        @Nullable final EntityPainting.EnumArt art = EnumHelper.addArt(
                "JSON_PAINTINGS_GENERATED_ID" + JSONHandler.nextPaintingId++,
                motive, info.getWidth(), info.getHeight(), info.getXOffset(), info.getYOffset());

        // Should never pass, but exists because EnumHelper is nullable.
        if(art == null) {
            JSONHandler.nextPaintingId--;
            throw new IllegalArgumentException(
                    "A critical error has occurred while creating painting with the motive: " + motive);
        }

        if(isTreasure != null) info.isTreasure = isTreasure;
        info.alwaysCapture = alwaysCapture != null ? alwaysCapture : info.isTreasure;
        if(info.frontTexture == null) info.frontTexture = JSONHandler.buildLocation(JSONHandler.frontTexturePath(info, motive));

        // Apply to enum.
        ActivePaintingInfo.get(art).copyFrom(info, true);
        if(remapping != null) PaintingHelper.remap(art, remapping);
    }

    private void modify(@Nonnull final EntityPainting.EnumArt art) {
        @Nonnull final ActivePaintingInfo active = ActivePaintingInfo.get(art);

        info.setXOffset(active.getXOffset());
        info.setYOffset(active.getYOffset());

        info.modId = active.modId;
        info.modName = active.modName;
        info.isTreasure = isTreasure != null ? isTreasure : active.isTreasure;
        info.alwaysCapture = alwaysCapture != null ? alwaysCapture : active.alwaysCapture;

        // Apply to enum.
        active.copyFrom(info, false);
        if(remapping != null) PaintingHelper.remap(art, remapping);
    }
}

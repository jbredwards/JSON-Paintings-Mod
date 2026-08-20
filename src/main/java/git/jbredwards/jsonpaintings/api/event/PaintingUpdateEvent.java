/*
 * Copyright (c) 2025. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.api.event;

import git.jbredwards.jsonpaintings.api.ActivePaintingInfo;
import git.jbredwards.jsonpaintings.api.PaintingInfo;
import net.minecraft.entity.item.EntityPainting;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;

/**
 * PaintingUpdateEvent is fired when an EntityPainting is updated. <br>
 * This event is fired whenever an EntityPainting is updated in
 * {@link EntityPainting#onUpdate()}. <br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the EntityPainting does not update.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.
 *
 * @author jbred
 *
 */
@ApiStatus.AvailableSince("1.3.0")
@Cancelable
public class PaintingUpdateEvent extends EntityEvent
{
    /**
     * The painting entity being ticked.
     */
    @ApiStatus.AvailableSince("1.3.0")
    @Nonnull
    public final EntityPainting painting;

    @ApiStatus.Internal
    public PaintingUpdateEvent(@Nonnull final EntityPainting paintingIn) {
        super(paintingIn);
        this.painting = paintingIn;
    }

    /**
     * @return The painting entity being ticked.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.3.0")
    @Nonnull
    public EntityPainting getPainting() {
        return this.painting;
    }

    /**
     * @return Type of the painting entity being ticked.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.3.0")
    @Nonnull
    public EntityPainting.EnumArt getArt() {
        return this.getPainting().art;
    }

    /**
     * @return JSON Paintings data of the painting entity being ticked.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    @Nonnull
    public PaintingInfo getExtras() {
        return ActivePaintingInfo.get(this.getArt());
    }

    /**
     * Note: This compares the motives, not the {@link PaintingInfo#title title}.
     * @param motive The motive to compare against.
     * @return True if this painting's art motives is equal to the one provided.
     * @throws NullPointerException If motive is null.
     * @author jbred
     */
    @ApiStatus.AvailableSince("1.5.0")
    public boolean matches(@Nonnull final String motive) {
        return motive.equals(this.getArt().title);
    }

    /**
     * Use {@link PaintingUpdateEvent#matches} instead.
     * This method uses an incorrect calculation, motives are case-sensitive.
     */
    @ApiStatus.AvailableSince("1.3.0")
    @Deprecated
    public boolean matchesArt(@Nonnull final String motive) {
        return motive.equalsIgnoreCase(this.getArt().title);
    }
}

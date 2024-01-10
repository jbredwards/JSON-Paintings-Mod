/*
 * Copyright (c) 2024. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.api.event;

import net.minecraft.entity.item.EntityPainting;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

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
 * @since 1.3.0
 * @author jbred
 *
 */
@Cancelable
public class PaintingUpdateEvent extends EntityEvent
{
    @Nonnull
    public final EntityPainting painting;
    public PaintingUpdateEvent(@Nonnull EntityPainting paintingIn) {
        super(paintingIn);
        painting = paintingIn;
    }

    @Nonnull
    public EntityPainting getPainting() { return painting; }

    @Nonnull
    public EntityPainting.EnumArt getArt() { return getPainting().art; }

    /**
     * @param mantle the mantle to compare against.
     * @return true if this painting's art mantle is equal to the one provided.
     */
    public boolean matchesArt(@Nonnull final String mantle) { return mantle.equalsIgnoreCase(getArt().title); }
}

/*
 * Copyright (c) 2024. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.common.capability;

import git.jbredwards.jsonpaintings.mod.JSONPaintings;
import git.jbredwards.jsonpaintings.mod.common.util.JSONHandler;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author jbred
 *
 */
@SuppressWarnings("ConstantConditions")
public interface IArtCapability
{
    EntityPainting.EnumArt getArt();
    void setArt(@Nullable EntityPainting.EnumArt artIn);
    default boolean hasArt() { return getArt() != null; }

    @CapabilityInject(IArtCapability.class)
    @Nonnull Capability<IArtCapability> CAPABILITY = null;
    @Nonnull ResourceLocation LOCATION = new ResourceLocation(JSONPaintings.MODID, "painting");

    @Nullable
    static IArtCapability get(@Nullable ICapabilityProvider provider) {
        return provider != null && provider.hasCapability(CAPABILITY, null) ? provider.getCapability(CAPABILITY, null) : null;
    }

    @SubscribeEvent
    static void attach(@Nonnull AttachCapabilitiesEvent<ItemStack> event) {
        if(event.getObject().getItem() == Items.PAINTING) event.addCapability(LOCATION, new ICapabilitySerializable<NBTBase>() {
            @Nullable
            final IArtCapability instance = CAPABILITY.getDefaultInstance();

            @Override
            public boolean hasCapability(@Nonnull final Capability<?> capability, @Nullable final EnumFacing facing) {
                return capability == CAPABILITY;
            }

            @Nullable
            @Override
            public <T> T getCapability(@Nonnull final Capability<T> capability, @Nullable final EnumFacing facing) {
                return hasCapability(capability, facing) ? CAPABILITY.cast(instance) : null;
            }

            @Nonnull
            @Override
            public NBTBase serializeNBT() { return CAPABILITY.writeNBT(instance, null); }

            @Override
            public void deserializeNBT(@Nonnull final NBTBase nbt) { CAPABILITY.readNBT(instance, null, nbt); }
        });
    }

    class Impl implements IArtCapability
    {
        @Nullable
        EntityPainting.EnumArt art;

        @Nullable
        @Override
        public EntityPainting.EnumArt getArt() { return art; }

        @Override
        public void setArt(@Nullable EntityPainting.EnumArt artIn) { art = artIn; }
    }

    enum Storage implements Capability.IStorage<IArtCapability>
    {
        INSTANCE;

        @Nonnull
        @Override
        public NBTBase writeNBT(@Nonnull Capability<IArtCapability> capability, @Nonnull IArtCapability instance, @Nullable EnumFacing side) {
            return instance.hasArt() ? new NBTTagString(instance.getArt().title) : new NBTTagString();
        }

        @Override
        public void readNBT(@Nonnull Capability<IArtCapability> capability, @Nonnull IArtCapability instance, @Nullable EnumFacing side, @Nullable NBTBase nbt) {
            if(nbt instanceof NBTTagString) {
                final String title = ((NBTTagString)nbt).getString();
                if(!title.isEmpty()) {
                    for(EntityPainting.EnumArt art : EntityPainting.EnumArt.values()) {
                        if(art.title.equals(title)) {
                            instance.setArt(art);
                            return;
                        }
                    }

                    // try remapping
                    @Nullable final EntityPainting.EnumArt mapped = JSONHandler.PAINTING_REMAPS.get(title);
                    if(mapped != null) instance.setArt(mapped);
                    else JSONPaintings.LOGGER.error("Painting with motive: \"" + title + "\" has been removed, resulting in lost data. This can be fixed by assigning it a mapping.");
                }
            }
        }
    }
}

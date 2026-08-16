/*
 * Copyright (c) 2025. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.common.capability;

import git.jbredwards.jsonpaintings.api.ActivePaintingInfo;
import git.jbredwards.jsonpaintings.api.PaintingHelper;
import git.jbredwards.jsonpaintings.mod.JSONPaintings;
import git.jbredwards.jsonpaintings.mod.common.util.JSONHandler;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;

/**
 *
 * @author jbred
 *
 */
@SuppressWarnings("ConstantConditions")
public interface IArtCapability
{
    @CapabilityInject(IArtCapability.class)
    @Nonnull Capability<IArtCapability> CAPABILITY = null;
    @Nonnull ResourceLocation LOCATION = new ResourceLocation(JSONPaintings.MODID, "painting");

    @Nullable EntityPainting.EnumArt getArt();
    void setArt(@Nullable final EntityPainting.EnumArt artIn);

    @Nonnull
    static Optional<ActivePaintingInfo> getInfo(@Nullable final ICapabilityProvider provider) {
        return getOptional(provider).map(ActivePaintingInfo::get);
    }

    @Nonnull
    static Optional<EntityPainting.EnumArt> getOptional(@Nullable final ICapabilityProvider provider) {
        return Optional.ofNullable(get(provider)).map(IArtCapability::getArt);
    }

    @Nullable
    static IArtCapability get(@Nullable final ICapabilityProvider provider) {
        return provider != null && provider.hasCapability(CAPABILITY, null) ? provider.getCapability(CAPABILITY, null) : null;
    }

    @SubscribeEvent
    static void attach(@Nonnull final AttachCapabilitiesEvent<ItemStack> event) {
        if(event.getObject().getItem() == Items.PAINTING) event.addCapability(LOCATION, new ICapabilitySerializable<NBTBase>() {
            @Nonnull final IArtCapability instance = new Impl(event.getObject());

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
        @Nonnull
        protected final ItemStack stack;
        public Impl(@Nonnull final ItemStack stackIn) { stack = stackIn; }

        @Nullable
        @Override
        public EntityPainting.EnumArt getArt() {
            if(stack.getTagCompound() != null && stack.getTagCompound().hasKey(LOCATION.toString(), Constants.NBT.TAG_STRING)) {
                @Nullable final EntityPainting.EnumArt art = Storage.getFromTitle(stack.getTagCompound().getString(LOCATION.toString()), a -> stack.setTagInfo(LOCATION.toString(), new NBTTagString(a.title)));

                if(art == null) setArt(null);
                else return art;
            }

            return null;
        }

        @Override
        public void setArt(@Nullable final EntityPainting.EnumArt artIn) {
            if(artIn != null) stack.setTagInfo(LOCATION.toString(), new NBTTagString(artIn.title));
            else if(stack.getTagCompound() != null && stack.getTagCompound().hasKey(LOCATION.toString(), Constants.NBT.TAG_STRING)) {
                stack.getTagCompound().removeTag(LOCATION.toString());
                if(stack.getTagCompound().isEmpty()) stack.setTagCompound(null);
            }
        }
    }

    enum Storage implements Capability.IStorage<IArtCapability>
    {
        INSTANCE;

        @Nonnull
        @Override
        public NBTBase writeNBT(@Nonnull Capability<IArtCapability> capability, @Nonnull IArtCapability instance, @Nullable EnumFacing side) {
            return new NBTTagByte((byte)0);
        }

        @Override
        public void readNBT(@Nonnull Capability<IArtCapability> capability, @Nonnull IArtCapability instance, @Nullable EnumFacing side, @Nullable NBTBase nbt) {
            if(nbt instanceof NBTTagString) {
                @Nullable final EntityPainting.EnumArt art = getFromTitle(((NBTTagString)nbt).getString(), a -> {});
                if(art != null) instance.setArt(art);
            }
        }

        @Nullable
        static EntityPainting.EnumArt getFromTitle(@Nonnull final String title, @Nonnull final Consumer<EntityPainting.EnumArt> mapper) {
            @Nullable final EntityPainting.EnumArt art = PaintingHelper.PAINTINGS.get(title);
            if(art != null) return art;

            // try remapping
            @Nullable final EntityPainting.EnumArt mapped = JSONHandler.PAINTING_REMAPS.get(title);
            if(mapped != null) {
                mapper.accept(mapped);
                return mapped;
            }

            // alert user of missing mapping
            JSONPaintings.LOGGER.error("Painting with motive: \"" + title + "\" has been removed, resulting in lost data. This can be fixed by assigning it a mapping.");
            return null;
        }
    }
}

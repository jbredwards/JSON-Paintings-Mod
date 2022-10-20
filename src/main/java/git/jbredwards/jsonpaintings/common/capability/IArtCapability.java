package git.jbredwards.jsonpaintings.common.capability;

import git.jbredwards.jsonpaintings.Constants;
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
    @Nonnull ResourceLocation LOCATION = new ResourceLocation(Constants.MODID, "painting");

    @Nullable
    static IArtCapability get(@Nullable ICapabilityProvider provider) {
        return provider != null && provider.hasCapability(CAPABILITY, null)
                ? provider.getCapability(CAPABILITY, null) : null;
    }

    @SubscribeEvent
    static void attach(@Nonnull AttachCapabilitiesEvent<ItemStack> event) {
        if(event.getObject().getItem() == Items.PAINTING) event.addCapability(LOCATION,
                new CapabilityProvider<>(CAPABILITY, new Impl()));
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
                }
            }
        }
    }
}

package git.jbredwards.jsonpaintings.common.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author jbred
 *
 */
public final class CapabilityProvider<T> implements ICapabilitySerializable<NBTBase>
{
    @Nonnull public final Capability<T> capability;
    @Nonnull public final T instance;

    public CapabilityProvider(@Nonnull Capability<T> capability, @Nonnull T instance) {
        this.capability = capability;
        this.instance = instance;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == this.capability;
    }

    @Nullable
    @Override
    public <t> t getCapability(@Nonnull Capability<t> capability, @Nullable EnumFacing facing) {
        return hasCapability(capability, facing) ? this.capability.cast(instance) : null;
    }

    @Nullable
    @Override
    public NBTBase serializeNBT() { return capability.writeNBT(instance, null); }

    @Override
    public void deserializeNBT(@Nonnull NBTBase nbt) { capability.readNBT(instance, null, nbt); }
}

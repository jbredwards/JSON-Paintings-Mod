/*
 * Copyright (c) 2024. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.common.item;

import git.jbredwards.jsonpaintings.mod.common.capability.IArtCapability;
import git.jbredwards.jsonpaintings.mod.common.util.IJSONPainting;
import git.jbredwards.jsonpaintings.mod.common.util.JSONHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemHangingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 *
 * @author jbred
 *
 */
@SuppressWarnings("unused") // used via asm
public class ItemPainting extends ItemHangingEntity
{
    public ItemPainting(@Nonnull final Class<? extends EntityHanging> entityClass) { super(entityClass); }

    @Override
    public void getSubItems(@Nonnull final CreativeTabs tab, @Nonnull final NonNullList<ItemStack> items) {
        if(isInCreativeTab(tab)) {
            items.add(new ItemStack(this));

            //add all painting types to the creative tab
            for(final EntityPainting.EnumArt art : EntityPainting.EnumArt.values()) {
                final ItemStack stack = new ItemStack(this);
                final IArtCapability cap = IArtCapability.get(stack);

                if(cap != null) {
                    cap.setArt(art);
                    items.add(stack);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(@Nonnull final ItemStack stack, @Nullable final World worldIn, @Nonnull final List<String> tooltip, @Nonnull final ITooltipFlag flagIn) {
        @Nullable final IArtCapability cap = IArtCapability.get(stack);
        if(cap != null && cap.hasArt()) tooltip.add(I18n.format("jsonpaintings.itemTooltip", cap.getArt().title, cap.getArt().sizeX >> 4, cap.getArt().sizeY >> 4));
    }

    @Nullable
    @Override
    public String getCreatorModId(@Nonnull final ItemStack itemStack) {
        @Nullable final ResourceLocation location = getRegistryName();
        if(location == null) return null;

        @Nullable final IArtCapability cap = IArtCapability.get(itemStack);
        return cap != null && cap.hasArt() ? JSONHandler.MODID_LOOKUP.getOrDefault(cap.getArt(), ForgeVersion.MOD_ID) : location.getNamespace();
    }

    @Nonnull
    protected IRarity getRarity(@Nonnull final ItemStack stack, @Nonnull final Predicate<IRarity> condition) {
        @Nullable final IArtCapability cap = IArtCapability.get(stack);
        if(cap == null || !cap.hasArt()) return super.getRarity(stack);

        @Nullable final IRarity rarity = IJSONPainting.from(cap.getArt()).getRarity();
        return condition.test(rarity) ? rarity : IJSONPainting.from(cap.getArt()).isCreative() ? EnumRarity.EPIC : EnumRarity.UNCOMMON;
    }

    @Nonnull
    @Override
    public IRarity getForgeRarity(@Nonnull final ItemStack stack) { return getRarity(stack, Objects::nonNull); }

    @Nonnull
    @Override
    public EnumRarity getRarity(@Nonnull final ItemStack stack) { return (EnumRarity)getRarity(stack, rarity -> rarity instanceof EnumRarity); }
}

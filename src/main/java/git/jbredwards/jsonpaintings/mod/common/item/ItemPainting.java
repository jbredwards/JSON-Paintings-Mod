/*
 * Copyright (c) 2025. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.common.item;

import com.mcf.davidee.paintinggui.handler.PlacePaintingEventHandler;
import git.jbredwards.jsonpaintings.api.ActivePaintingInfo;
import git.jbredwards.jsonpaintings.api.PaintingHelper;
import git.jbredwards.jsonpaintings.mod.JSONPaintings;
import git.jbredwards.jsonpaintings.mod.common.capability.IArtCapability;
import git.jbredwards.jsonpaintings.mod.common.util.IJSONPainting;
import io.netty.util.internal.IntegerHolder;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemHangingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jbred
 *
 */
@SuppressWarnings("unused") // used via asm
public class ItemPainting extends ItemHangingEntity
{
    public ItemPainting(@Nonnull final Class<? extends EntityHanging> entityClass) { super(entityClass); }

    @Nullable
    public static EntityPainting.EnumArt getRandomArt(@Nonnull EntityPainting painting, @Nonnull EntityPlayer player, @Nonnull EnumFacing facing) {
        final List<Pair<EntityPainting.EnumArt, Integer>> validArt = new ArrayList<>(EntityPainting.EnumArt.values().length);
        final IntegerHolder maxSize = new IntegerHolder();

        for(EntityPainting.EnumArt art : EntityPainting.EnumArt.values()) {
            if(!player.isCreative() && IJSONPainting.from(art).isCreative()) continue;

            painting.art = art;
            painting.updateFacingWithBoundingBox(facing);

            if(painting.onValidSurface()) {
                final int size = art.sizeX * art.sizeY;
                validArt.add(Pair.of(art, size));
                if(size > maxSize.value) maxSize.value = size;
            }
        }

        validArt.removeIf(pair -> pair.getValue() < maxSize.value);
        return validArt.isEmpty() ? null : validArt.get(painting.world.rand.nextInt(validArt.size())).getKey();
    }

    @Nonnull
    @Override
    public EnumActionResult onItemUse(@Nonnull final EntityPlayer player, @Nonnull final World worldIn, @Nonnull final BlockPos pos, @Nonnull final EnumHand hand, @Nonnull final EnumFacing facing, final float hitX, final float hitY, final float hitZ) {
        @Nonnull final ItemStack stack = player.getHeldItem(hand);
        @Nullable final IArtCapability cap = IArtCapability.get(stack);

        if(cap != null) {
            @Nullable final EntityPainting.EnumArt art = cap.getArt();
            if((art != null || !JSONPaintings.IS_PSG_INSTALLED) && facing.getAxis().isHorizontal()) {
                @Nonnull final BlockPos offset = pos.offset(facing);
                if(player.canPlayerEdit(offset, facing, stack)) {
                    @Nonnull final EntityPainting painting = new EntityPainting(worldIn);
                    painting.setPosition(offset.getX(), offset.getY(), offset.getZ());

                    if(art != null) painting.art = art;
                    else { // get random valid mantle
                        @Nullable final EntityPainting.EnumArt randomArt = getRandomArt(painting, player, facing);
                        if(randomArt != null) painting.art = randomArt;
                        else { // no mantle can be placed
                            return EnumActionResult.PASS;
                        }
                    }

                    painting.updateFacingWithBoundingBox(facing);
                    if(painting.onValidSurface()) {
                        if(!painting.world.isRemote) {
                            if(!player.isCreative()) stack.shrink(1);
                            painting.world.spawnEntity(painting);
                            painting.playPlaceSound();
                        }

                        return EnumActionResult.SUCCESS;
                    }

                    else return EnumActionResult.PASS;
                }
            }
        }

        // Painting Selection GUI's event handler is removed at runtime to prevent bypassing block interactions, this re-implements it
        if(JSONPaintings.IS_PSG_INSTALLED) {
            @Nonnull final PlayerInteractEvent.RightClickBlock event = new PlayerInteractEvent.RightClickBlock(player, hand, pos, facing, new Vec3d(hitX, hitY, hitZ));
            new PlacePaintingEventHandler().onPaintingPlaced(event);
            if(event.isCanceled()) return event.getCancellationResult();
        }

        return EnumActionResult.PASS;
    }

    @Override
    public void getSubItems(@Nonnull final CreativeTabs tab, @Nonnull final NonNullList<ItemStack> items) {
        if(isInCreativeTab(tab)) {
            items.add(new ItemStack(this));
            if(tab != CreativeTabs.SEARCH && tab != PaintingsTab.INSTANCE) return;
            // add all painting types to the creative tab
            for(@Nonnull final EntityPainting.EnumArt art : EntityPainting.EnumArt.values()) {
                items.add(PaintingHelper.write(new ItemStack(this), art));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(@Nonnull final ItemStack stack, @Nullable final World worldIn, @Nonnull final List<String> tooltip, @Nonnull final ITooltipFlag flagIn) {
        @Nullable final EntityPainting.EnumArt art = PaintingHelper.read(stack);
        if(art != null) {
            @Nonnull final ActivePaintingInfo info = ActivePaintingInfo.get(art);

            tooltip.add(PaintingHelper.getTitle(info, art.title).getFormattedText());
            if(info.author != null) tooltip.add(info.author.getFormattedText());

            tooltip.add(TextFormatting.WHITE + I18n.format(JSONPaintings.MODID + ".itemTooltipDim", art.sizeX >> 4, art.sizeY >> 4));
        }
    }

    @Nullable
    @Override
    public String getCreatorModId(@Nonnull final ItemStack stack) {
        @Nullable final ResourceLocation loc = getRegistryName();

        @Nullable final EntityPainting.EnumArt art = PaintingHelper.read(stack);
        if(art == null) return loc == null ? null : loc.getNamespace();

        @Nonnull final String modId = PaintingHelper.getModId(ActivePaintingInfo.get(art));
        return Loader.isModLoaded(modId) ? modId : JSONPaintings.MODID;
    }

    @Nonnull
    @Override
    public IRarity getForgeRarity(@Nonnull final ItemStack stack) {
        @Nullable final EntityPainting.EnumArt art = PaintingHelper.read(stack);
        if(art == null) return super.getRarity(stack);

        @Nullable final IRarity rarity = ActivePaintingInfo.get(art).rarity;
        return rarity != null ? rarity : super.getRarity(stack);
    }

    @Nonnull
    @Override
    public EnumRarity getRarity(@Nonnull final ItemStack stack) {
        @Nonnull final IRarity rarity = getForgeRarity(stack);
        return rarity instanceof EnumRarity ? (EnumRarity)rarity : super.getRarity(stack);
    }

    @Nonnull
    @Override
    public CreativeTabs[] getCreativeTabs() {
        return ArrayUtils.add(super.getCreativeTabs(), PaintingsTab.INSTANCE);
    }
}

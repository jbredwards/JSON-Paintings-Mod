/*
 * Copyright (c) 2026. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.common.item;

import git.jbredwards.jsonpaintings.mod.JSONPaintings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * A creative tab that only contains paintings.
 * @author jbred
 *
 */
public final class PaintingsTab extends CreativeTabs
{
    @Nonnull
    public static final CreativeTabs INSTANCE = new PaintingsTab().setBackgroundImageName("item_search.png");
    private PaintingsTab() { super(JSONPaintings.MODID + ".tab"); }

    @Override
    public boolean hasSearchBar() {
        return true;
    }

    @Nonnull
    @SideOnly(Side.CLIENT)
    @Override
    public ItemStack createIcon() {
        return Items.PAINTING.getDefaultInstance();
    }
}

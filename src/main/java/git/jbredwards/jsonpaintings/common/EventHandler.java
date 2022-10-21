package git.jbredwards.jsonpaintings.common;

import git.jbredwards.jsonpaintings.Main;
import git.jbredwards.jsonpaintings.common.capability.IArtCapability;
import git.jbredwards.jsonpaintings.common.util.IJSONPainting;
import io.netty.util.internal.IntegerHolder;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
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
public final class EventHandler
{
    @SubscribeEvent(priority = EventPriority.HIGH)
    static void handlePlacement(@Nonnull PlayerInteractEvent.RightClickBlock event) {
        final ItemStack stack = event.getItemStack();
        final @Nullable IArtCapability cap = IArtCapability.get(stack);

        if(cap != null && (!Main.IS_PSG_INSTALLED || cap.hasArt())) {
            final @Nullable EnumFacing facing = event.getFace();
            if(facing != null && facing.getAxis().isHorizontal()) {
                final BlockPos offset = event.getPos().offset(facing);
                final EntityPlayer player = event.getEntityPlayer();

                if(player.canPlayerEdit(offset, facing, stack)) {
                    final EntityPainting painting = new EntityPainting(event.getWorld());
                    painting.setPosition(offset.getX(), offset.getY(), offset.getZ());
                    painting.art = cap.hasArt() ? cap.getArt() : getRandomArt(painting, player, facing);
                    painting.updateFacingWithBoundingBox(facing);
                    if(painting.onValidSurface()) {
                        if(!painting.world.isRemote) {
                            if(!player.isCreative()) stack.shrink(1);
                            painting.world.spawnEntity(painting);
                            painting.playPlaceSound();
                        }

                        event.setCancellationResult(EnumActionResult.SUCCESS);
                    }

                    else event.setCancellationResult(EnumActionResult.FAIL);
                    event.setCanceled(true);
                }
            }
        }
    }

    @Nonnull
    public static EntityPainting.EnumArt getRandomArt(@Nonnull EntityPainting painting, @Nonnull EntityPlayer player, @Nonnull EnumFacing facing) {
        final List<Pair<EntityPainting.EnumArt, Integer>> validArt = new ArrayList<>();
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

        //this would be wack
        if(validArt.size() == 0) throw new IllegalStateException(
                "Attempted to place painting entity with no valid art values! " +
                "Please ensure there is at least one 1x1 art type with \"isCreative\" set to false."
        );

        validArt.removeIf(pair -> pair.getValue() < maxSize.value);
        return validArt.get(painting.world.rand.nextInt(validArt.size())).getKey();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGH)
    static void handleTooltip(@Nonnull ItemTooltipEvent event) {
        final ItemStack stack = event.getItemStack();
        final @Nullable IArtCapability cap = IArtCapability.get(stack);
        if(cap != null && cap.hasArt())
            event.getToolTip().add(1, I18n.format("jsonpaintings.itemTooltip", cap.getArt().title));
    }
}

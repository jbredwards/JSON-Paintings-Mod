package git.jbredwards.jsonpaintings.asm.transformer;

import git.jbredwards.jsonpaintings.common.util.IJSONPainting;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 *
 * @author jbred
 *
 */
@SuppressWarnings("unused")
public final class PSGRevampedServerTransformer implements IClassTransformer, Opcodes
{
    @Nonnull
    @Override
    public byte[] transform(@Nonnull String name, @Nonnull String transformedName, @Nonnull byte[] basicClass) {
        if("com.mcf.davidee.paintinggui.handler.PlacePaintingEventHandler".equals(transformedName)) {
            final ClassNode classNode = new ClassNode();
            new ClassReader(basicClass).accept(classNode, 0);

            methods:
            for(MethodNode method : classNode.methods) {
                /*
                 * onPaintingPlaced:
                 * Old code:
                 * for(EnumArt art : EnumArt.values())
                 * {
                 *     ...
                 * }
                 *
                 * New code:
                 * //don't allow creative paintings to appear in the selection menu for survival players
                 * for(EnumArt art : Hooks.values(player))
                 * {
                 *     ...
                 * }
                 */
                if(method.name.equals("onPaintingPlaced")) {
                    for(AbstractInsnNode insn : method.instructions.toArray()) {
                        if(insn.getOpcode() == INVOKESTATIC && ((MethodInsnNode)insn).name.equals("values")) {
                            method.instructions.insertBefore(insn, new VarInsnNode(ALOAD, 3));
                            ((MethodInsnNode)insn).owner = "git/jbredwards/jsonpaintings/asm/transformer/PSGRevampedServerTransformer$Hooks";
                            ((MethodInsnNode)insn).desc = "(Lnet/minecraft/entity/player/EntityPlayer;)[Lnet/minecraft/entity/item/EntityPainting$EnumArt;";
                            break methods;
                        }
                    }
                }
            }

            //writes the changes
            final ClassWriter writer = new ClassWriter(0);
            classNode.accept(writer);
            return writer.toByteArray();
        }

        return basicClass;
    }

    public static final class Hooks
    {
        @Nonnull
        public static EntityPainting.EnumArt[] values(@Nonnull EntityPlayer player) {
            return player.isCreative() ? EntityPainting.EnumArt.values()
                    : Arrays.stream(EntityPainting.EnumArt.values())
                            .filter(art -> !IJSONPainting.from(art).isCreative())
                            .toArray(EntityPainting.EnumArt[]::new);
        }
    }
}

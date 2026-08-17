/*
 * Copyright (c) 2026. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.asm.transformer;

import git.jbredwards.jsonpaintings.api.PaintingHelper;
import git.jbredwards.jsonpaintings.mod.asm.AccessorHwyla;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.overlay.Tooltip;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import javax.annotation.Nonnull;
import java.util.ListIterator;

/**
 *
 * @author jbred
 *
 */
public final class HwylaTransformer implements IClassTransformer, Opcodes
{
    @Nonnull
    @Override
    public byte[] transform(@Nonnull final String name, @Nonnull final String transformedName, @Nonnull final byte[] basicClass) {
        if("mcp.mobius.waila.overlay.Tooltip".equals(transformedName)) {
            final ClassNode classNode = new ClassNode();
            new ClassReader(basicClass).accept(classNode, 0);
            //implement interface
            classNode.interfaces.add("git/jbredwards/jsonpaintings/mod/asm/AccessorHwyla");
            /*
             * @ASMGenerated
             * public void jsonpaintings$computePositionAndSize(boolean hasIcon)
             * {
             *     return this.computePositionAndSize(hasIcon);
             * }
             */
            final MethodNode recompute = new MethodNode(ACC_PUBLIC, "jsonpaintings$computePositionAndSize", "(Z)V", null, null);
            recompute.visitVarInsn(ALOAD, 0);
            recompute.visitVarInsn(ILOAD, 1);
            recompute.visitMethodInsn(INVOKEVIRTUAL, classNode.name, "computePositionAndSize", "(Z)V", false);
            recompute.visitInsn(RETURN);
            recompute.visitMaxs(2, 3);
            classNode.methods.add(recompute);
            /*
             * @ASMGenerated
             * public void jsonpaintings$stack(ItemStack stack)
             * {
             *     this.stack = stack;
             * }
             */
            final MethodNode set = new MethodNode(ACC_PUBLIC, "jsonpaintings$stack", "(Lnet/minecraft/item/ItemStack;)V", null, null);
            set.visitVarInsn(ALOAD, 0);
            set.visitVarInsn(ALOAD, 1);
            set.visitFieldInsn(PUTFIELD, classNode.name, "stack", "Lnet/minecraft/item/ItemStack;");
            set.visitInsn(RETURN);
            set.visitMaxs(2, 3);
            classNode.methods.add(set);

            //writes the changes
            final ClassWriter writer = new ClassWriter(0);
            classNode.accept(writer);
            return writer.toByteArray();
        }
        else if("mcp.mobius.waila.overlay.WailaTickHandler".equals(transformedName)) {
            final ClassNode classNode = new ClassNode();
            new ClassReader(basicClass).accept(classNode, 0);
            methods:
            for(@Nonnull final MethodNode method : classNode.methods) {
                if(method.name.equals("tickClient")) {
                    for(@Nonnull final ListIterator<AbstractInsnNode> it = method.instructions.iterator(); it.hasNext();) {
                        @Nonnull final AbstractInsnNode insn = it.next();
                        if(insn.getOpcode() == INVOKESPECIAL
                        && ((MethodInsnNode)insn).owner.equals("mcp/mobius/waila/overlay/Tooltip")
                        && ((MethodInsnNode)insn).desc.equals("(Ljava/util/List;Z)V")) {
                            it.add(new VarInsnNode(ALOAD, 10));
                            it.add(new MethodInsnNode(INVOKESTATIC, "git/jbredwards/jsonpaintings/mod/asm/transformer/HwylaTransformer$Hooks", "applyPainting", "(Lmcp/mobius/waila/overlay/Tooltip;Lnet/minecraft/entity/Entity;)Lmcp/mobius/waila/overlay/Tooltip;", false));
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

    @SuppressWarnings("unused")
    public static final class Hooks
    {
        @Nonnull
        public static Tooltip applyPainting(@Nonnull final Tooltip tooltip, @Nonnull final Entity entity) {
            if(entity instanceof EntityPainting && ConfigHandler.instance().showItem()) {
                ((AccessorHwyla)tooltip).jsonpaintings$computePositionAndSize(true);
                ((AccessorHwyla)tooltip).jsonpaintings$stack(PaintingHelper.write(new ItemStack(Items.PAINTING), ((EntityPainting)entity).art));
            }

            return tooltip;
        }
    }
}

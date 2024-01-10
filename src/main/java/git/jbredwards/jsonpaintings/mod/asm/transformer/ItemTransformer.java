/*
 * Copyright (c) 2024. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.asm.transformer;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import javax.annotation.Nonnull;

/**
 *
 * @author jbred
 *
 */
@SuppressWarnings("unused")
public final class ItemTransformer implements IClassTransformer, Opcodes
{
    @Nonnull
    @Override
    public byte[] transform(@Nonnull String name, @Nonnull String transformedName, @Nonnull byte[] basicClass) {
        if("net.minecraft.item.Item".equals(transformedName)) {
            final ClassNode classNode = new ClassNode();
            new ClassReader(basicClass).accept(classNode, ClassReader.SKIP_FRAMES);

            methods:
            for(MethodNode method : classNode.methods) {
                /*
                 * registerItems: (changes are around line 1740)
                 * Old code:
                 * registerItem(321, "painting", (new ItemHangingEntity(EntityPainting.class)).setTranslationKey("painting"));
                 *
                 * New code:
                 * //new painting entity class
                 * registerItem(321, "painting", (new ItemPainting(EntityPainting.class)).setTranslationKey("painting"));
                 */
                if(method.name.equals(FMLLaunchHandler.isDeobfuscatedEnvironment() ? "registerItems" : "func_150900_l")) {
                    for(final AbstractInsnNode insn : method.instructions.toArray()) {
                        if(insn.getOpcode() == NEW && ((TypeInsnNode)insn).desc.equals("net/minecraft/item/ItemHangingEntity"))
                            ((TypeInsnNode)insn).desc = "git/jbredwards/jsonpaintings/mod/common/item/ItemPainting";

                        else if(insn.getOpcode() == INVOKESPECIAL && ((MethodInsnNode)insn).owner.equals("net/minecraft/item/ItemHangingEntity")) {
                            ((MethodInsnNode)insn).owner = "git/jbredwards/jsonpaintings/mod/common/item/ItemPainting";
                            break methods;
                        }
                    }
                }
            }

            //writes the changes
            final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(writer);
            return writer.toByteArray();
        }

        return basicClass;
    }
}

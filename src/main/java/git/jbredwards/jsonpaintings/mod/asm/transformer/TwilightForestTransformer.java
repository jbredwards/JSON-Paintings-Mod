/*
 * Copyright (c) 2024. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.asm.transformer;

import git.jbredwards.jsonpaintings.mod.common.util.IJSONPainting;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * Don't allow creative paintings to appear in twilight forest dungeons
 * @author jbred
 *
 */
@SuppressWarnings("unused")
public final class TwilightForestTransformer implements IClassTransformer, Opcodes
{
    @Nonnull
    @Override
    public byte[] transform(@Nonnull String name, @Nonnull String transformedName, @Nonnull byte[] basicClass) {
        if("twilightforest.structures.lichtower.ComponentTFTowerWing".equals(transformedName)) {
            final ClassNode classNode = new ClassNode();
            new ClassReader(basicClass).accept(classNode, 0);

            methods:
            for(MethodNode method : classNode.methods) {
                /*
                 * getPaintingOfSize:
                 * Old code:
                 * for(EnumArt art : EnumArt.values())
                 * {
                 *     ...
                 * }
                 *
                 * New code:
                 * // Don't allow creative paintings to appear in twilight forest dungeons
                 * for(EnumArt art : Hooks.values(player))
                 * {
                 *     ...
                 * }
                 */
                if(method.name.equals("getPaintingOfSize")) {
                    for(final AbstractInsnNode insn : method.instructions.toArray()) {
                        if(insn.getOpcode() == INVOKESTATIC && ((MethodInsnNode)insn).name.equals("values")) {
                            method.instructions.insertBefore(insn, new VarInsnNode(ALOAD, 3));
                            ((MethodInsnNode)insn).owner = "git/jbredwards/jsonpaintings/mod/asm/transformer/TwilightForestTransformer$Hooks";
                            ((MethodInsnNode)insn).desc = "()[Lnet/minecraft/entity/item/EntityPainting$EnumArt;";
                            break methods;
                        }
                    }
                }

                //writes the changes
                final ClassWriter writer = new ClassWriter(0);
                classNode.accept(writer);
                return writer.toByteArray();
            }
        }

        return basicClass;
    }

    public static final class Hooks
    {
        @Nonnull
        public static EntityPainting.EnumArt[] values() {
            return Arrays.stream(EntityPainting.EnumArt.values()).filter(art -> !IJSONPainting.from(art).isCreative()).toArray(EntityPainting.EnumArt[]::new);
        }
    }
}

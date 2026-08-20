/*
 * Copyright (c) 2026. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.asm.transformer;

import git.jbredwards.jsonpaintings.api.ActivePaintingInfo;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author jbred
 *
 */
public final class EnumHelperTransformer implements IClassTransformer, Opcodes
{
    @Nonnull
    @Override
    public byte[] transform(@Nonnull final String name, @Nonnull final String transformedName, @Nonnull final byte[] basicClass) {
        if("net.minecraftforge.common.util.EnumHelper".equals(transformedName)) {
            @Nonnull final ClassNode classNode = new ClassNode();
            new ClassReader(basicClass).accept(classNode, ClassReader.SKIP_FRAMES);
            for(@Nonnull final MethodNode method : classNode.methods) {
                if(method.name.equals("addArt")) {
                    @Nonnull final LabelNode start = new LabelNode(), end = new LabelNode();
                    @Nonnull final LocalVariableNode existing = new LocalVariableNode(
                            "jsonpaintings$existing", "Lnet/minecraft/entity/item/EntityPainting$EnumArt;", null, start, end,
                            method.localVariables.stream().mapToInt(local -> local.index).max().orElse(-1) + 1);

                    method.localVariables.add(existing);
                    method.instructions.insert(method.instructions.getFirst(), start);
                    method.instructions.add(end);

                    @Nonnull final InsnList list = new InsnList();
                    list.add(new FieldInsnNode(GETSTATIC, "git/jbredwards/jsonpaintings/api/PaintingHelper", "PAINTINGS", "Ljava/util/Map;"));
                    list.add(new VarInsnNode(ALOAD, 1));
                    list.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true));
                    list.add(new TypeInsnNode(CHECKCAST, "net/minecraft/entity/item/EntityPainting$EnumArt"));
                    list.add(new VarInsnNode(ASTORE, existing.index));
                    list.add(new VarInsnNode(ALOAD, existing.index));
                    list.add(new MethodInsnNode(INVOKESTATIC, "git/jbredwards/jsonpaintings/mod/asm/transformer/EnumHelperTransformer$Hooks", "applyNewMod", "(Lnet/minecraft/entity/item/EntityPainting$EnumArt;)V", false));

                    @Nonnull final LabelNode label = new LabelNode();
                    list.add(new VarInsnNode(ALOAD, existing.index));
                    list.add(new JumpInsnNode(IFNULL, label));
                    list.add(new VarInsnNode(ALOAD, existing.index));
                    list.add(new InsnNode(ARETURN));

                    list.add(label);
                    list.add(new FrameNode(F_SAME, 0, null, 0, null));
                    method.instructions.insert(start, list);
                    break;
                }
            }

            //writes the changes
            @Nonnull final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(writer);
            return writer.toByteArray();
        }

        return basicClass;
    }

    @SuppressWarnings("unused")
    public static final class Hooks
    {
        public static void applyNewMod(@Nullable final EntityPainting.EnumArt art) {
            if(art != null) {
                @Nullable final ModContainer container = Loader.instance().activeModContainer();
                if(container != null) {
                    ActivePaintingInfo.get(art).modId = container.getModId();
                    ActivePaintingInfo.get(art).modName = container.getName();
                }
            }
        }
    }
}

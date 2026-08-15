/*
 * Copyright (c) 2025. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.asm.transformer;

import net.minecraft.launchwrapper.IClassTransformer;
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
public final class EnumArtTransformer implements IClassTransformer, Opcodes
{
    @Nonnull
    @Override
    public byte[] transform(@Nonnull String name, @Nonnull String transformedName, @Nonnull byte[] basicClass) {
        if("net.minecraft.entity.item.EntityPainting$EnumArt".equals(transformedName)) {
            final ClassNode classNode = new ClassNode();
            new ClassReader(basicClass).accept(classNode, 0);
            //implement interface
            classNode.interfaces.add("git/jbredwards/jsonpaintings/mod/asm/AccessorEnumArt");
            classNode.interfaces.add("git/jbredwards/jsonpaintings/mod/common/util/IJSONPainting");
            //add new fields
            classNode.fields.add(new FieldNode(ACC_PUBLIC, "jsonpaintings$info", "Lgit/jbredwards/jsonpaintings/api/ActivePaintingInfo;", null, null));
            /*
             * @ASMGenerated
             * public PaintingInfo jsonpaintings$info()
             * {
             *     return this.jsonpaintings$info;
             * }
             */
            final MethodNode get = new MethodNode(ACC_PUBLIC, "jsonpaintings$info", "()Lgit/jbredwards/jsonpaintings/api/ActivePaintingInfo;", null, null);
            get.visitVarInsn(ALOAD, 0);
            get.visitFieldInsn(GETFIELD, "net/minecraft/entity/item/EntityPainting$EnumArt", "jsonpaintings$info", "Lgit/jbredwards/jsonpaintings/api/ActivePaintingInfo;");
            get.visitInsn(ARETURN);
            get.visitMaxs(1, 2);
            classNode.methods.add(get);
            /*
             * @ASMGenerated
             * public void jsonpaintings$info(PaintingInfo info)
             * {
             *     this.jsonpaintings$info = info;
             * }
             */
            final MethodNode set = new MethodNode(ACC_PUBLIC, "jsonpaintings$info", "(Lgit/jbredwards/jsonpaintings/api/ActivePaintingInfo;)V", null, null);
            set.visitVarInsn(ALOAD, 0);
            set.visitVarInsn(ALOAD, 1);
            set.visitFieldInsn(PUTFIELD, "net/minecraft/entity/item/EntityPainting$EnumArt", "jsonpaintings$info", "Lgit/jbredwards/jsonpaintings/api/ActivePaintingInfo;");
            set.visitInsn(RETURN);
            set.visitMaxs(2, 3);
            classNode.methods.add(set);

            //writes the changes
            final ClassWriter writer = new ClassWriter(0);
            classNode.accept(writer);
            return writer.toByteArray();
        }

        return basicClass;
    }
}

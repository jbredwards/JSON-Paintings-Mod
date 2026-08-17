/*
 * Copyright (c) 2025. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.asm.transformer;

import git.jbredwards.jsonpaintings.api.ActivePaintingInfo;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.Map;

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
            methods:
            for(@Nonnull final MethodNode method : classNode.methods) {
                /*
                 * <init>:
                 * Old code:
                 * {
                 *     ...
                 * }
                 *
                 * New code:
                 * // Store invoking mod for paintings.
                 * {
                 *     ...
                 *     Hooks.addMod(this);
                 * }
                 */
                if(method.name.equals("<init>")) {
                    for(@Nonnull final ListIterator<AbstractInsnNode> it = method.instructions.iterator(); it.hasNext();) {
                        if(it.next().getOpcode() == RETURN) {
                            it.previous(); // Insert before return statement.
                            it.add(new VarInsnNode(ALOAD, 0));
                            it.add(new MethodInsnNode(INVOKESTATIC, "git/jbredwards/jsonpaintings/mod/asm/transformer/EnumArtTransformer$Hooks", "applyMod", "(Lnet/minecraft/entity/item/EntityPainting$EnumArt;)V", false));
                            break methods;
                        }
                    }
                }
            }
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

    @SuppressWarnings("unused")
    public static final class Hooks
    {
        public static void applyMod(@Nonnull final EntityPainting.EnumArt painting) {
            PAINTINGS.put(painting.title, painting);
            if(painting.ordinal() < 26) { // Set default info for Vanilla's paintings.
                @Nonnull final ActivePaintingInfo info = ActivePaintingInfo.get(painting);
                if(painting.ordinal() != 19) info.author = new TextComponentString(TextFormatting.GRAY + "Kristoffer Zetterstrand");
                info.modId = "minecraft";
                info.modName = "Minecraft";
                return;
            }

            @Nullable final ModContainer mod = Loader.instance().activeModContainer();
            if(mod == null) return; // No active container? skipping...
            @Nonnull final ActivePaintingInfo info = ActivePaintingInfo.get(painting);
            info.modId = mod.getModId();
            info.modName = mod.getName();
        }

        // Helper.
        @Nonnull private static final Map<String, EntityPainting.EnumArt> PAINTINGS = new LinkedHashMap<>();
        @Nonnull public static Map<String, EntityPainting.EnumArt> paintings() { return Collections.unmodifiableMap(PAINTINGS); }
    }
}

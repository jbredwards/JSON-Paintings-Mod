/*
 * Copyright (c) 2024. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.asm.transformer;

import git.jbredwards.jsonpaintings.mod.common.util.IJSONPainting;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.util.ResourceLocation;
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
public final class PSGRevampedClientTransformer implements IClassTransformer, Opcodes
{
    @Nonnull
    @Override
    public byte[] transform(@Nonnull String name, @Nonnull String transformedName, @Nonnull byte[] basicClass) {
        if("com.mcf.davidee.paintinggui.gui.PaintingButton".equals(transformedName)) {
            final ClassNode classNode = new ClassNode();
            new ClassReader(basicClass).accept(classNode, 0);

            //paintings++ has a class with the same name and location (but different code)
            //this is changed to true once it's determined that this class is from the right mod, not paintings++
            boolean isValidTransformer = false;

            methods:
            for(MethodNode method : classNode.methods) {
                /*
                 * draw:
                 * Old code:
                 * mc.renderEngine.bindTexture(TEXTURE);
                 *
                 * New code:
                 * //account for custom textures
                 * mc.renderEngine.bindTexture(Hooks.getTexture(this.art, TEXTURE));
                 */
                if(method.name.equals("draw")) {
                    for(AbstractInsnNode insn : method.instructions.toArray()) {
                        if(insn.getOpcode() == GETSTATIC && ((FieldInsnNode)insn).name.equals("TEXTURE")) {
                            method.instructions.insertBefore(insn, new VarInsnNode(ALOAD, 0));
                            method.instructions.insertBefore(insn, new FieldInsnNode(GETFIELD, "com/mcf/davidee/paintinggui/gui/PaintingButton", "art", "Lnet/minecraft/entity/item/EntityPainting$EnumArt;"));
                            method.instructions.insert(insn, new MethodInsnNode(INVOKESTATIC, "git/jbredwards/jsonpaintings/mod/asm/transformer/PSGRevampedClientTransformer$Hooks", "getTexture", "(Lnet/minecraft/entity/item/EntityPainting$EnumArt;Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/util/ResourceLocation;", false));
                            isValidTransformer = true;
                            break;
                        }
                    }
                }
                /*
                 * drawTexturedModalRect:
                 * Old code:
                 * double f = 1d /(double)KZ_WIDTH;
		         * double f1 =1d /(double)KZ_HEIGHT;
		         *
		         * New code:
		         * //account for custom width & height
                 * double f = 1d /(double)Hooks.getWidth(this.art, KZ_WIDTH);
                 * double f1 =1d /(double)Hooks.getHeight(this.art, KZ_HEIGHT);
                 */
                else if(isValidTransformer && method.name.equals(FMLLaunchHandler.isDeobfuscatedEnvironment() ? "drawTexturedModalRect" : "func_73729_b")) {
                    for(final AbstractInsnNode insn : method.instructions.toArray()) {
                        if(insn.getOpcode() == GETSTATIC && ((FieldInsnNode)insn).name.equals("KZ_WIDTH")) {
                            method.instructions.insertBefore(insn, new VarInsnNode(ALOAD, 0));
                            method.instructions.insertBefore(insn, new FieldInsnNode(GETFIELD, "com/mcf/davidee/paintinggui/gui/PaintingButton", "art", "Lnet/minecraft/entity/item/EntityPainting$EnumArt;"));
                            method.instructions.insert(insn, new MethodInsnNode(INVOKESTATIC, "git/jbredwards/jsonpaintings/mod/asm/transformer/PSGRevampedClientTransformer$Hooks", "getWidth", "(Lnet/minecraft/entity/item/EntityPainting$EnumArt;I)I", false));
                        }
                        else if(insn.getOpcode() == GETSTATIC && ((FieldInsnNode)insn).name.equals("KZ_HEIGHT")) {
                            method.instructions.insertBefore(insn, new VarInsnNode(ALOAD, 0));
                            method.instructions.insertBefore(insn, new FieldInsnNode(GETFIELD, "com/mcf/davidee/paintinggui/gui/PaintingButton", "art", "Lnet/minecraft/entity/item/EntityPainting$EnumArt;"));
                            method.instructions.insert(insn, new MethodInsnNode(INVOKESTATIC, "git/jbredwards/jsonpaintings/mod/asm/transformer/PSGRevampedClientTransformer$Hooks", "getHeight", "(Lnet/minecraft/entity/item/EntityPainting$EnumArt;I)I", false));
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
        public static ResourceLocation getTexture(@Nonnull EntityPainting.EnumArt art, @Nonnull ResourceLocation defaultTexture) {
            final IJSONPainting painting = IJSONPainting.from(art);
            return painting.useSpecialRenderer() ? painting.getFrontTexture() : defaultTexture;
        }

        public static int getWidth(@Nonnull EntityPainting.EnumArt art, int defaultWidth) {
            return IJSONPainting.from(art).useSpecialRenderer() ? art.sizeX : defaultWidth;
        }

        public static int getHeight(@Nonnull EntityPainting.EnumArt art, int defaultHeight) {
            return IJSONPainting.from(art).useSpecialRenderer() ? art.sizeY : defaultHeight;
        }
    }
}

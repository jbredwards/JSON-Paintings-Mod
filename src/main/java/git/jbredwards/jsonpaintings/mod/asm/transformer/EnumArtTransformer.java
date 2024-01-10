/*
 * Copyright (c) 2024. jbredwards
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
            classNode.interfaces.add("git/jbredwards/jsonpaintings/mod/common/util/IJSONPainting");
            //add new fields
            classNode.fields.add(new FieldNode(ACC_PRIVATE, "frontTexture", "Lnet/minecraft/util/ResourceLocation;", null, null));
            classNode.fields.add(new FieldNode(ACC_PRIVATE, "backTexture", "Lnet/minecraft/util/ResourceLocation;", null, null));
            classNode.fields.add(new FieldNode(ACC_PRIVATE, "sideTexture", "Lnet/minecraft/util/ResourceLocation;", null, null));
            classNode.fields.add(new FieldNode(ACC_PRIVATE, "modName", "Ljava/lang/String;", null, null));
            classNode.fields.add(new FieldNode(ACC_PRIVATE, "rarity", "Lnet/minecraftforge/common/IRarity;", null, null));
            classNode.fields.add(new FieldNode(ACC_PRIVATE, "backOffsetX", "I", null, 0));
            classNode.fields.add(new FieldNode(ACC_PRIVATE, "backOffsetY", "I", null, 0));
            classNode.fields.add(new FieldNode(ACC_PRIVATE, "sideOffsetX", "I", null, 0));
            classNode.fields.add(new FieldNode(ACC_PRIVATE, "sideOffsetY", "I", null, 0));
            classNode.fields.add(new FieldNode(ACC_PRIVATE, "isCreative", "Z", null, false));
            classNode.fields.add(new FieldNode(ACC_PRIVATE, "alwaysCapture", "Z", null, false));
            classNode.fields.add(new FieldNode(ACC_PRIVATE, "hasBackTexture", "Z", null, false));
            classNode.fields.add(new FieldNode(ACC_PRIVATE, "hasSideTexture", "Z", null, false));
            classNode.fields.add(new FieldNode(ACC_PRIVATE, "useSpecialRenderer", "Z", null, false));
            /*
             * @ASMGenerated
             * public ResourceLocation getFrontTexture()
             * {
             *     return this.frontTexture;
             * }
             */
            final MethodNode getTexture = new MethodNode(ACC_PUBLIC, "getFrontTexture", "()Lnet/minecraft/util/ResourceLocation;", null, null);
            getTexture.visitVarInsn(ALOAD, 0);
            getTexture.visitFieldInsn(GETFIELD, "net/minecraft/entity/item/EntityPainting$EnumArt", "frontTexture", "Lnet/minecraft/util/ResourceLocation;");
            getTexture.visitInsn(ARETURN);
            getTexture.visitMaxs(1, 2);
            classNode.methods.add(getTexture);
            /*
             * @ASMGenerated
             * public void setFrontTexture(ResourceLocation texture)
             * {
             *     this.frontTexture = texture;
             * }
             */
            final MethodNode setTexture = new MethodNode(ACC_PUBLIC, "setFrontTexture", "(Lnet/minecraft/util/ResourceLocation;)V", null, null);
            setTexture.visitVarInsn(ALOAD, 0);
            setTexture.visitVarInsn(ALOAD, 1);
            setTexture.visitFieldInsn(PUTFIELD, "net/minecraft/entity/item/EntityPainting$EnumArt", "frontTexture", "Lnet/minecraft/util/ResourceLocation;");
            setTexture.visitInsn(RETURN);
            setTexture.visitMaxs(2, 3);
            classNode.methods.add(setTexture);
            /*
             * @ASMGenerated
             * public ResourceLocation getBackTexture()
             * {
             *     return this.backTexture;
             * }
             */
            final MethodNode getBackTexture = new MethodNode(ACC_PUBLIC, "getBackTexture", "()Lnet/minecraft/util/ResourceLocation;", null, null);
            getBackTexture.visitVarInsn(ALOAD, 0);
            getBackTexture.visitFieldInsn(GETFIELD, "net/minecraft/entity/item/EntityPainting$EnumArt", "backTexture", "Lnet/minecraft/util/ResourceLocation;");
            getBackTexture.visitInsn(ARETURN);
            getBackTexture.visitMaxs(1, 2);
            classNode.methods.add(getBackTexture);
            /*
             * @ASMGenerated
             * public void setBackTexture(ResourceLocation texture)
             * {
             *     this.backTexture = texture;
             * }
             */
            final MethodNode setBackTexture = new MethodNode(ACC_PUBLIC, "setBackTexture", "(Lnet/minecraft/util/ResourceLocation;)V", null, null);
            setBackTexture.visitVarInsn(ALOAD, 0);
            setBackTexture.visitVarInsn(ALOAD, 1);
            setBackTexture.visitFieldInsn(PUTFIELD, "net/minecraft/entity/item/EntityPainting$EnumArt", "backTexture", "Lnet/minecraft/util/ResourceLocation;");
            setBackTexture.visitInsn(RETURN);
            setBackTexture.visitMaxs(2, 3);
            classNode.methods.add(setBackTexture);
            /*
             * @ASMGenerated
             * public ResourceLocation getSideTexture()
             * {
             *     return this.sideTexture;
             * }
             */
            final MethodNode getSideTexture = new MethodNode(ACC_PUBLIC, "getSideTexture", "()Lnet/minecraft/util/ResourceLocation;", null, null);
            getSideTexture.visitVarInsn(ALOAD, 0);
            getSideTexture.visitFieldInsn(GETFIELD, "net/minecraft/entity/item/EntityPainting$EnumArt", "sideTexture", "Lnet/minecraft/util/ResourceLocation;");
            getSideTexture.visitInsn(ARETURN);
            getSideTexture.visitMaxs(1, 2);
            classNode.methods.add(getSideTexture);
            /*
             * @ASMGenerated
             * public void setSideTexture(ResourceLocation texture)
             * {
             *     this.sideTexture = texture;
             * }
             */
            final MethodNode setSideTexture = new MethodNode(ACC_PUBLIC, "setSideTexture", "(Lnet/minecraft/util/ResourceLocation;)V", null, null);
            setSideTexture.visitVarInsn(ALOAD, 0);
            setSideTexture.visitVarInsn(ALOAD, 1);
            setSideTexture.visitFieldInsn(PUTFIELD, "net/minecraft/entity/item/EntityPainting$EnumArt", "sideTexture", "Lnet/minecraft/util/ResourceLocation;");
            setSideTexture.visitInsn(RETURN);
            setSideTexture.visitMaxs(2, 3);
            classNode.methods.add(setSideTexture);
            /*
             * @ASMGenerated
             * public String getModName()
             * {
             *     return this.modName;
             * }
             */
            final MethodNode getModName = new MethodNode(ACC_PUBLIC, "getModName", "()Ljava/lang/String;", null, null);
            getModName.visitVarInsn(ALOAD, 0);
            getModName.visitFieldInsn(GETFIELD, "net/minecraft/entity/item/EntityPainting$EnumArt", "modName", "Ljava/lang/String;");
            getModName.visitInsn(ARETURN);
            getModName.visitMaxs(1, 2);
            classNode.methods.add(getModName);
            /*
             * @ASMGenerated
             * public void setModName(String modName)
             * {
             *     this.modName = modName;
             * }
             */
            final MethodNode setModName = new MethodNode(ACC_PUBLIC, "setModName", "(Ljava/lang/String;)V", null, null);
            setModName.visitVarInsn(ALOAD, 0);
            setModName.visitVarInsn(ALOAD, 1);
            setModName.visitFieldInsn(PUTFIELD, "net/minecraft/entity/item/EntityPainting$EnumArt", "modName", "Ljava/lang/String;");
            setModName.visitInsn(RETURN);
            setModName.visitMaxs(2, 3);
            classNode.methods.add(setModName);
            /*
             * @ASMGenerated
             * public IRarity getRarity()
             * {
             *     return this.rarity;
             * }
             */
            final MethodNode getRarity = new MethodNode(ACC_PUBLIC, "getRarity", "()Lnet/minecraftforge/common/IRarity;", null, null);
            getRarity.visitVarInsn(ALOAD, 0);
            getRarity.visitFieldInsn(GETFIELD, "net/minecraft/entity/item/EntityPainting$EnumArt", "rarity", "Lnet/minecraftforge/common/IRarity;");
            getRarity.visitInsn(ARETURN);
            getRarity.visitMaxs(1, 2);
            classNode.methods.add(getRarity);
            /*
             * @ASMGenerated
             * public void setRarity(IRarity rarity)
             * {
             *     this.rarity = rarity;
             * }
             */
            final MethodNode setRarity = new MethodNode(ACC_PUBLIC, "setRarity", "(Lnet/minecraftforge/common/IRarity;)V", null, null);
            setRarity.visitVarInsn(ALOAD, 0);
            setRarity.visitVarInsn(ALOAD, 1);
            setRarity.visitFieldInsn(PUTFIELD, "net/minecraft/entity/item/EntityPainting$EnumArt", "rarity", "Lnet/minecraftforge/common/IRarity;");
            setRarity.visitInsn(RETURN);
            setRarity.visitMaxs(2, 3);
            classNode.methods.add(setRarity);
            /*
             * @ASMGenerated
             * public boolean isCreative()
             * {
             *     return this.isCreative;
             * }
             */
            final MethodNode isCreative = new MethodNode(ACC_PUBLIC, "isCreative", "()Z", null, null);
            isCreative.visitVarInsn(ALOAD, 0);
            isCreative.visitFieldInsn(GETFIELD, "net/minecraft/entity/item/EntityPainting$EnumArt", "isCreative", "Z");
            isCreative.visitInsn(IRETURN);
            isCreative.visitMaxs(1, 2);
            classNode.methods.add(isCreative);
            /*
             * @ASMGenerated
             * public void setCreative(boolean isCreative)
             * {
             *     this.isCreative = isCreative;
             * }
             */
            final MethodNode setCreative = new MethodNode(ACC_PUBLIC, "setCreative", "(Z)V", null, null);
            setCreative.visitVarInsn(ALOAD, 0);
            setCreative.visitVarInsn(ILOAD, 1);
            setCreative.visitFieldInsn(PUTFIELD, "net/minecraft/entity/item/EntityPainting$EnumArt", "isCreative", "Z");
            setCreative.visitInsn(RETURN);
            setCreative.visitMaxs(2, 3);
            classNode.methods.add(setCreative);
            /*
             * @ASMGenerated
             * public boolean alwaysCapture()
             * {
             *     return this.alwaysCapture;
             * }
             */
            final MethodNode alwaysCapture = new MethodNode(ACC_PUBLIC, "alwaysCapture", "()Z", null, null);
            alwaysCapture.visitVarInsn(ALOAD, 0);
            alwaysCapture.visitFieldInsn(GETFIELD, "net/minecraft/entity/item/EntityPainting$EnumArt", "alwaysCapture", "Z");
            alwaysCapture.visitInsn(IRETURN);
            alwaysCapture.visitMaxs(1, 2);
            classNode.methods.add(alwaysCapture);
            /*
             * @ASMGenerated
             * public void setAlwaysCapture(boolean alwaysCapture)
             * {
             *     this.alwaysCapture = alwaysCapture;
             * }
             */
            final MethodNode setAlwaysCapture = new MethodNode(ACC_PUBLIC, "setAlwaysCapture", "(Z)V", null, null);
            setAlwaysCapture.visitVarInsn(ALOAD, 0);
            setAlwaysCapture.visitVarInsn(ILOAD, 1);
            setAlwaysCapture.visitFieldInsn(PUTFIELD, "net/minecraft/entity/item/EntityPainting$EnumArt", "alwaysCapture", "Z");
            setAlwaysCapture.visitInsn(RETURN);
            setAlwaysCapture.visitMaxs(2, 3);
            classNode.methods.add(setAlwaysCapture);
            /*
             * @ASMGenerated
             * public boolean hasBackTexture()
             * {
             *     return this.hasBackTexture;
             * }
             */
            final MethodNode hasBackTexture = new MethodNode(ACC_PUBLIC, "hasBackTexture", "()Z", null, null);
            hasBackTexture.visitVarInsn(ALOAD, 0);
            hasBackTexture.visitFieldInsn(GETFIELD, "net/minecraft/entity/item/EntityPainting$EnumArt", "hasBackTexture", "Z");
            hasBackTexture.visitInsn(IRETURN);
            hasBackTexture.visitMaxs(1, 2);
            classNode.methods.add(hasBackTexture);
            /*
             * @ASMGenerated
             * public void setHasBackTexture(boolean hasTexture)
             * {
             *     this.hasBackTexture = hasTexture;
             * }
             */
            final MethodNode setHasBackTexture = new MethodNode(ACC_PUBLIC, "setHasBackTexture", "(Z)V", null, null);
            setHasBackTexture.visitVarInsn(ALOAD, 0);
            setHasBackTexture.visitVarInsn(ILOAD, 1);
            setHasBackTexture.visitFieldInsn(PUTFIELD, "net/minecraft/entity/item/EntityPainting$EnumArt", "hasBackTexture", "Z");
            setHasBackTexture.visitInsn(RETURN);
            setHasBackTexture.visitMaxs(2, 3);
            classNode.methods.add(setHasBackTexture);
            /*
             * @ASMGenerated
             * public boolean hasSideTexture()
             * {
             *     return this.hasSideTexture;
             * }
             */
            final MethodNode hasSideTexture = new MethodNode(ACC_PUBLIC, "hasSideTexture", "()Z", null, null);
            hasSideTexture.visitVarInsn(ALOAD, 0);
            hasSideTexture.visitFieldInsn(GETFIELD, "net/minecraft/entity/item/EntityPainting$EnumArt", "hasSideTexture", "Z");
            hasSideTexture.visitInsn(IRETURN);
            hasSideTexture.visitMaxs(1, 2);
            classNode.methods.add(hasSideTexture);
            /*
             * @ASMGenerated
             * public void setHasSideTexture(boolean hasTexture)
             * {
             *     this.hasSideTexture = hasTexture;
             * }
             */
            final MethodNode setHasSideTexture = new MethodNode(ACC_PUBLIC, "setHasSideTexture", "(Z)V", null, null);
            setHasSideTexture.visitVarInsn(ALOAD, 0);
            setHasSideTexture.visitVarInsn(ILOAD, 1);
            setHasSideTexture.visitFieldInsn(PUTFIELD, "net/minecraft/entity/item/EntityPainting$EnumArt", "hasSideTexture", "Z");
            setHasSideTexture.visitInsn(RETURN);
            setHasSideTexture.visitMaxs(2, 3);
            classNode.methods.add(setHasSideTexture);
            /*
             * @ASMGenerated
             * public boolean useSpecialRenderer()
             * {
             *     return this.useSpecialRenderer;
             * }
             */
            final MethodNode useSpecialRenderer = new MethodNode(ACC_PUBLIC, "useSpecialRenderer", "()Z", null, null);
            useSpecialRenderer.visitVarInsn(ALOAD, 0);
            useSpecialRenderer.visitFieldInsn(GETFIELD, "net/minecraft/entity/item/EntityPainting$EnumArt", "useSpecialRenderer", "Z");
            useSpecialRenderer.visitInsn(IRETURN);
            useSpecialRenderer.visitMaxs(1, 2);
            classNode.methods.add(useSpecialRenderer);
            /*
             * @ASMGenerated
             * public void setUseSpecialRenderer(boolean useSpecialRenderer)
             * {
             *     this.useSpecialRenderer = useSpecialRenderer;
             * }
             */
            final MethodNode setUseSpecialRenderer = new MethodNode(ACC_PUBLIC, "setUseSpecialRenderer", "(Z)V", null, null);
            setUseSpecialRenderer.visitVarInsn(ALOAD, 0);
            setUseSpecialRenderer.visitVarInsn(ILOAD, 1);
            setUseSpecialRenderer.visitFieldInsn(PUTFIELD, "net/minecraft/entity/item/EntityPainting$EnumArt", "useSpecialRenderer", "Z");
            setUseSpecialRenderer.visitInsn(RETURN);
            setUseSpecialRenderer.visitMaxs(2, 3);
            classNode.methods.add(setUseSpecialRenderer);

            //writes the changes
            final ClassWriter writer = new ClassWriter(0);
            classNode.accept(writer);
            return writer.toByteArray();
        }

        return basicClass;
    }
}

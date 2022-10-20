package git.jbredwards.jsonpaintings.asm.transformer;

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
            classNode.interfaces.add("git/jbredwards/jsonpaintings/common/util/IJSONPainting");
            //add new fields
            classNode.fields.add(new FieldNode(ACC_PRIVATE, "frontTexture", "Lnet/minecraft/util/ResourceLocation;", null, null));
            classNode.fields.add(new FieldNode(ACC_PRIVATE, "backTexture", "Lnet/minecraft/util/ResourceLocation;", null, null));
            classNode.fields.add(new FieldNode(ACC_PRIVATE, "sideTexture", "Lnet/minecraft/util/ResourceLocation;", null, null));
            classNode.fields.add(new FieldNode(ACC_PRIVATE, "backOffsetX", "I", null, 0));
            classNode.fields.add(new FieldNode(ACC_PRIVATE, "backOffsetY", "I", null, 0));
            classNode.fields.add(new FieldNode(ACC_PRIVATE, "sideOffsetX", "I", null, 0));
            classNode.fields.add(new FieldNode(ACC_PRIVATE, "sideOffsetY", "I", null, 0));
            classNode.fields.add(new FieldNode(ACC_PRIVATE, "isCreative", "Z", null, false));
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

/*
 * Copyright (c) 2024. jbredwards
 * All rights reserved.
 */

package git.jbredwards.jsonpaintings.mod.asm.transformer;

import git.jbredwards.jsonpaintings.api.event.PaintingUpdateEvent;
import git.jbredwards.jsonpaintings.mod.JSONPaintings;
import git.jbredwards.jsonpaintings.mod.common.capability.IArtCapability;
import git.jbredwards.jsonpaintings.mod.common.util.IJSONPainting;
import git.jbredwards.jsonpaintings.mod.common.util.JSONHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

/**
 *
 * @author jbred
 *
 */
@SuppressWarnings("unused")
public final class EntityPaintingTransformer implements IClassTransformer, Opcodes
{
    @Nonnull
    @Override
    public byte[] transform(@Nonnull String name, @Nonnull String transformedName, @Nonnull byte[] basicClass) {
        if("net.minecraft.entity.item.EntityPainting".equals(transformedName)) {
            final ClassNode classNode = new ClassNode();
            new ClassReader(basicClass).accept(classNode, ClassReader.SKIP_FRAMES);

            methods:
            for(MethodNode method : classNode.methods) {
                /*
                 * Constructor: (changes are around line 35)
                 * Old code:
                 * for (EntityPainting.EnumArt entitypainting$enumart : EntityPainting.EnumArt.values())
                 * {
                 *     ...
                 * }
                 *
                 * New code:
                 * // remove creative paintings from the fallback random selection
                 * // this constructor should never get called, this transformer exists in case of another mod that does
                 * for (EntityPainting.EnumArt entitypainting$enumart : Hooks.values())
                 * {
                 *     ...
                 * }
                 */
                if(method.name.equals("<init>") && method.desc.equals("(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)V")) {
                    for(final AbstractInsnNode insn : method.instructions.toArray()) {
                        if(insn.getOpcode() == INVOKESTATIC && ((MethodInsnNode)insn).owner.equals("net/minecraft/entity/item/EntityPainting$EnumArt")) {
                            ((MethodInsnNode)insn).owner = "git/jbredwards/jsonpaintings/mod/asm/transformer/EntityPaintingTransformer$Hooks";
                            break;
                        }
                    }
                }
                /*
                 * readEntityFromNBT: (changes are around line 115)
                 * Old code:
                 * this.art = EntityPainting.EnumArt.KEBAB;
                 *
                 * New code:
                 * // apply mappings
                 * this.art = Hooks.remap(s, EntityPainting.EnumArt.KEBAB);
                 */
                else if(method.name.equals(FMLLaunchHandler.isDeobfuscatedEnvironment() ? "readEntityFromNBT" : "func_70037_a")) {
                    for(final AbstractInsnNode insn : method.instructions.toArray()) {
                        if(insn.getOpcode() == GETSTATIC && ((FieldInsnNode)insn).name.equals("KEBAB")) {
                            method.instructions.insertBefore(insn, new VarInsnNode(ALOAD, 2));
                            method.instructions.insert(insn, new MethodInsnNode(INVOKESTATIC, "git/jbredwards/jsonpaintings/mod/asm/transformer/EntityPaintingTransformer$Hooks", "remap", "(Ljava/lang/String;Lnet/minecraft/entity/item/EntityPainting$EnumArt;)Lnet/minecraft/entity/item/EntityPainting$EnumArt;", false));
                            break;
                        }
                    }
                }
                /*
                 * onBroken: (changes are around line 150)
                 * Old code:
                 * this.entityDropItem(new ItemStack(Items.PAINTING), 0.0F);
                 *
                 * New code:
                 * // apply IArtCapability if sneaking
                 * this.entityDropItem(Hooks.applyArt(new ItemStack(Items.PAINTING), this, brokenEntity), 0.0F);
                 */
                else if(method.name.equals(FMLLaunchHandler.isDeobfuscatedEnvironment() ? "onBroken" : "func_110128_b")) {
                    for(final AbstractInsnNode insn : method.instructions.toArray()) {
                        if(insn.getOpcode() == FCONST_0) {
                            method.instructions.insertBefore(insn, new VarInsnNode(ALOAD, 0));
                            method.instructions.insertBefore(insn, new VarInsnNode(ALOAD, 1));
                            method.instructions.insertBefore(insn, new MethodInsnNode(INVOKESTATIC, "git/jbredwards/jsonpaintings/mod/asm/transformer/EntityPaintingTransformer$Hooks", "applyArt", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/item/EntityPainting;Lnet/minecraft/entity/Entity;)Lnet/minecraft/item/ItemStack;", false));
                            break methods;
                        }
                    }
                }
            }

            // ---------------
            // getPickedResult
            // ---------------

            final MethodNode getPickedResult = new MethodNode(ACC_PUBLIC, "getPickedResult", "(Lnet/minecraft/util/math/RayTraceResult;)Lnet/minecraft/item/ItemStack;", null, null);
            getPickedResult.visitVarInsn(ALOAD, 0);
            getPickedResult.visitMethodInsn(INVOKESTATIC, "git/jbredwards/jsonpaintings/mod/asm/transformer/EntityPaintingTransformer$Hooks", "getPickedResult", "(Lnet/minecraft/entity/item/EntityPainting;)Lnet/minecraft/item/ItemStack;", false);
            getPickedResult.visitInsn(ARETURN);
            getPickedResult.visitMaxs(1, 2);
            classNode.methods.add(getPickedResult);

            // --------
            // onUpdate
            // --------

            final MethodNode onUpdate = new MethodNode(ACC_PUBLIC, FMLLaunchHandler.isDeobfuscatedEnvironment() ? "onUpdate" : "func_70071_h_", "()V", null, null);
            onUpdate.visitVarInsn(ALOAD, 0);
            onUpdate.visitMethodInsn(INVOKESTATIC, "git/jbredwards/jsonpaintings/mod/asm/transformer/EntityPaintingTransformer$Hooks", "onUpdatePainting", "(Lnet/minecraft/entity/item/EntityPainting;)Z", false);

            final LabelNode label = new LabelNode();
            onUpdate.instructions.add(new JumpInsnNode(IFEQ, label));
            onUpdate.visitInsn(RETURN);

            onUpdate.instructions.add(label);
            onUpdate.visitFrame(F_SAME, 0, null, 0, null);

            onUpdate.visitVarInsn(ALOAD, 0);
            onUpdate.visitMethodInsn(INVOKESPECIAL, "net/minecraft/entity/EntityHanging", FMLLaunchHandler.isDeobfuscatedEnvironment() ? "onUpdate" : "func_70071_h_", "()V", false);
            onUpdate.visitInsn(RETURN);
            onUpdate.visitMaxs(1, 2);
            classNode.methods.add(onUpdate);

            //writes the changes
            final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(writer);
            return writer.toByteArray();
        }

        return basicClass;
    }

    public static final class Hooks
    {
        @Nonnull
        public static ItemStack applyArt(@Nonnull final ItemStack stack, @Nonnull final EntityPainting painting, @Nullable final Entity breaker) {
            if(breaker instanceof EntityPlayer && breaker.isSneaking() || IJSONPainting.from(painting.art).alwaysCapture()) {
                final @Nullable IArtCapability cap = IArtCapability.get(stack);
                if(cap != null) cap.setArt(painting.art);
            }

            return stack;
        }

        @Nonnull
        public static EntityPainting.EnumArt[] values() {
            final EntityPainting.EnumArt[] values = Arrays.stream(EntityPainting.EnumArt.values())
                    .filter(art -> !IJSONPainting.from(art).isCreative())
                    .toArray(EntityPainting.EnumArt[]::new);

            //this would be wack
            if(values.length == 0) throw new IllegalStateException(
                    "Attempted to place painting entity with no valid art values! " +
                    "Please ensure there is at least one 1x1 art type with \"isCreative\" set to false."
            );

            return values;
        }

        @Nonnull
        public static ItemStack getPickedResult(@Nonnull final EntityPainting entity) {
            final ItemStack stack = new ItemStack(Items.PAINTING);
            if(GuiScreen.isCtrlKeyDown()) {
                final @Nullable IArtCapability cap = IArtCapability.get(stack);
                if(cap != null) cap.setArt(entity.art);
            }

            return stack;
        }

        public static boolean onUpdatePainting(@Nonnull final EntityPainting painting) {
            return MinecraftForge.EVENT_BUS.post(new PaintingUpdateEvent(painting));
        }

        @Nonnull
        public static EntityPainting.EnumArt remap(@Nonnull final String motive, @Nonnull final EntityPainting.EnumArt fallback) {
            @Nullable final EntityPainting.EnumArt mapped = JSONHandler.PAINTING_REMAPS.get(motive);
            if(mapped != null) return mapped;

            JSONPaintings.LOGGER.error("Painting with motive: \"" + motive + "\" has been removed, resulting in lost data. This can be fixed by assigning it a mapping.");
            return fallback;
        }
    }
}

package git.jbredwards.jsonpaintings.asm.transformer;

import git.jbredwards.jsonpaintings.common.capability.IArtCapability;
import git.jbredwards.jsonpaintings.common.util.IJSONPainting;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.IClassTransformer;
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
                 * //remove creative paintings from the fallback random selection
                 * //this constructor should never get called, this transformer exists in case of another mod that does
                 * for (EntityPainting.EnumArt entitypainting$enumart : Hooks.values())
                 * {
                 *     ...
                 * }
                 */
                if(method.name.equals("<init>") && method.desc.equals("(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)V")) {
                    for(AbstractInsnNode insn : method.instructions.toArray()) {
                        if(insn.getOpcode() == INVOKESTATIC && ((MethodInsnNode)insn).owner.equals("net/minecraft/entity/item/EntityPainting$EnumArt")) {
                            ((MethodInsnNode)insn).owner = "git/jbredwards/jsonpaintings/asm/transformer/EntityPaintingTransformer$Hooks";
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
                 * //apply IArtCapability if sneaking
                 * this.entityDropItem(Hooks.applyArt(new ItemStack(Items.PAINTING), this, brokenEntity), 0.0F);
                 */
                else if(method.name.equals(FMLLaunchHandler.isDeobfuscatedEnvironment() ? "onBroken" : "func_110128_b")) {
                    for(AbstractInsnNode insn : method.instructions.toArray()) {
                        if(insn.getOpcode() == FCONST_0) {
                            method.instructions.insertBefore(insn, new VarInsnNode(ALOAD, 0));
                            method.instructions.insertBefore(insn, new VarInsnNode(ALOAD, 1));
                            method.instructions.insertBefore(insn, new MethodInsnNode(INVOKESTATIC, "git/jbredwards/jsonpaintings/asm/transformer/EntityPaintingTransformer$Hooks", "applyArt", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/item/EntityPainting;Lnet/minecraft/entity/Entity;)Lnet/minecraft/item/ItemStack;", false));
                            break methods;
                        }
                    }
                }
            }

            final MethodNode getPickedResult = new MethodNode(ACC_PUBLIC, "getPickedResult", "(Lnet/minecraft/util/math/RayTraceResult;)Lnet/minecraft/item/ItemStack;", null, null);
            getPickedResult.visitVarInsn(ALOAD, 0);
            getPickedResult.visitMethodInsn(INVOKESTATIC, "git/jbredwards/jsonpaintings/asm/transformer/EntityPaintingTransformer$Hooks", "getPickedResult", "(Lnet/minecraft/entity/item/EntityPainting;)Lnet/minecraft/item/ItemStack;", false);
            getPickedResult.visitInsn(ARETURN);
            getPickedResult.visitMaxs(1, 2);
            classNode.methods.add(getPickedResult);

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
        public static ItemStack applyArt(@Nonnull ItemStack stack, @Nonnull EntityPainting painting, @Nullable Entity breaker) {
            if(breaker instanceof EntityPlayer && breaker.isSneaking()) {
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
        public static ItemStack getPickedResult(@Nonnull EntityPainting entity) {
            final ItemStack stack = new ItemStack(Items.PAINTING);
            if(GuiScreen.isCtrlKeyDown()) {
                final @Nullable IArtCapability cap = IArtCapability.get(stack);
                if(cap != null) cap.setArt(entity.art);
            }

            return stack;
        }
    }
}

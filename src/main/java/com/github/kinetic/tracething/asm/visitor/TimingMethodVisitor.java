package com.github.kinetic.tracething.asm.visitor;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * Method visitor to add timing instrumentation to methods
 */
public final class TimingMethodVisitor extends AdviceAdapter {

    /**
     * The name of the method being visited
     */
    private final String methodName;

    /**
     * The local variable index for the start time
     */
    private int startTimeVar;

    /**
     * Constructor
     *
     * @param access     the method's access flags (see {@link Opcodes})
     * @param name       the method's name
     * @param descriptor the method's descriptor (see {@link org.objectweb.asm.Type Type})
     * @param mv         the method visitor to which this adapter must delegate method calls. May be
     */
    public TimingMethodVisitor(
            final int access,
            final String name,
            final String descriptor,
            final MethodVisitor mv
    ) {
        super(Opcodes.ASM9, mv, access, name, descriptor);

        this.methodName = name;
    }

    /**
     * On method entrance, store the start time in a local variable
     */
    @Override
    protected void onMethodEnter() {
        startTimeVar = newLocal(org.objectweb.asm.Type.LONG_TYPE);

        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
        mv.visitVarInsn(LSTORE, startTimeVar);
    }

    /**
     * On method exit, calculate the duration and store it in a local variable
     *
     * @param opcode one of {@link Opcodes#RETURN}, {@link Opcodes#IRETURN}, {@link Opcodes#FRETURN},
     *               {@link Opcodes#ARETURN}, {@link Opcodes#LRETURN}, {@link Opcodes#DRETURN} or {@link
     *               Opcodes#ATHROW}.
     */
    @Override
    protected void onMethodExit(final int opcode) {
        final int durationVar = newLocal(org.objectweb.asm.Type.LONG_TYPE);

        // calculate duration
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
        mv.visitVarInsn(LLOAD, startTimeVar);
        mv.visitInsn(LSUB);
        mv.visitVarInsn(LSTORE, durationVar);

        // add duration to profiling data
        mv.visitLdcInsn(methodName);
        mv.visitVarInsn(LLOAD, durationVar);
        mv.visitMethodInsn(
                INVOKESTATIC,
                "com/github/kinetic/tracething/dto/ProfilingData",
                "add",
                "(Ljava/lang/String;J)V",
                false
        );
    }
}

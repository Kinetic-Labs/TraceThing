package com.github.kinetic.tracething.asm.visitor;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

public class TimingMethodVisitor extends AdviceAdapter {

    private final String methodName;
    private int startTimeVar;

    public TimingMethodVisitor(int access, String name, String descriptor, MethodVisitor mv) {
        super(Opcodes.ASM9, mv, access, name, descriptor);

        this.methodName = name;
    }

    @Override
    protected void onMethodEnter() {
        startTimeVar = newLocal(org.objectweb.asm.Type.LONG_TYPE);

        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
        mv.visitVarInsn(LSTORE, startTimeVar);
    }

    @Override
    protected void onMethodExit(int opcode) {
        int durationVar = newLocal(org.objectweb.asm.Type.LONG_TYPE);

        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);
        mv.visitVarInsn(LLOAD, startTimeVar);
        mv.visitInsn(LSUB);
        mv.visitVarInsn(LSTORE, durationVar);

        mv.visitLdcInsn(methodName);
        mv.visitVarInsn(LLOAD, durationVar);
        mv.visitMethodInsn(INVOKESTATIC, "com/github/kinetic/tracething/dto/ProfilingData", "add", "(Ljava/lang/String;J)V", false);
    }
}

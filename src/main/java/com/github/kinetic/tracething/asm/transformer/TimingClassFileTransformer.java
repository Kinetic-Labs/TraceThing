package com.github.kinetic.tracething.asm.transformer;

import com.github.kinetic.tracething.asm.visitor.TimingMethodVisitor;
import org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

@SuppressWarnings("CallToPrintStackTrace")
public class TimingClassFileTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        if(!className.startsWith("com/github/kinetic/tracething") ||
                className.contains("tracething/TraceThingAgent") ||
                className.contains("tracething/Timing") ||
                className.contains("tracething/dto/ProfilingData")) {

            return classfileBuffer;
        }

        try {
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            ClassVisitor cv = new ClassVisitor(Opcodes.ASM9, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                    return new TimingMethodVisitor(access, name, descriptor, mv);
                }
            };
            cr.accept(cv, ClassReader.EXPAND_FRAMES);
            return cw.toByteArray();
        } catch(Exception exception) {
            System.err.println("Error transforming class " + className);
            exception.printStackTrace();

            return classfileBuffer;
        }
    }
}

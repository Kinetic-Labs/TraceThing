package com.github.kinetic.tracething.asm.transformer;

import com.github.kinetic.tracething.asm.visitor.TimingMethodVisitor;
import org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

/**
 * Class file transformer to add timing instrumentation to classes
 */
@SuppressWarnings("CallToPrintStackTrace")
public final class TimingClassFileTransformer implements ClassFileTransformer {

    /**
     * Transform a class file and return the transformed bytes
     *
     * @param loader              the defining loader of the class to be transformed,
     *                            may be {@code null} if the bootstrap loader
     * @param className           the name of the class in the internal form of fully
     *                            qualified class and interface names as defined in
     *                            <i>The Java Virtual Machine Specification</i>.
     *                            For example, <code>"java/util/List"</code>.
     * @param classBeingRedefined if this is triggered by a redefine or retransform,
     *                            the class being redefined or retransformed;
     *                            if this is a class load, {@code null}
     * @param protectionDomain    the protection domain of the class being defined or redefined
     * @param classfileBuffer     the input byte buffer in class file format - must not be modified
     * @return the transformed bytes, or the original bytes if no transformation is needed
     */
    @Override
    public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined,
                            final ProtectionDomain protectionDomain, final byte[] classfileBuffer) {

        // skip internal classes
        if(
                className.startsWith("com/github/kinetic/tracething") ||
                className.contains("tracething/TraceThingAgent") ||
                className.contains("tracething/dto/ProfilingData") ||
                className.contains("tracething/util/HtmlReportGenerator") ||
                className.startsWith("java") ||
                className.startsWith("sun") ||
                className.startsWith("jdk")
        )
            return classfileBuffer;

        try {
            final ClassReader cr = new ClassReader(classfileBuffer);
            final ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            final ClassVisitor cv = new ClassVisitor(Opcodes.ASM9, cw) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    final MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

                    // read time taken for method
                    return new TimingMethodVisitor(access, className, name, descriptor, mv);
                }
            };
            cr.accept(cv, ClassReader.EXPAND_FRAMES);

            return cw.toByteArray();
        } catch(final Exception exception) {
            System.err.println("Error transforming class " + className);
            exception.printStackTrace();

            return classfileBuffer;
        }
    }
}

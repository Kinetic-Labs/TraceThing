package com.github.kinetic.tracething;

import com.github.kinetic.tracething.asm.transformer.TimingClassFileTransformer;
import com.github.kinetic.tracething.util.HtmlReportGenerator;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.util.jar.JarFile;

/**
 * Main agent entrypoint for TraceThing
 */
@SuppressWarnings("CallToPrintStackTrace")
public final class TraceThingAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("Starting TraceThing...");

        // add agent to the bootstrap class loader
        try {
            inst.appendToBootstrapClassLoaderSearch(
                    new JarFile(
                            new File(TraceThingAgent.class
                                    .getProtectionDomain()
                                    .getCodeSource()
                                    .getLocation()
                                    .toURI()
                            )
                    )
            );
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        // add the class file transformer
        inst.addTransformer(new TimingClassFileTransformer());

        // add a shutdown hook to generate the profiling report
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            final HtmlReportGenerator htmlReportGenerator = new HtmlReportGenerator();

            System.out.println("Generating your profiling report...");

            htmlReportGenerator.generateReport();
        }));
    }
}

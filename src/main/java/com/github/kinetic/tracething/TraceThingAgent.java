package com.github.kinetic.tracething;

import com.github.kinetic.tracething.asm.transformer.TimingClassFileTransformer;
import com.github.kinetic.tracething.util.HtmlReportGenerator;

import java.lang.instrument.Instrumentation;

public class TraceThingAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("Starting TraceThing...");

        inst.addTransformer(new TimingClassFileTransformer());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Generating your profiling report...");
            HtmlReportGenerator.generateReport();
        }));
    }
}

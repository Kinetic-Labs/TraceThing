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

    private static final String MAGENTA = "\033[35m";
    private static final String RESET = "\033[0m";

    public static void premain(String agentArgs, Instrumentation inst) {
        final String asciiArt = getAsciiArt();

        System.out.println(asciiArt);

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

            System.out.println(MAGENTA + "TT :: Generating your profiling report..." + RESET);

            htmlReportGenerator.generateReport();
        }));
    }

    private static String getAsciiArt() {
        return MAGENTA + """
                         TraceThing
                        ,----,       ,----,
                      ,/   .`|     ,/   .`|
                    ,`   .'  :   ,`   .'  :
                  ;    ;     / ;    ;     /
                .'___,/    ,'.'___,/    ,'
                |    :     | |    :     |
                ;    |.';  ; ;    |.';  ;
                `----'  |  | `----'  |  |
                    '   :  ;     '   :  ;
                    |   |  '     |   |  '
                    '   :  |     '   :  |
                    ;   |.'      ;   |.'
                    '---'        '---'
                      By Kinetic Labs
                """ + RESET;
    }
}

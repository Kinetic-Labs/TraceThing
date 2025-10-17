package com.github.kinetic.tracething;

public class Main {

    public static void main(String[] ignoredArgs) throws InterruptedException {
        System.out.println("Sample application started.");
        methodA();
        methodB();

        System.out.println(
                "Sample app complete, to use as a java agent pass this jar through the -javaagent flag"
        );
    }

    public static void methodA() throws InterruptedException {
        System.out.println("Executing methodA");
        Thread.sleep(100);

        for(int i = 0; i < 5; i++)
            methodC();
    }

    public static void methodB() throws InterruptedException {
        System.out.println("Executing methodB");
        Thread.sleep(200);
    }

    public static void methodC() throws InterruptedException {
        System.out.println("Executing methodC");
        Thread.sleep(50);
    }
}

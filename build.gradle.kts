plugins {
    id("java")
    application
    id("com.gradleup.shadow") version "9.2.2"
}

group = "com.github.kinetic.tracething"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("com.github.kinetic.tracething.Main")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.ow2.asm:asm:9.9")
    implementation("org.ow2.asm:asm-commons:9.9")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.jar {
    enabled = false
}

tasks.shadowJar {
    archiveBaseName.set("TraceThing")
    archiveClassifier.set("")
    archiveVersion.set("1.0-SNAPSHOT")

    manifest {
        attributes(
            "Premain-Class" to "com.github.kinetic.tracething.TraceThingAgent",
            "Main-Class" to "com.github.kinetic.tracething.Main",
            "Can-Redefine-Classes" to true,
            "Can-Retransform-Classes" to true
        )
    }

}

tasks.distZip {
    dependsOn(tasks.shadowJar)
    from(tasks.shadowJar.get().archiveFile)
}

tasks.distTar {
    dependsOn(tasks.shadowJar)
    from(tasks.shadowJar.get().archiveFile)
}

tasks.startScripts {
    dependsOn(tasks.shadowJar)
    applicationName = "TraceThing"
}

tasks.test {
    useJUnitPlatform()
}

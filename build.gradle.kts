plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    id("com.gradleup.shadow") version "8.3.5"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id("org.openapi.generator") version "7.12.0"
}

allprojects {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://maven.cereal-automation.com/releases")
        }
    }
}

buildscript {
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.6.1")
    }
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    if (name !in listOf("command", "command-monitoring", "script-common")) {
        apply(plugin = "com.gradleup.shadow")

        tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
            archiveFileName.set("release.jar")

            dependencies {
                // The below dependencies are included in the Cereal client by default so they can be excluded here.
                // New (breaking) versions will have a different artifact id so they will always stay compatible.
                // Be careful when adding something here because Proguard could need the code to determine that methods
                // called by any of these libs are in still in use so that Proguard doesn't remove them. For example
                // kotlinx-coroutines-core isn't excluded for that reason.
                exclude { dependency ->
                    (
                        dependency.moduleGroup == "com.cereal-automation" &&
                            (dependency.moduleName == "cereal-sdk" || dependency.moduleName == "cereal-chrome-driver")
                    )
                }

                // Kotlin is included in the Cereal client by default so leave it out to make the script binary smaller and to
                // prevent conflicts with coroutines, which is also used in the Scripts' interface.
                exclude("DebugProbesKt.bin", "META-INF/**", "*.jpg", "kotlin/**")
            }
        }

        tasks.register("scriptJar", proguard.gradle.ProGuardTask::class.java) {
            description = "Build script jar with obfuscation"
            dependsOn("shadowJar")

            val artifactName = "release.jar"
            val buildDir = layout.buildDirectory.get()
            val obfuscatedFolder = "$buildDir/obfuscated"

            injars("$buildDir/libs/$artifactName")
            outjars("$obfuscatedFolder/$artifactName")

            // Mapping for debugging
            printseeds("$obfuscatedFolder/seeds.txt")
            printmapping("$obfuscatedFolder/mapping.txt")

            // Dependencies
            libraryjars(sourceSets.main.get().compileClasspath)

            configuration(
                files(
                    "${rootDir.absolutePath}/proguard-rules/script.pro",
                    "${rootDir.absolutePath}/proguard-rules/cereal-licensing.pro",
                    "${rootDir.absolutePath}/proguard-rules/coroutines.pro",
                    "${rootDir.absolutePath}/proguard-rules/kotlinx-serialization.pro",
                    "${rootDir.absolutePath}/proguard-rules/ktor.pro",
                ),
            )
        }
    }
}

tasks {
    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}

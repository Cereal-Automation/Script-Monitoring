plugins {
    kotlin("jvm") version "2.0.21"
    id("com.gradleup.shadow") version "8.3.5"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
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
    apply(plugin = "com.gradleup.shadow")

    tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
        archiveFileName.set("release.jar")

        dependencies {
            // The below dependencies are included in the Cereal client by default. New (breaking) versions will have a different artifact id.
            exclude { dependency ->
                dependency.moduleGroup == "com.cereal-automation" &&
                    (dependency.moduleName == "cereal-sdk" || dependency.moduleName == "cereal-chrome-driver")
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
                "${rootDir.absolutePath}/proguard-rules/kotlinx-serialization.pro",
            ),
        )
    }
}

tasks {
    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gradleup.shadow)
    alias(libs.plugins.ktlint)
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
        classpath(libs.proguard)
    }
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "java")

    dependencies {
        implementation(rootProject.libs.cereal.sdk) {
            artifact {
                classifier = "all"
            }
        }
        implementation(rootProject.libs.bundles.cereal.base)
    }
}

project(":script") {
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
                                dependency.moduleName == "cereal-sdk"
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
                "${rootDir.absolutePath}/proguard-rules/okhttp.pro",
                "${rootDir.absolutePath}/proguard-rules/okio.pro",
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

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

    // Exclude these dependencies because they are added as compileOnly.
    configurations.configureEach {
        if (name == "runtimeClasspath") {
            exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
            exclude(group = "com.cereal-automation", module = "cereal-sdk")
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
}

buildscript {
    dependencies {
        classpath(libs.proguard)
    }
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    if (name !in listOf("command", "command-monitoring", "script-common", "scraping-common")) {
        apply(plugin = "com.gradleup.shadow")

        tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
            archiveFileName.set("release.jar")
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
                    "${rootDir.absolutePath}/proguard-rules/kdriver.pro",
                    "${rootDir.absolutePath}/proguard-rules/kotlinx-serialization.pro",
                    "${rootDir.absolutePath}/proguard-rules/ktor.pro",
                    "${rootDir.absolutePath}/proguard-rules/okhttp.pro",
                    "${rootDir.absolutePath}/proguard-rules/okio.pro",
                    "${rootDir.absolutePath}/proguard-rules/commons-logging.pro",
                ),
            )
        }
    }
}

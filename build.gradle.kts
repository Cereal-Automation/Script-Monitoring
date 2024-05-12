apply(from = "packaging.gradle")
apply(from = "proguard.gradle")

plugins {
    kotlin("jvm") version "1.9.22"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    maven {
        url = uri("https://maven.cereal-automation.com/releases")
    }
    mavenCentral()
}

dependencies {
    implementation("com.cereal-automation:cereal-sdk:1.0.0:all")
    implementation("com.cereal-automation:cereal-licensing:1.0.0")

    // Other Cereal libraries, uncomment to use them.
    // implementation("com.cereal-automation:cereal-chrome-driver:0.11.0:all")

    testImplementation(kotlin("test"))
    testImplementation("com.cereal-automation:cereal-test-utils:1.0.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    testImplementation("io.mockk:mockk:1.13.9")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs = listOf("-Xjvm-default=all")
        }
    }

    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs = listOf("-Xjvm-default=all")
        }
    }

    kotlin {
        jvmToolchain(17)
    }
}

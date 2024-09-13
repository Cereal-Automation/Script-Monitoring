apply(from = "packaging.gradle")
apply(from = "proguard.gradle")

plugins {
    kotlin("jvm") version "1.9.22"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

allprojects {
    repositories {
        maven {
            url = uri("https://maven.cereal-automation.com/releases")
        }
        mavenCentral()
    }
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

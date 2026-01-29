plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.bundles.kotlin.coroutines)

    testImplementation(libs.bundles.testing)
    testImplementation(libs.cereal.test.utils)

    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines.core)
}

tasks.test {
    useJUnitPlatform {
        excludeTags("integration")
    }
}

tasks.register<Test>("integrationTest") {
    useJUnitPlatform {
        includeTags("integration")
    }
    description = "Runs only the integrations tests."
    group = "verification"
}

kotlin {
    jvmToolchain(17)
}

apply(from = "${rootProject.projectDir}/gradle/publishing.gradle.kts")

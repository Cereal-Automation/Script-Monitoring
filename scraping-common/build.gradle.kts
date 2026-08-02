plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    compileOnly(libs.cereal.sdk)
    implementation(libs.bundles.cereal.base)
    implementation(libs.bundles.web.scraping)
    implementation(libs.bundles.kotlin.coroutines)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.bundles.ktor.client)

    implementation(project(":command"))

    testImplementation(libs.cereal.sdk)
    testImplementation(libs.bundles.testing)
    testImplementation(libs.ktor.client.mock)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

apply(from = "${rootProject.projectDir}/gradle/publishing.gradle.kts")

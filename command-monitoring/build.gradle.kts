plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    compileOnly(libs.cereal.sdk)
    implementation(libs.bundles.cereal.base)
    implementation(libs.bundles.kotlin.coroutines)

    testImplementation(libs.cereal.sdk)
    testImplementation(libs.bundles.testing)

    implementation(project(":command"))
    testImplementation(project(":command"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

apply(from = "${rootProject.projectDir}/gradle/publishing.gradle.kts")

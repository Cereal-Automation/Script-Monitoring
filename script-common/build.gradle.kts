plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-test-fixtures`
}

dependencies {
    compileOnly(libs.cereal.sdk)
    implementation(libs.bundles.cereal.base)

    // Shared test support reused by the script modules (e.g. FakeLogRepository).
    testFixturesApi(project(":command"))

    testImplementation(libs.cereal.sdk)
    testImplementation(libs.bundles.testing)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

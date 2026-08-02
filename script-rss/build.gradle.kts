plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    compileOnly(libs.cereal.sdk)
    implementation(libs.bundles.cereal.base)
    implementation(libs.bundles.web.scraping)
    implementation(libs.bundles.kotlin.coroutines)

    implementation(project(":script-common"))
    implementation(project(":command"))
    implementation(project(":command-monitoring"))

    testImplementation(libs.cereal.sdk)
    testImplementation(libs.bundles.testing)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

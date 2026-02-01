plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    compileOnly(libs.cereal.sdk) {
        artifact {
            classifier = "all"
        }
    }
    implementation(libs.bundles.cereal.base)

    implementation(project(":script-common"))
    implementation(project(":command"))
    implementation(project(":command-monitoring"))

    testImplementation(libs.bundles.testing)
    testImplementation(libs.cereal.test.utils)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

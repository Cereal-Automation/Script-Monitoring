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

    testImplementation(libs.cereal.sdk) {
        artifact {
            classifier = "all"
        }
    }
    testImplementation(libs.bundles.testing)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

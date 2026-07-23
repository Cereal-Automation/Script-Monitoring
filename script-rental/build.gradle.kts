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
    implementation(libs.bundles.web.scraping)
    implementation(libs.bundles.kotlin.coroutines)
    implementation(libs.kdriver)

    implementation(project(":script-common"))
    implementation(project(":command"))
    implementation(project(":command-monitoring"))

    testImplementation(libs.cereal.sdk) {
        artifact {
            classifier = "all"
        }
    }
    testImplementation(libs.bundles.testing)
    testImplementation(testFixtures(project(":script-common")))
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
    // kdriver is published targeting Java 21; the toolchain must match to run
    // the browser-based repositories and their tests (root still targets 17 bytecode).
    jvmToolchain(21)
}

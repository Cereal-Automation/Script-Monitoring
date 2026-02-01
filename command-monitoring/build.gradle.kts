plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
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
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.bundles.ktor.client)
    implementation(libs.logging.interceptor)

    testImplementation(libs.cereal.sdk) {
        artifact {
            classifier = "all"
        }
    }
    testImplementation(libs.bundles.testing)
    testImplementation(libs.ktor.client.mock)

    implementation(project(":command"))
    implementation(project(":stockx-api-client"))
    testImplementation(project(":command"))
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

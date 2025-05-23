plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    api("com.cereal-automation:cereal-sdk:1.6.0:all")
    api("com.cereal-automation:cereal-licensing:1.6.0")

    api("com.prof18.rssparser:rssparser:6.0.8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("org.htmlunit:htmlunit:4.7.0")
    implementation("org.jsoup:jsoup:1.19.1")
    api("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    testImplementation("io.mockk:mockk:1.13.13")
    testImplementation("io.ktor:ktor-client-mock:2.3.13")

    // Ktor
    implementation("io.ktor:ktor-client-core:2.3.13")
    implementation("io.ktor:ktor-client-okhttp:2.3.13")
    implementation("io.ktor:ktor-client-logging:2.3.13")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.13")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.13")
    implementation("io.ktor:ktor-client-auth:2.3.13")
    implementation("io.ktor:ktor-client-encoding:2.3.13")

    implementation(project(":command"))
    api(project(":stockx-api-client"))
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

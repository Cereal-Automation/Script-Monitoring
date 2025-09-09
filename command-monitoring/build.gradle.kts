plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation("com.cereal-automation:cereal-sdk:1.7.0:all")
    implementation("com.cereal-automation:cereal-licensing:1.7.1")

    implementation("com.prof18.rssparser:rssparser:6.0.8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("org.htmlunit:htmlunit:4.7.0")
    implementation("org.jsoup:jsoup:1.19.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    testImplementation("io.mockk:mockk:1.14.5")
    testImplementation("io.ktor:ktor-client-mock")

    implementation(platform("io.ktor:ktor-bom:3.2.3"))
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-okhttp")
    implementation("io.ktor:ktor-client-logging")
    implementation("io.ktor:ktor-client-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-client-auth")
    implementation("io.ktor:ktor-client-encoding")
    implementation("com.squareup.okhttp3:logging-interceptor:5.1.0")
    testImplementation("io.ktor:ktor-client-mock")

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

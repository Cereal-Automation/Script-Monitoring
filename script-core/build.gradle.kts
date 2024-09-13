plugins {
    kotlin("jvm")
}

dependencies {
    api("com.cereal-automation:cereal-sdk:1.2.0:all")
    implementation("com.cereal-automation:cereal-licensing:1.2.0")

    implementation("com.prof18.rssparser:rssparser:6.0.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    testImplementation(kotlin("test"))
    testImplementation("com.cereal-automation:cereal-test-utils:1.0.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    testImplementation("io.mockk:mockk:1.13.9")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

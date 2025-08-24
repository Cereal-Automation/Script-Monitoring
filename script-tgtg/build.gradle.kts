plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":script-common"))
    implementation(project(":command-monitoring"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    testImplementation("io.mockk:mockk:1.14.5")
    testImplementation("com.cereal-automation:cereal-test-utils:1.7.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

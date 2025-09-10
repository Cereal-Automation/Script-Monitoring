plugins {
    kotlin("jvm")
}

dependencies {
    implementation("com.cereal-automation:cereal-sdk:1.7.0:all")
    implementation("com.cereal-automation:cereal-licensing:1.7.1")

    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    testImplementation("io.mockk:mockk:1.14.5")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

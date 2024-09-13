plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":script-core"))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

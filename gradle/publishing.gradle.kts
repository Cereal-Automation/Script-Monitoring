apply(plugin = "maven-publish")

afterEvaluate {
    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                groupId = "com.cereal-automation"
                artifactId = "script-${project.name}"
                version = project.findProperty("publishVersion")?.toString() ?: "SNAPSHOT"
                pom {
                    val displayName = when (project.name) {
                        "command" -> "Cereal Script Command"
                        "command-monitoring" -> "Cereal Script Command Monitoring"
                        else -> project.name
                    }
                    name.set("$displayName Module")
                    description.set("$displayName module for Script Monitoring")
                    url.set("https://github.com/cereal-automation/script-monitoring")
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }
                    developers {
                        developer {
                            id.set("cereal-automation")
                            name.set("Cereal Automation")
                            email.set("support@cereal-automation.com")
                        }
                    }
                }
            }
        }
        repositories {
            maven {
                name = "local"
                url = uri("${rootProject.layout.buildDirectory.get()}/repos")
            }
        }
    }
}

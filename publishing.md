# Maven Artifacts

This project publishes Maven artifacts for the `command` and `command-monitoring` modules, making them available for use in other projects.

## Available Modules

- **command**: Core command functionality
- **command-monitoring**: Command monitoring and execution tracking

## Creating Local Maven Artifacts

### Prerequisites

- Java 17 or higher
- Gradle (or use the included `gradlew` wrapper)

### Publishing to Local Maven Repository

To create Maven artifacts in your local Maven repository (`~/.m2/repository`) with `SNAPSHOT` as version:

#### Publish Both Modules
```bash
./gradlew publishToMavenLocal
```

#### Publish Individual Modules
```bash
# Publish command module only
./gradlew :command:publishToMavenLocal

# Publish command-monitoring module only
./gradlew :command-monitoring:publishToMavenLocal
```

#### Publish with Custom Version
```bash
# Publish with a specific version
./gradlew :command:publishToMavenLocal -PpublishVersion=1.2.3
./gradlew :command-monitoring:publishToMavenLocal -PpublishVersion=1.2.3
```

### Using the Artifacts

Once published locally, you can use these artifacts in other projects by adding them to your `build.gradle.kts`:

```kotlin
repositories {
    mavenLocal()
}

dependencies {
    implementation("com.cereal-automation:script-command:SNAPSHOT")
    implementation("com.cereal-automation:script-command-monitoring:SNAPSHOT")
}
```

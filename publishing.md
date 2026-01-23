# Maven Artifacts

This project publishes Maven artifacts for the `command` and `command-monitoring` modules, making them available for use
in other projects.

## Available Modules

- **command**: Core command functionality
- **command-monitoring**: Command monitoring and execution tracking

## Automated Publishing to GitHub Packages

When a version tag is pushed (e.g., `1.0.0`), the GitHub Actions workflow automatically:

1. Builds the `command` and `command-monitoring` modules
2. Publishes them to GitHub Packages as Maven artifacts
3. Creates and uploads build artifacts

### Creating a Release

To trigger automated publishing:

```bash
# Create and push a version tag
git tag 1.0.0
git push origin 1.0.0
```

The artifacts will be published to GitHub Packages at:

- `com.cereal-automation:script-command:1.0.0`
- `com.cereal-automation:script-command-monitoring:1.0.0`

### Using Artifacts from GitHub Packages

To use the published artifacts in other projects:

```kotlin
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/cereal-automation/script-monitoring")
    }
}

dependencies {
    implementation("com.cereal-automation:script-command:1.0.0")
    implementation("com.cereal-automation:script-command-monitoring:1.0.0")
}
```

Note: Since this is a public repository, no authentication is required to download packages.

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

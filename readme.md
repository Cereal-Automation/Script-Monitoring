# Cereal Monitoring Script

## Development Getting Started

### Prerequisites

* Install Java SE Development Kit 17: https://www.oracle.com/java/technologies/downloads/#java17

### Installation

1. Create a new GitHub repository and use this repository as template.
2. Clone your project

```sh
git clone https://github.com/Cereal-Automation/Script-Monitoring.git
```

3. Open the project in your preferred IDE. We recommend using IntelliJ IDEA because of easier troubleshooting when you
   need any help.

### Available gradle commands

* Run all tests

```sh
./gradlew test
```

### Creating a release using GitHub Actions

The easiest way to build the script binary is by creating a git tag. This will trigger GitHub Actions to build a jar,
update the version_code,
obfuscate it, and automatically create a GitHub Release with the jar attached as an artifact. You can find the release
on the [GitHub Releases](https://docs.github.com/en/repositories/releasing-projects-on-github/about-releases) page of
your repository.

To create a release:

1. Commit and push your changes
2. Create and push a tag: `git tag v1.0.0 && git push origin v1.0.0`
3. The workflow will automatically build and create a GitHub Release using the tag as the version, with
   `release-<version>.jar` attached

### CI/CD

A GitHub actions configuration is included in this repository. It contains the following actions:

* On each push to master tests will run.
* When a tag is created a script release JAR is generated and a GitHub Release is automatically created with the JAR
  attached.

#### Build binary manually

If you prefer to build the binary manually, you can run the following command locally:

```sh
./gradlew :script:scriptJar
```

The resulting JAR file will be located in the `build/obfuscated` folder.

## Modules

| Module             | Description                                                                                |
|--------------------|--------------------------------------------------------------------------------------------|
| command            | Contains the command definition and an entry point to easily execute one or more commands. |
| command-monitoring | Implements the monitoring command used by scripts set up as monitors.                      |
| script             | The actual script made available in the Cereal marketplace.                                |

## Tips

* Use the https://plugins.jetbrains.com/plugin/9960-json-to-kotlin-class-jsontokotlinclass- plugin to generate classes
  from JSON.

# Cereal Public Scripts

This repository contains the source code of the free scripts published on
the [marketplace](https://marketplace.cereal-automation.com)
of the [Cereal Automation](https://www.cereal-automation.com/) platform.

## Getting Started

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

### Creating a release

#### Build binary using GitHub Actions

To create a release, use the following steps:

1. Create a git tag in the format `script-<module>:<version>` (e.g., `script-sample:1.0.0`).
    - The `<module>` corresponds to the script module name.
    - The `<version>` is the version number of the release.

2. Push the tag to the repository. This will trigger the GitHub Actions workflow to:
    - Build the JAR file for the specified module.
    - Update the `version_code` in the `manifest.json` file dynamically based on the version (e.g., `1.0.0` becomes
      `100`).
    - Upload the JAR file as an artifact in the GitHub Actions run.

#### Build binary manually

If you prefer to build the binary manually, you can run the following command locally:

```sh
./gradlew :[script-module]:scriptJar
```

The resulting JAR file will be located in the `build/obfuscated` folder.

### CI/CD

A GitHub actions configuration is included in this repository. It contains the following actions:

* On each push to master tests will run.
* When a tag is created an obfuscated release JAR is generated.

## Modules

| Module             | Description                                                                                                                             |
|--------------------|-----------------------------------------------------------------------------------------------------------------------------------------|
| command            | Contains the command definition and an entry point to easily execute one or more commands.                                              |
| command-monitoring | Implements the monitoring command used by scripts set up as monitors.                                                                   |
| script-common      | Defines the common dependencies for scripts implemented in this repository and contains code useful for setting up a monitoring script. |
| script-*           | The actual scripts made available in the Cereal marketplace.                                                                            |

## Tips

* Use the https://plugins.jetbrains.com/plugin/9960-json-to-kotlin-class-jsontokotlinclass- plugin to generate classes
  from JSON.

## Updating Stockx api client

### 1. OpenAPI Specification

Place the updated Stockx OpenAPI JSON file ([found here](https://developer.stockx.com/openapi/reference/overview))
inside the root `specs/` directory: `specs/stockx.json`.

## 2. Generating the Client

Regenerate the API client from the spec by running:

```bash
./gradlew openApiGenerate
```

This will regenerate the client code in the `stockx-api-client` module.

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

First update the script version in `src/main/resources/manifest.json`

#### Build binary using GitHub Actions

Next, the easiest way to build the script binary is by creating a git tag. This will trigger GitHub Actions to build a
jar and
obfuscate it. This jar is uploaded as artifact and is therefore available in
the [GitHub Actions Artifacts](https://docs.github.com/en/actions/managing-workflow-runs/downloading-workflow-artifacts)
section.

#### Build binary manually

If you are not using GitHub (Actions) or don't want to use this way of creating a jar you can execute
the following command locally or in your custom CI pipeline to get a jar that can be used as a release
in the Cereal Marketplace:

* `./gradlew :[script-module]:scriptJar`

The jar can be found in the `build/obfuscated` folder.

### CI/CD

A GitHub actions configuration is included in this repository. It contains the following actions:

* On each push to master tests will run.
* When a tag is created an obfuscated release JAR is generated.

## Modules

| Module             | Description                                                                                                                                  |
|--------------------|----------------------------------------------------------------------------------------------------------------------------------------------|
| command            | Contains the command definition and an entry point to easily execute one or more commands.                                                   |
| command-monitoring | Implements the monitoring command used by scripts set up as monitors.                                                                       |
| script-common      | Defines the common dependencies for scripts implemented in this repository and contains code useful for setting up a monitoring script.      |
| script-*           | The actual scripts made available in the Cereal marketplace.                                                                                |

## Tips

* Use the https://plugins.jetbrains.com/plugin/9960-json-to-kotlin-class-jsontokotlinclass- plugin to generate classes
  from JSON.

## Updating Stockx api client

### 1. OpenAPI Specification

Place the updated Stockx OpenAPI JSON file ([found here](https://developer.stockx.com/openapi/reference/overview)) inside the root `specs/` directory: `specs/stockx.json`.

## 2. Generating the Client

Regenerate the API client from the spec by running:

```bash
./gradlew openApiGenerate
```

This will regenerate the client code in the `stockx-api-client` module.

# Cereal script template

## Getting Started

### Prerequisites
* Install Java SE Development Kit 11: https://www.oracle.com/java/technologies/downloads/#java11

### Installation
1. Create a new GitHub repository and use this repository as template.
2. Clone your project
```sh
git clone https://github.com/Your-Org/Script-Repo-Name.git
```
3. Open the project in your preferred IDE. We recommend using IntelliJ IDEA because of easier troubleshooting when you need any help.

## Configuring template
* You'll need to update the script name in several files:
  * `settings.gradle.kts` update rootProject.name.
  * `.idea/.name` update the file content.
* Update package_name, name and version in `src/main/resources/manifest.json`.
* Rename the package `com.cereal.script.sample` to something you like.

## Usage
* The repository contains a SampleScript class which is the main entrance for Cereal to start your script. Remove any boilerplate code from that class that you don't need and rename the script to something more descriptive. Do the same for SampleConfiguration.
* Implement the functionality of your script.
* Verify that your script is working properly by running the included test (see TestSampleScript.kt)

### Available gradle commands
* Run all tests
```sh
./gradlew test
```

### Creating a release
First update the script version in `src/main/resources/manifest.json`

#### Build binary using GitHub Actions
Next, the easiest way to build the script binary is by creating a git tag. This will trigger GitHub Actions to build a jar and
obfuscate it. This jar is uploaded as artifact and is therefore available in the [GitHub Actions Artifacts](https://docs.github.com/en/actions/managing-workflow-runs/downloading-workflow-artifacts)
section.

#### Build binary manually
If you are not using GitHub (Actions) or don't want to use this way of creating a jar you can execute
the following command locally or in your custom CI pipeline to get a jar that can be used as a release
in the Cereal Marketplace:

* `./gradlew obfuscateArtifact`

The jar can be found in the `build/obfuscated` folder.

### CI/CD
A GitHub actions configuration is included in this repository. It contains the following actions:

* On each push to master tests will run.
* When a tag is created an obfuscated release JAR is generated.
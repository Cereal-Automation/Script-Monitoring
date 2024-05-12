package com.cereal.script.sample

import com.cereal.licensechecker.LicenseChecker
import com.cereal.licensechecker.LicenseState
import com.cereal.sdk.component.ComponentProvider
import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script

// TODO: Replace this with the script's public key. This can be found in the Cereal Developer Console.
private const val SCRIPT_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
        "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAtL7rXEYD9WcQCGl8D9Ph\n" +
        "wj0WiPG/01+Y3rJyX5TRBfZLNE3hoLOPFDUzQOSy280e90Qv64Ux5plyUuts1Wbk\n" +
        "5vOH5q/TXEhdPixlwrVIAiMayIvV+t8mYCpOJBqaD+cvPQ1DYehUQ3hzax2XSd5O\n" +
        "K3N3r5iPJwtaLBLfSf8E5OnlCcADj8++3q52keTYkpJCrrJVwdJSs23oTq2aQEYj\n" +
        "WeQenq3Pl/J922kWqI8vZJiIb7kmKzcBdZR0zE39/d363dh/KU2c9v5DKFKG2HI6\n" +
        "I3eUkYGTUUqL+pLw9NtY4/tPmHN7FZXJ9rUvAaPk7oQzjSL2cJ1chmtcipUsZAy3\n" +
        "Fneh2HYlmAQpAc0V60DMzw9tQS2UQ5kQDGcC7h7xuAYHZT6jKhnuZon89Bek9qT+\n" +
        "ULgRMjuGTL4rpiMUabPj1IbGVZ6vTwYOjcltERh19MT8QchPo/UBB8W1CK4T3aLf\n" +
        "O3MHnGBeVTlhpBts57lAUGKP8RmGKLpmjL5lA4nw1B7BVzeJ2VuSy8Jhheq75IFp\n" +
        "kGoSrlqfxtA7SE8negMUEq6fca4J/Y5bABH6KHUrMiVaJGLa51Ert4qdOCvfJBlL\n" +
        "Ho/42AejYUJDi/P/fRiC99i6ObNPGXhQ9bz1Quz6F6VAzMjMmHo+OwQ5R2SHq2Yn\n" +
        "KmW5+hWaT3sqkxMw1a2JfTUCAwEAAQ==\n" +
        "-----END PUBLIC KEY-----\n"


class SampleScript : Script<SampleConfiguration> {

    private var isLicensed = false

    override suspend fun onStart(configuration: SampleConfiguration, provider: ComponentProvider): Boolean {
        val licenseChecker = LicenseChecker("com.cereal-automation.samplescript", SCRIPT_PUBLIC_KEY, provider.license())
        val licenseResult = licenseChecker.checkAccess()
        isLicensed = licenseResult is LicenseState.Licensed

        // When there was an error during license validation (i.e. no internet connection or server was down)
        // we want the user to be able to restart the script so license can be checked again so only in that
        // case return false. This stops the script and the user can start it again manually.
        return licenseResult !is LicenseState.ErrorValidatingLicense
    }

    override suspend fun execute(configuration: SampleConfiguration, provider: ComponentProvider, statusUpdate: suspend (message: String) -> Unit): ExecutionResult {
        // Prevent execution when user is not licensed.
        if(!isLicensed) {
            return ExecutionResult.Error("Unlicensed")
        }

        statusUpdate("Start printing configuration...")

        provider.logger().info("Found boolean config value: ${configuration.keyBoolean()}")
        provider.logger().info("Found integer config value: ${configuration.keyInteger()}")
        provider.logger().info("Found float config value: ${configuration.keyFloat()}")
        provider.logger().info("Found string config value: ${configuration.keyString()}")

        return ExecutionResult.Success("Printed configuration")
    }

    override suspend fun onFinish(configuration: SampleConfiguration, provider: ComponentProvider) {
    }
}

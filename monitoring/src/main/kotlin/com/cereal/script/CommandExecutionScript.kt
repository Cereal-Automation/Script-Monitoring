package com.cereal.script

import com.cereal.licensechecker.LicenseChecker
import com.cereal.licensechecker.LicenseState
import com.cereal.script.commands.Command
import com.cereal.script.data.ScriptLogRepository
import com.cereal.script.domain.ExecuteCommandsInteractor
import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.component.ComponentProvider
import kotlinx.coroutines.flow.collect
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.until
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * The Monitor class handles monitoring of items using specified strategies and repositories.
 *
 * @property scriptId Unique identifier of the script.
 * @property scriptPublicKey Optional public key for license validation.
 */
class CommandExecutionScript(
    private val scriptId: String,
    private val scriptPublicKey: String?,
) {
    private var isLicensed = false

    suspend fun onStart(provider: ComponentProvider): Boolean {
        val scriptId = scriptId
        val scriptPublicKey = scriptPublicKey

        if (scriptPublicKey != null) {
            val licenseChecker = LicenseChecker(scriptId, scriptPublicKey, provider.license())
            val licenseResult = licenseChecker.checkAccess()
            isLicensed = licenseResult is LicenseState.Licensed

            // When there was an error during license validation (i.e. no internet connection or server was down)
            // we want the user to be able to restart the script so license can be checked again so only in that
            // case return false. This stops the script and the user can start it again manually.
            return licenseResult !is LicenseState.ErrorValidatingLicense
        } else {
            isLicensed = true
            return true
        }
    }

    suspend fun execute(
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
        commands: List<Command>,
    ): ExecutionResult {
        // Prevent execution when user is not licensed.
        if (!isLicensed) {
            return ExecutionResult.Error("Unlicensed")
        }

        val start = Clock.System.now()
        try {
            val interactor = createInteractor(provider, statusUpdate)
            interactor(commands).collect()
        } catch (e: Exception) {
            return ExecutionResult.Error("Error after ${start.untilNow()} while running script: ${e.message}")
        }

        return ExecutionResult.Success("Script completed successfully in ${start.untilNow()}.")
    }

    fun onFinish() {
        // No-op
    }

    private fun createInteractor(
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): ExecuteCommandsInteractor {
        val logRepository = ScriptLogRepository(provider.logger(), statusUpdate)

        return ExecuteCommandsInteractor(
            logRepository,
        )
    }

    private fun Instant.untilNow(): Duration {
        val now = Clock.System.now()
        return this.until(now, DateTimeUnit.SECOND).seconds
    }
}

package com.cereal.tgtg.command

import com.cereal.script.commands.ChainContext
import com.cereal.script.commands.Command
import com.cereal.script.commands.RunDecision
import com.cereal.script.interactor.UnrecoverableException
import com.cereal.script.repository.LogRepository
import com.cereal.tgtg.domain.TgtgAuthRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Data class to store authentication state in ChainContext between commands.
 */
data class TgtgAuthState(
    val pollingId: String,
    val startTime: Instant,
    val instructionsShown: Boolean = false,
)

/**
 * Command that checks existing TGTG authentication and initiates email authentication if needed.
 */
class TgtgLoginCommand(
    private val tgtgAuthRepository: TgtgAuthRepository,
    private val logRepository: LogRepository,
) : Command {
    override suspend fun shouldRun(context: ChainContext): RunDecision {
        return RunDecision.RunOnce()
    }

    override suspend fun execute(context: ChainContext) {
        logRepository.info("Checking TGTG authentication status...")

        // Try to login with existing credentials
        val loginSuccess = tgtgAuthRepository.login()

        if (loginSuccess) {
            logRepository.info("✅ TGTG authentication successful using existing credentials!")
            return
        }

        // Login failed - need to start interactive authentication
        logRepository.info("🔐 TGTG login failed. Starting interactive authentication...")

        try {
            // Send authentication email
            logRepository.info("📧 Sending authentication email...")

            val authResponse = tgtgAuthRepository.authByEmail()
            val pollingId = authResponse.pollingId

            if (pollingId.isNullOrEmpty()) {
                throw UnrecoverableException("Failed to get polling ID from TGTG. Authentication cannot continue.")
            }

            logRepository.info("✅ Authentication email sent successfully!")

            // Store authentication state in context for the polling command
            val authState =
                TgtgAuthState(
                    pollingId = pollingId,
                    startTime = Clock.System.now(),
                )
            context.put(authState)
        } catch (e: UnrecoverableException) {
            throw e
        } catch (e: Exception) {
            throw UnrecoverableException("Failed to start authentication: ${e.message}", e)
        }
    }

    override fun getDescription(): String = "Initiating TGTG authentication"
}

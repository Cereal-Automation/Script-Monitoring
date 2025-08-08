package com.cereal.tgtg

import com.cereal.command.monitor.data.tgtg.TgtgApiClient
import com.cereal.script.commands.ChainContext
import com.cereal.script.commands.Command
import com.cereal.script.commands.RunDecision
import com.cereal.script.interactor.UnrecoverableException
import com.cereal.script.repository.LogRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.until


/**
 * Command that polls for TGTG authentication completion.
 */
class TgtgAuthPollCommand(
    private val tgtgApiClient: TgtgApiClient,
    private val logRepository: LogRepository,
    private val statusUpdate: suspend (message: String) -> Unit,
) : Command {
    override suspend fun shouldRun(context: ChainContext): RunDecision {
        val authState = context.get<TgtgAuthState>()

        // Only run if we have authentication state (email was sent)
        if (authState == null) {
            return RunDecision.Skip
        }

        // Check for timeout (5 minutes)
        val currentTime = Clock.System.now()
        val elapsedTime = authState.startTime.until(currentTime, DateTimeUnit.MINUTE)

        if (elapsedTime >= 5) {
            // Remove auth state and throw unrecoverable exception
            context.store.removeIf { it is TgtgAuthState }
            throw UnrecoverableException(
                "Authentication timeout reached after 5 minutes. " +
                        "Please restart the script and make sure to click the link in the email.",
            )
        }

        // Continue polling
        return RunDecision.RunRepeat()
    }

    override suspend fun execute(context: ChainContext) {
        val authState =
            context.get<TgtgAuthState>()
                ?: throw UnrecoverableException("Authentication state not found")

        // Show instructions on first poll attempt
        if (!authState.instructionsShown) {
            showAuthenticationInstructions()
            context.put(authState.copy(instructionsShown = true))
        }

        val currentTime = Clock.System.now()
        val elapsedTime = authState.startTime.until(currentTime, DateTimeUnit.MINUTE)

        // Poll for authentication completion
        logRepository.info("🔍 Checking authentication status... (${elapsedTime.toInt() + 1} minutes elapsed)")
        statusUpdate("Checking authentication status... please check your email and click the link")

        try {
            val pollResponse = tgtgApiClient.authPoll(authState.pollingId)

            if (pollResponse.accessToken != null && pollResponse.refreshToken != null) {
                logRepository.info("🎉 Authentication successful! You are now logged in to TGTG.")
                statusUpdate("Authentication successful! TGTG login complete.")
                // Authentication successful - remove state from context
                context.store.removeIf { it is TgtgAuthState }
                return
            }

            // Authentication not yet complete - will be retried by RunDecision.RunRepeat()
            logRepository.info("Authentication not yet completed, continuing to poll...")
        } catch (e: Exception) {
            throw UnrecoverableException("Failed to poll for authentication: ${e.message}", e)
        }
    }

    override fun getDescription(): String = "Polling for TGTG authentication completion"

    /**
     * Shows authentication instructions to the user.
     */
    private suspend fun showAuthenticationInstructions() {
        val emailMessage =
            """
            |
            |📧 AUTHENTICATION REQUIRED 📧
            |
            |An authentication email has been sent to your TGTG account.
            |
            |IMPORTANT INSTRUCTIONS:
            |1. Check your email inbox for a message from Too Good To Go
            |2. Open the email on your PC/computer (NOT on your phone)
            |3. Click the authentication link in the email
            |4. Do NOT open the email on a phone with the TGTG app installed
            |
            |The script will automatically continue once you click the link.
            |Waiting for authentication...
            |
            """.trimMargin()

        logRepository.info(emailMessage)
        statusUpdate("Waiting for email authentication - please check your email and click the link")
    }
}
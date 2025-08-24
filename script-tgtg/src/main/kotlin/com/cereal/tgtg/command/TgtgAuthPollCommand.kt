package com.cereal.tgtg.command

import com.cereal.script.commands.ChainContext
import com.cereal.script.commands.Command
import com.cereal.script.commands.RunDecision
import com.cereal.script.interactor.UnrecoverableException
import com.cereal.script.repository.LogRepository
import com.cereal.sdk.component.userinteraction.UserInteractionComponent
import com.cereal.tgtg.TgtgConfiguration
import com.cereal.tgtg.domain.TgtgAuthRepository

/**
 * Command that waits for the user to complete TGTG authentication.
 */
class TgtgAuthPollCommand(
    private val tgtgAuthRepository: TgtgAuthRepository,
    private val logRepository: LogRepository,
    private val configuration: TgtgConfiguration,
    private val userInteractionComponent: UserInteractionComponent,
) : Command {

    companion object {
        // Public constant so other components/tests can reference the instructions directly
        const val AUTHENTICATION_INSTRUCTIONS: String = """

AUTHENTICATION REQUIRED

An authentication email has been sent to your TGTG account.

IMPORTANT INSTRUCTIONS:
1. Check your email inbox for a message from Too Good To Go
2. Open the email on your PC/computer (NOT on your phone)
3. Click the authentication link in the email
4. Do NOT open the email on a phone with the TGTG app installed

Press Continue here after you clicked the link.

"""
    }

    override suspend fun shouldRun(context: ChainContext): RunDecision {
        val authState = context.get<TgtgAuthState>()

        // Only run if we have authentication state (email was sent)
        if (authState == null) {
            // No auth state means either auth never started or already completed. Skip running.
            return RunDecision.Skip
        }

        // Allow the user to try again by repeating until authentication succeeds.
        return RunDecision.RunRepeat()
    }

    override suspend fun execute(context: ChainContext) {
        val authState =
            context.get<TgtgAuthState>()
                ?: throw UnrecoverableException("Authentication state not found")

        // Show instructions on first attempt; on subsequent attempts show a shorter prompt.
        if (!authState.instructionsShown) {
            val message = AUTHENTICATION_INSTRUCTIONS
            // Log as well, so the instructions are visible in logs
            logRepository.info(message)
            context.put(authState.copy(instructionsShown = true))
        }

        // Wait for the user to confirm they clicked the email link
        try {
            userInteractionComponent.showContinueButton()
        } catch (e: Exception) {
            throw UnrecoverableException("Failed to show continue button: ${e.message}", e)
        }

        // After the user pressed continue, check authentication status
        try {
            val isAuthenticated = tgtgAuthRepository.authPoll(authState.pollingId, configuration.email())
            if (isAuthenticated) {
                logRepository.info("Authentication successful! You are now logged in to TGTG.")
                context.store.removeIf { it is TgtgAuthState }
                return
            } else {
                // Let the user try again on the next iteration.
                logRepository.info("Authentication not completed. If you did not click the email link yet, please do so and press Continue to try again.")
                return
            }
        } catch (e: UnrecoverableException) {
            throw e
        } catch (e: Exception) {
            throw UnrecoverableException("Failed to check authentication: ${e.message}", e)
        }
    }

    override fun getDescription(): String = "Awaiting user confirmation for TGTG authentication"
}

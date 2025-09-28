package com.cereal.tgtg.command

import com.cereal.script.commands.ChainContext
import com.cereal.script.commands.Command
import com.cereal.script.commands.RunDecision
import com.cereal.script.interactor.UnrecoverableException
import com.cereal.script.repository.LogRepository
import com.cereal.sdk.component.userinteraction.UserInteractionComponent
import com.cereal.tgtg.TgtgConfiguration
import com.cereal.tgtg.command.context.TgtgAuthState
import com.cereal.tgtg.domain.TgtgAuthRepository
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

/**
 * Command that waits for the user to complete TGTG authentication.
 */
@OptIn(ExperimentalTime::class)
class TgtgAuthPollCommand(
    private val tgtgAuthRepository: TgtgAuthRepository,
    private val logRepository: LogRepository,
    private val configuration: TgtgConfiguration,
    private val userInteractionComponent: UserInteractionComponent,
) : Command {
    companion object {
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
        // Only run if we have authentication state (email was sent)
        context.get<TgtgAuthState>()
            ?: // No auth state means either auth never started or already completed. Skip running.
            return RunDecision.Skip

        // Allow the user to try again by repeating until authentication succeeds. Add some delay to prevent spamming the API.
        return RunDecision.RunRepeat(startDelay = 15.seconds)
    }

    override suspend fun execute(context: ChainContext) {
        val authState =
            context.get<TgtgAuthState>()
                ?: throw UnrecoverableException("Authentication state not found")

        if (!authState.instructionsShown) {
            logRepository.info(AUTHENTICATION_INSTRUCTIONS)
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
                logRepository.info(
                    "Authentication not completed. If you did not click the email link yet, please do so and press Continue to try again.",
                )
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

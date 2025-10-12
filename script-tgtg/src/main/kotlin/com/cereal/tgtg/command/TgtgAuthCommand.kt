package com.cereal.tgtg.command

import com.cereal.script.commands.ChainContext
import com.cereal.script.commands.Command
import com.cereal.script.commands.RunDecision
import com.cereal.script.interactor.UnrecoverableException
import com.cereal.script.repository.LogRepository
import com.cereal.tgtg.command.context.TgtgAuthState
import com.cereal.tgtg.domain.TgtgAuthRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Command that checks existing TGTG authentication and initiates email authentication if needed.
 */
@OptIn(ExperimentalTime::class)
class TgtgAuthCommand(
    private val tgtgAuthRepository: TgtgAuthRepository,
    private val logRepository: LogRepository,
    private val configuration: com.cereal.tgtg.TgtgConfiguration,
) : Command {
    override suspend fun shouldRun(context: ChainContext): RunDecision = RunDecision.RunOnce()

    override suspend fun execute(context: ChainContext) {
        logRepository.info("Checking TGTG authentication status...")

        // Try to login with existing credentials
        val loginSuccess = tgtgAuthRepository.login()

        if (loginSuccess) {
            logRepository.info("TGTG authentication successful using existing credentials!")
            return
        }

        // Login failed - need to start interactive authentication
        logRepository.info("TGTG login failed. Starting interactive authentication...")

        try {
            // Send authentication email
            logRepository.info("Sending authentication email...")

            val authResult = tgtgAuthRepository.authByEmail(configuration.email())
            val pollingId =
                authResult.pollingId ?: throw UnrecoverableException("Failed to obtain polling ID from auth result")

            logRepository.info("Authentication email sent successfully!")

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

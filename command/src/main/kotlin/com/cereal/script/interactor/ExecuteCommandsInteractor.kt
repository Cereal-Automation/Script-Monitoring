package com.cereal.script.interactor

import com.cereal.script.commands.ChainContext
import com.cereal.script.commands.Command
import com.cereal.script.commands.RunDecision
import com.cereal.script.exception.RestartableException
import com.cereal.script.repository.LogRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen

/**
 * Interactor responsible for executing a chain of commands in sequence.
 *
 * This class handles the execution flow of commands, including:
 * - Sequential execution with context propagation
 * - Command repetition based on RunDecision
 * - Restart capabilities for error recovery
 * - Proper context updates between commands
 */
class ExecuteCommandsInteractor(
    private val logRepository: LogRepository,
) {
    /**
     * Executes a list of commands in sequence, propagating context between them.
     *
     * @param commands The list of commands to execute
     * @param startContext The initial context for command execution
     * @return A flow emitting updated context after each command execution
     */
    operator fun invoke(
        commands: List<Command>,
        startContext: ChainContext,
    ): Flow<ChainContext> =
        flow {
            var currentContext = startContext

            for (command in commands) {
                var decision: RunDecision
                do {
                    decision = command.shouldRun(currentContext)
                    if (decision is RunDecision.Skip) break

                    applyDecisionDelay(decision)

                    // Execute the command and emit updates
                    executeCommand(command, currentContext)
                        .collect { emitted ->
                            currentContext = emitted
                            emit(currentContext)
                        }
                } while (decision is RunDecision.RunRepeat)
            }
        }.retryWhen { cause, _ ->
            if (cause is RestartableException) {
                logRepository.info("Restarting command chain due to an error")
                true
            } else {
                false
            }
        }

    /**
     * Applies appropriate delay based on the run decision.
     */
    private suspend fun applyDecisionDelay(runDecision: RunDecision) {
        when (runDecision) {
            is RunDecision.RunOnce, is RunDecision.RunRepeat -> {
                val delayDuration =
                    when (runDecision) {
                        is RunDecision.RunOnce -> runDecision.startDelay
                        is RunDecision.RunRepeat -> runDecision.startDelay
                        else -> throw IllegalStateException("Unexpected RunDecision type")
                    }
                if (delayDuration.isPositive()) delay(delayDuration)
            }

            is RunDecision.Skip -> { /* No delay needed */ }
        }
    }

    /**
     * Executes a given command within a specific context.
     *
     * This function is responsible for running the command's execution logic,
     * emitting the updated context, and handling retries and logging as needed.
     *
     * @param command The command to be executed, which encapsulates the specific logic of the action.
     * @param context The context in which to execute the command
     * @return A flow that emits updated ChainContext after executing the command
     */
    private fun executeCommand(
        command: Command,
        context: ChainContext,
    ): Flow<ChainContext> =
        flow {
            command.execute(context)
            emit(context)
        }.withRetry(command.getDescription(), logRepository)
            .withLogging(command.getDescription(), logRepository)
}

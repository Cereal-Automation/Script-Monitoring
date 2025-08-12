package com.cereal.script.interactor

import com.cereal.script.commands.ChainContext
import com.cereal.script.commands.Command
import com.cereal.script.commands.RunDecision
import com.cereal.script.exception.RestartableException
import com.cereal.script.repository.LogRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

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
            var currentCommandIndex = 0

            while (currentCommandIndex < commands.size) {
                val command = commands[currentCommandIndex]

                try {
                    val result = processCommand(command, currentContext, currentCommandIndex)
                    currentContext = result.updatedContext
                    currentCommandIndex = result.nextIndex

                    // Only emit if the command was actually executed (not skipped)
                    if (result.wasExecuted) {
                        emit(currentContext)
                    }
                } catch (e: Exception) {
                    when (e) {
                        is RestartableException -> {
                            // Continue the loop (restart already set up)
                            logRepository.info("Caught RestartableException, continuing with restart")
                        }
                        else -> throw e
                    }
                }
            }
        }

    /**
     * Result of processing a command, containing the updated context, next command index,
     * and whether the command was actually executed.
     */
    private data class CommandProcessingResult(
        val updatedContext: ChainContext,
        val nextIndex: Int,
        val wasExecuted: Boolean,
    )

    /**
     * Processes a single command, handling execution, repetition, and restart logic.
     *
     * @param command The command to process
     * @param context The current chain context
     * @param currentIndex The current command index
     * @return A CommandProcessingResult with updated context and next index
     */
    private suspend fun FlowCollector<ChainContext>.processCommand(
        command: Command,
        context: ChainContext,
        currentIndex: Int,
    ): CommandProcessingResult {
        var updatedContext = context
        var shouldRestartChain = false
        var wasExecuted = false

        do {
            val runDecision = command.shouldRun(updatedContext)

            if (runDecision is RunDecision.Skip) break

            applyDecisionDelay(runDecision)

            updatedContext =
                executeCommandWithRestartHandling(
                    command,
                    updatedContext,
                    onRestartNeeded = { shouldRestartChain = true },
                )
            wasExecuted = true
        } while (runDecision is RunDecision.RunRepeat)

        val nextIndex = if (shouldRestartChain) 0 else currentIndex + 1
        return CommandProcessingResult(updatedContext, nextIndex, wasExecuted)
    }

    /**
     * Applies appropriate delay based on the run decision.
     */
    private suspend fun applyDecisionDelay(runDecision: RunDecision) {
        when (runDecision) {
            is RunDecision.RunOnce, is RunDecision.RunRepeat -> {
                val delay =
                    when (runDecision) {
                        is RunDecision.RunOnce -> runDecision.startDelay
                        is RunDecision.RunRepeat -> runDecision.startDelay
                        else -> throw IllegalStateException("Unexpected RunDecision type")
                    }
                if (delay.isPositive()) delay(delay)
            }
            is RunDecision.Skip -> { /* No delay needed */ }
        }
    }

    /**
     * Executes a command with restart exception handling.
     *
     * @param command The command to execute
     * @param context The current context
     * @param onRestartNeeded Callback when restart is needed
     * @return Updated context after command execution
     */
    private suspend fun FlowCollector<ChainContext>.executeCommandWithRestartHandling(
        command: Command,
        context: ChainContext,
        onRestartNeeded: () -> Unit,
    ): ChainContext {
        var updatedContext = context

        // Execute the command and collect the result without emitting to the outer flow
        // This matches the original behavior where the context is only emitted in the main flow
        executeCommand(command, context)
            .catch { e ->
                if (e is RestartableException) {
                    logRepository.info("Restarting command chain due to: ${e.message}")
                    onRestartNeeded()
                    throw e
                } else {
                    throw e
                }
            }
            .collect {
                updatedContext = it
            }

        return updatedContext
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

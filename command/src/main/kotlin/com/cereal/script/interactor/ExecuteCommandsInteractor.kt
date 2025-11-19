package com.cereal.script.interactor

import com.cereal.script.commands.ChainContext
import com.cereal.script.commands.Command
import com.cereal.script.commands.RunDecision
import com.cereal.script.exception.InvalidChainContextException
import com.cereal.script.repository.LogRepository
import kotlinx.coroutines.CancellationException
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
    companion object {
        private const val CHAIN_RESTART_MAX_ATTEMPTS = 5
    }

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
    ): Flow<ChainContext> {
        // Snapshot the initial context state at invocation time to avoid retaining mutations on restarts
        val initialSnapshot = copyContext(startContext)
        var attemptIndex = 0

        return flow {
            // For the first attempt, use the original startContext instance (preserves existing expectations/tests)
            // For subsequent attempts (after a restart), use a fresh copy of the initial snapshot
            var currentContext = if (attemptIndex == 0) startContext else copyContext(initialSnapshot)

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
                    
                    // Log waiting time before next run
                    if (decision is RunDecision.RunRepeat && decision.startDelay.isPositive()) {
                        val seconds = decision.startDelay.inWholeSeconds
                        logRepository.info("Waiting $seconds seconds before running again.")
                    }
                } while (decision is RunDecision.RunRepeat)
            }
        }.retryWhen { cause, _ ->
            when (cause) {
                is CancellationException -> false
                is InvalidChainContextException -> {
                    if (attemptIndex < CHAIN_RESTART_MAX_ATTEMPTS) {
                        logRepository.info("Restarting command chain due to an error")
                        attemptIndex += 1
                        true
                    } else {
                        logRepository.info("Maximum restarts reached ($CHAIN_RESTART_MAX_ATTEMPTS). Aborting.")
                        false
                    }
                }
                else -> false
            }
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

    /**
     * Create a shallow copy of the given ChainContext.
     * We copy the store list into a new ChainContext to avoid retaining mutations across restarts.
     */
    private fun copyContext(from: ChainContext): ChainContext {
        val copy = ChainContext()
        copy.store.addAll(from.store)
        return copy
    }
}

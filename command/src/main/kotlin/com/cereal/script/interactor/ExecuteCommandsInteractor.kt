package com.cereal.script.interactor

import com.cereal.script.commands.ChainContext
import com.cereal.script.commands.Command
import com.cereal.script.commands.RunDecision
import com.cereal.script.repository.LogRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class ExecuteCommandsInteractor(
    private val logRepository: LogRepository,
) {
    operator fun invoke(
        commands: List<Command>,
        startContext: ChainContext,
    ): Flow<ChainContext> =
        flow {
            var context = startContext

            for (command in commands) {
                // Repeat as long as this command wants to run.
                while (true) {
                    val shouldRun = command.shouldRun(context)
                    if (shouldRun is RunDecision.Skip) break
                    if (shouldRun is RunDecision.RunWithDelay) delay(shouldRun.delay)

                    emitAll(
                        executeCommand(command, context).onEach {
                            context = it
                        },
                    )
                }
            }
        }

    /**
     * Executes a given command within a specific context.
     *
     * This function is responsible for running the command's execution logic,
     * emitting the updated context, and handling retries and logging as needed.
     *
     * @param command The command to be executed, which encapsulates the specific logic of the action.
     * @return A flow that emits `ChainContext` from executing the command, retrying if needed.
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

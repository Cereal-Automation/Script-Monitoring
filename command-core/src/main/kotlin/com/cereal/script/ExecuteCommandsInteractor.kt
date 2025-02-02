package com.cereal.script

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
     * Executes a given command, emitting the result of each execution. If the command
     * returns a result of `CommandResult.Repeat`, it will be re-executed and re-emitted
     * until a different result is obtained. The execution flow incorporates retry and logging
     * mechanisms to handle errors and log the process.
     *
     * @param command The command to be executed, which encapsulates the specific logic of the action.
     * @return A flow that emits `CommandResult` from executing the command, retrying if needed.
     */
    private fun executeCommand(
        command: Command,
        context: ChainContext,
    ): Flow<ChainContext> =
        flow {
            emit(command.execute(context))
        }.withRetry(command.getDescription(), logRepository)
            .withLogging(command.getDescription(), logRepository)
}

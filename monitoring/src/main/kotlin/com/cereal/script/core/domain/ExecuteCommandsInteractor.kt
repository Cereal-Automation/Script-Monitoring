package com.cereal.script.core.domain

import com.cereal.script.commands.Command
import com.cereal.script.commands.CommandResult
import com.cereal.script.core.domain.repository.LogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ExecuteCommandsInteractor(
    private val logRepository: LogRepository,
) {
    operator fun invoke(commands: List<Command>): Flow<Unit> =
        flow {
            for (command in commands) {
                emitAll(executeCommand(command))
            }
        }.map {}

    /**
     * Executes a given command, emitting the result of each execution. If the command
     * returns a result of `CommandResult.Repeat`, it will be re-executed and re-emitted
     * until a different result is obtained. The execution flow incorporates retry and logging
     * mechanisms to handle errors and log the process.
     *
     * @param command The command to be executed, which encapsulates the specific logic of the action.
     * @return A flow that emits `CommandResult` from executing the command, retrying if needed.
     */
    private fun executeCommand(command: Command): Flow<CommandResult> =
        flow {
            var result: CommandResult
            do {
                result = command.execute()
                emit(result)
            } while (result == CommandResult.Repeat)
        }.withRetry(command.getDescription(), logRepository)
            .withLogging(command.getDescription(), logRepository)
}

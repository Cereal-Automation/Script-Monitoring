package com.cereal.script.commands.helloworld

import com.cereal.script.commands.Command
import com.cereal.script.commands.CommandResult
import kotlinx.coroutines.delay

class DelayCommand(
    private val delay: Long = 1,
) : Command {
    override suspend fun shouldRun(): Boolean = true

    override suspend fun execute(): CommandResult {
        delay(delay)
        return CommandResult.Completed
    }

    override fun getDescription(): String = "Delay for $delay milliseconds and completes."
}

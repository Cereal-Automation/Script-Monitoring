package com.cereal.script.commands.delay

import com.cereal.script.commands.ChainContext
import com.cereal.script.commands.Command
import com.cereal.script.commands.RunDecision
import kotlinx.coroutines.delay

class DelayCommand(
    private val delay: Long = 1,
) : Command {
    override suspend fun shouldRun(context: ChainContext): RunDecision = RunDecision.RunNow

    override suspend fun execute(context: ChainContext): ChainContext {
        delay(delay)
        return context
    }

    override fun getDescription(): String = "Delay for $delay milliseconds and completes."
}

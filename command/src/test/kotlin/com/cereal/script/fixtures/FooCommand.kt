package com.cereal.shared.fixtures

import com.cereal.script.commands.ChainContext
import com.cereal.script.commands.Command
import com.cereal.script.commands.RunDecision

class FooCommand(
    private val numberOfRuns: Int = 1,
) : Command {
    private var runCount = 0

    override suspend fun shouldRun(context: ChainContext): RunDecision {
        if (runCount == numberOfRuns) {
            return RunDecision.Skip
        } else {
            runCount++
            return RunDecision.RunOnce()
        }
    }

    override suspend fun execute(context: ChainContext) {
        println("Executing FooCommand")
    }

    override fun getDescription(): String = "Foo"
}

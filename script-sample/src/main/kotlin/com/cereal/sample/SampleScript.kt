package com.cereal.sample

import com.cereal.script.commands.CommandFactory
import com.cereal.script.commands.monitor.data.rss.RssFeedItemRepository
import com.cereal.script.commands.monitor.strategy.NewItemAvailableMonitorStrategy
import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script
import com.cereal.sdk.component.ComponentProvider
import com.cereal.shared.CommandExecutionScript
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.seconds

class SampleScript : Script<SampleConfiguration> {
    private val commandExecutionScript =
        CommandExecutionScript(
            scriptId = "com.cereal-automation.monitor.sample",
            scriptPublicKey = null,
        )

    override suspend fun onStart(
        configuration: SampleConfiguration,
        provider: ComponentProvider,
    ): Boolean = commandExecutionScript.onStart(provider)

    override suspend fun execute(
        configuration: SampleConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): ExecutionResult {
        val factory = CommandFactory(provider)

        val strategies = listOf(NewItemAvailableMonitorStrategy(Clock.System.now()))
        val commands =
            listOf(
                factory.createMonitorCommand(
                    itemRepository =
                        RssFeedItemRepository(
                            "https://feeds.rijksoverheid.nl/nieuws.rss",
                            provider.logger(),
                        ),
                    strategies,
                    configuration.monitorInterval()?.seconds,
                    statusUpdate,
                    "Sample Script",
                ),
            )
        return commandExecutionScript.execute(provider, statusUpdate, commands)
    }

    override suspend fun onFinish(
        configuration: SampleConfiguration,
        provider: ComponentProvider,
    ) {
        commandExecutionScript.onFinish()
    }
}

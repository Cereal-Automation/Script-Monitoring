package com.cereal.sample

import com.cereal.command.monitor.MonitorCommandFactory
import com.cereal.command.monitor.data.rss.RssFeedItemRepository
import com.cereal.command.monitor.strategy.NewItemAvailableMonitorStrategy
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
        val factory = MonitorCommandFactory(provider)

        val logRepository = factory.logRepository(statusUpdate)
        val notificationRepository = factory.notificationRepository("Sample Script")

        val strategies = listOf(NewItemAvailableMonitorStrategy(Clock.System.now()))
        val commands =
            listOf(
                factory.monitorCommand(
                    itemRepository =
                        RssFeedItemRepository(
                            "https://feeds.rijksoverheid.nl/nieuws.rss",
                            provider.logger(),
                        ),
                    logRepository,
                    notificationRepository,
                    strategies,
                    configuration.monitorInterval()?.seconds,
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

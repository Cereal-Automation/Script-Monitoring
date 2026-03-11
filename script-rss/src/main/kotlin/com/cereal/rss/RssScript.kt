package com.cereal.rss

import com.cereal.command.monitor.MonitorCommandFactory
import com.cereal.command.monitor.data.rss.RssFeedItemRepository
import com.cereal.command.monitor.strategy.MonitorStrategy
import com.cereal.command.monitor.strategy.MonitorStrategyFactory
import com.cereal.script.CommandExecutionScript
import com.cereal.script.commands.Command
import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script
import com.cereal.sdk.component.ComponentProvider
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class RssScript : Script<RssConfiguration> {
    private val commandExecutionScript =
        CommandExecutionScript(
            scriptId = "com.cereal-automation.monitor.rss",
            scriptPublicKey = null,
        )

    override suspend fun onStart(
        configuration: RssConfiguration,
        provider: ComponentProvider,
    ): Boolean = commandExecutionScript.onStart(provider)

    override suspend fun execute(
        configuration: RssConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): ExecutionResult {
        val commands = buildCommands(configuration, provider, statusUpdate)
        return commandExecutionScript.execute(provider, statusUpdate, commands)
    }

    override suspend fun onFinish(
        configuration: RssConfiguration,
        provider: ComponentProvider,
    ) {
        commandExecutionScript.onFinish()
    }

    private fun buildCommands(
        configuration: RssConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): List<Command> {
        val factory = MonitorCommandFactory(provider)
        val logRepository = factory.logRepository(statusUpdate)
        val notificationRepository = factory.notificationRepository("RSS Monitor")
        val itemRepository =
            RssFeedItemRepository(
                rssFeedUrl = configuration.rssUrl(),
                logger = provider.logger(),
            )

        val strategies = buildMonitorStrategies(configuration)

        return listOf(
            factory.monitorCommand(
                itemRepository = itemRepository,
                logRepository = logRepository,
                notificationRepository = notificationRepository,
                strategies = strategies,
                scrapeInterval = configuration.monitorInterval()?.seconds,
            ),
        )
    }

    private fun buildMonitorStrategies(configuration: RssConfiguration): List<MonitorStrategy> =
        buildList {
            if (configuration.monitorNewItems()) {
                add(MonitorStrategyFactory.newItemAvailableMonitorStrategy(Clock.System.now()))
            }
        }
}

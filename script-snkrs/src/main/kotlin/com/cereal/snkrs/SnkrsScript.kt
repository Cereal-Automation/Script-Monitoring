package com.cereal.snkrs

import com.cereal.command.monitor.MonitorCommandFactory
import com.cereal.command.monitor.data.snkrs.SnkrsApiClient
import com.cereal.command.monitor.data.snkrs.SnkrsItemRepository
import com.cereal.command.monitor.strategy.MonitorStrategy
import com.cereal.command.monitor.strategy.MonitorStrategyFactory
import com.cereal.script.CommandExecutionScript
import com.cereal.script.commands.Command
import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script
import com.cereal.sdk.component.ComponentProvider
import kotlin.time.Duration.Companion.seconds

class SnkrsScript : Script<SnkrsConfiguration> {
    private val commandExecutionScript =
        CommandExecutionScript(
            scriptId = "com.cereal.snkrs.monitor",
            scriptPublicKey = null,
        )

    override suspend fun onStart(
        configuration: SnkrsConfiguration,
        provider: ComponentProvider,
    ): Boolean = commandExecutionScript.onStart(provider)

    override suspend fun execute(
        configuration: SnkrsConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): ExecutionResult {
        val commands = buildCommands(configuration, provider, statusUpdate)
        return commandExecutionScript.execute(
            provider,
            statusUpdate,
            commands,
        )
    }

    override suspend fun onFinish(
        configuration: SnkrsConfiguration,
        provider: ComponentProvider,
    ) {
        commandExecutionScript.onFinish()
    }

    /**
     * Constructs a list of commands based on the provided configuration, component provider, and status update function.
     *
     * @param configuration The configuration settings which contain information such as the category to monitor, proxy settings, and optionally a monitor interval.
     * @param provider The component provider used to supply necessary components for command creation.
     * @param statusUpdate A suspend function that accepts a message string, allowing for real-time status updates during command execution.
     * @return A list of commands generated based on the provided configuration and component provider.
     */
    private fun buildCommands(
        configuration: SnkrsConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): List<Command> {
        val factory = MonitorCommandFactory(provider)
        val monitorStrategies = buildMonitorStrategies(configuration)

        val logRepository = factory.logRepository(statusUpdate)
        val notificationRepository = factory.notificationRepository("Nike SNKRS")
        val nikeRepository =
            SnkrsItemRepository(
                snkrsApiClient = SnkrsApiClient(logRepository),
                locale = configuration.locale(),
            )

        return listOf(
            factory.monitorCommand(
                nikeRepository,
                logRepository,
                notificationRepository,
                monitorStrategies,
                configuration.monitorInterval()?.seconds,
            ),
        )
    }

    private fun buildMonitorStrategies(configuration: SnkrsConfiguration): List<MonitorStrategy> =
        buildList {
            if (configuration.monitorPriceDrops()) {
                add(MonitorStrategyFactory.priceDropMonitorStrategy())
            }
            if (configuration.monitorStockChanges()) {
                add(MonitorStrategyFactory.stockAvailableMonitorStrategy())
            }
        }
}

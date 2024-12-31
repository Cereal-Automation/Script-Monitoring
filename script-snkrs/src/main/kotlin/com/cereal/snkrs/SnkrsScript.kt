package com.cereal.snkrs

import com.cereal.script.commands.Command
import com.cereal.script.commands.CommandFactory
import com.cereal.script.commands.monitor.data.snkrs.SnkrsApiClient
import com.cereal.script.commands.monitor.data.snkrs.SnkrsItemRepository
import com.cereal.script.commands.monitor.strategy.MonitorStrategy
import com.cereal.script.commands.monitor.strategy.PriceDropMonitorStrategy
import com.cereal.script.commands.monitor.strategy.StockAvailableMonitorStrategy
import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script
import com.cereal.sdk.component.ComponentProvider
import com.cereal.shared.CommandExecutionScript
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
        return commandExecutionScript.execute(provider, statusUpdate, commands)
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
        val factory = CommandFactory(provider)
        val monitorStrategies = buildMonitorStrategies(configuration)

        val nikeRepository =
            SnkrsItemRepository(
                snkrsApiClient = SnkrsApiClient(),
                locale = configuration.locale(),
            )

        return listOf(
            factory.createMonitorCommand(
                nikeRepository,
                monitorStrategies,
                configuration.monitorInterval()?.seconds,
                statusUpdate,
                "Nike SNKRS",
            ),
        )
    }

    private fun buildMonitorStrategies(configuration: SnkrsConfiguration): List<MonitorStrategy> =
        buildList {
            if (configuration.monitorPriceDrops()) {
                add(PriceDropMonitorStrategy())
            }
            if (configuration.monitorStockChanges()) {
                add(StockAvailableMonitorStrategy())
            }
        }
}

package com.cereal.nike

import com.cereal.script.commands.Command
import com.cereal.script.commands.CommandFactory
import com.cereal.script.commands.monitor.data.nike.NikeItemRepository
import com.cereal.script.commands.monitor.strategy.MonitorStrategy
import com.cereal.script.commands.monitor.strategy.NewItemAvailableMonitorStrategy
import com.cereal.script.commands.monitor.strategy.PriceDropMonitorStrategy
import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script
import com.cereal.sdk.component.ComponentProvider
import com.cereal.shared.CommandExecutionScript
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.seconds

class NikeScript : Script<NikeConfiguration> {
    private val commandExecutionScript =
        CommandExecutionScript(
            scriptId = "com.cereal-automation.monitor.nike",
            scriptPublicKey = null,
        )

    override suspend fun onStart(
        configuration: NikeConfiguration,
        provider: ComponentProvider,
    ): Boolean = commandExecutionScript.onStart(provider)

    override suspend fun execute(
        configuration: NikeConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): ExecutionResult {
        val commands = buildCommands(configuration, provider, statusUpdate)
        return commandExecutionScript.execute(provider, statusUpdate, commands)
    }

    override suspend fun onFinish(
        configuration: NikeConfiguration,
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
        configuration: NikeConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): List<Command> {
        val factory = CommandFactory(provider)
        val monitorStrategies = buildMonitorStrategies(configuration)

        val logRepository = factory.logRepository(statusUpdate)
        val notificationRepository = factory.notificationRepository("Nike")
        val nikeRepository =
            NikeItemRepository(
                logRepository = logRepository,
                category = configuration.category(),
                randomProxy = configuration.proxy(),
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

    private fun buildMonitorStrategies(configuration: NikeConfiguration): List<MonitorStrategy> =
        buildList {
            if (configuration.monitorNewProduct()) {
                add(NewItemAvailableMonitorStrategy(Clock.System.now()))
            }
            if (configuration.monitorPriceDrops()) {
                add(PriceDropMonitorStrategy())
            }
        }
}

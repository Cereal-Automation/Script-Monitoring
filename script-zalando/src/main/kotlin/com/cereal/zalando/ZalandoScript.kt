package com.cereal.zalando

import com.cereal.command.monitor.MonitorCommandFactory
import com.cereal.command.monitor.data.zalando.ZalandoItemRepository
import com.cereal.command.monitor.strategy.MonitorStrategy
import com.cereal.command.monitor.strategy.MonitorStrategyFactory
import com.cereal.command.monitor.strategy.NewItemAvailableMonitorStrategy
import com.cereal.command.monitor.strategy.PriceDropMonitorStrategy
import com.cereal.script.CommandExecutionScript
import com.cereal.script.commands.Command
import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script
import com.cereal.sdk.component.ComponentProvider
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.seconds

class ZalandoScript : Script<ZalandoConfiguration> {
    private val commandExecutionScript =
        CommandExecutionScript(
            scriptId = "com.cereal-automation.monitor.zalando",
            scriptPublicKey = null,
        )

    override suspend fun onStart(
        configuration: ZalandoConfiguration,
        provider: ComponentProvider,
    ): Boolean = commandExecutionScript.onStart(provider)

    override suspend fun execute(
        configuration: ZalandoConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): ExecutionResult {
        val commands = buildCommands(configuration, provider, statusUpdate)
        return commandExecutionScript.execute(provider, statusUpdate, commands)
    }

    override suspend fun onFinish(
        configuration: ZalandoConfiguration,
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
        configuration: ZalandoConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): List<Command> {
        val factory = MonitorCommandFactory(provider)
        val monitorStrategies = buildMonitorStrategies(configuration)

        val logRepository = factory.logRepository(statusUpdate)
        val notificationRepository = factory.notificationRepository("Zalando")
        val nikeRepository =
            ZalandoItemRepository(
                logRepository = logRepository,
                website = configuration.website(),
                category = configuration.category(),
                monitorType = configuration.monitorType(),
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

    private fun buildMonitorStrategies(configuration: ZalandoConfiguration): List<MonitorStrategy> =
        buildList {
            if (configuration.monitorNewProduct()) {
                add(MonitorStrategyFactory.newItemAvailableMonitorStrategy(Clock.System.now()))
            }
            if (configuration.monitorPriceDrops()) {
                add(MonitorStrategyFactory.priceDropMonitorStrategy())
            }
        }
}

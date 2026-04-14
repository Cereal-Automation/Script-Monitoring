package com.cereal.rental

import com.cereal.command.monitor.MonitorCommandFactory
import com.cereal.command.monitor.data.rental.FundaItemRepository
import com.cereal.command.monitor.data.rental.ParariusItemRepository
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
class RentalScript : Script<RentalConfiguration> {
    private val commandExecutionScript =
        CommandExecutionScript(
            scriptId = "com.cereal-automation.monitor.rental",
            scriptPublicKey = null,
        )

    override suspend fun onStart(
        configuration: RentalConfiguration,
        provider: ComponentProvider,
    ): Boolean = commandExecutionScript.onStart(provider)

    override suspend fun execute(
        configuration: RentalConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): ExecutionResult {
        val commands = buildCommands(configuration, provider, statusUpdate)
        return commandExecutionScript.execute(provider, statusUpdate, commands)
    }

    override suspend fun onFinish(
        configuration: RentalConfiguration,
        provider: ComponentProvider,
    ) {
        commandExecutionScript.onFinish()
    }

    private fun buildCommands(
        configuration: RentalConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): List<Command> {
        val factory = MonitorCommandFactory(provider)
        val logRepository = factory.logRepository(statusUpdate)
        val notificationRepository = factory.notificationRepository("Dutch Rental Monitor")
        val cities = parseCities(configuration.cities())
        val furnishing = configuration.furnishing()
        val propertyType = configuration.propertyType()
        val strategy = MonitorStrategyFactory.newItemAvailableMonitorStrategy(Clock.System.now(), requiresBaseline = false)
        val strategies = listOf(strategy)

        return buildList {
            if (configuration.enablePararius()) {
                val parariusRepository =
                    ParariusItemRepository(
                        cities = cities,
                        maxPrice = configuration.maxPrice(),
                        minSizeM2 = configuration.minSizeM2(),
                        minRooms = configuration.minRooms(),
                        furnishing = furnishing,
                        propertyType = propertyType,
                        randomProxy = null,
                        logRepository = logRepository,
                    )
                add(
                    factory.monitorCommand(
                        itemRepository = parariusRepository,
                        logRepository = logRepository,
                        notificationRepository = notificationRepository,
                        strategies = strategies,
                        scrapeInterval = configuration.monitorInterval()?.let { it * 60 }?.seconds,
                    ),
                )
            }

            if (configuration.enableFunda()) {
                val fundaRepository =
                    FundaItemRepository(
                        cities = cities,
                        maxPrice = configuration.maxPrice(),
                        minSizeM2 = configuration.minSizeM2(),
                        minRooms = configuration.minRooms(),
                        furnishing = furnishing,
                        propertyType = propertyType,
                        logRepository = logRepository,
                        randomProxy = null,
                    )
                add(
                    factory.monitorCommand(
                        itemRepository = fundaRepository,
                        logRepository = logRepository,
                        notificationRepository = notificationRepository,
                        strategies = strategies,
                        scrapeInterval = configuration.monitorInterval()?.let { it * 60 }?.seconds,
                    ),
                )
            }
        }
    }

    private fun parseCities(input: String): List<String> = input.split(",").map { it.trim().lowercase() }.filter { it.isNotBlank() }
}

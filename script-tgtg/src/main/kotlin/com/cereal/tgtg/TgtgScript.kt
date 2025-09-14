package com.cereal.tgtg

import com.cereal.command.monitor.MonitorCommandFactory
import com.cereal.command.monitor.data.common.cache.PreferenceCacheManager
import com.cereal.command.monitor.data.tgtg.TgtgItemRepository
import com.cereal.command.monitor.data.tgtg.apiclients.PlayStoreApiClient
import com.cereal.command.monitor.data.tgtg.apiclients.TgtgApiClient
import com.cereal.command.monitor.strategy.MonitorStrategy
import com.cereal.command.monitor.strategy.NewItemAvailableMonitorStrategy
import com.cereal.command.monitor.strategy.StockAvailableMonitorStrategy
import com.cereal.script.CommandExecutionScript
import com.cereal.script.commands.Command
import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script
import com.cereal.sdk.component.ComponentProvider
import com.cereal.tgtg.command.TgtgAuthPollCommand
import com.cereal.tgtg.command.TgtgLoginCommand
import com.cereal.tgtg.data.TgtgAuthRepositoryImpl
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

class TgtgScript : Script<TgtgConfiguration> {
    private val commandExecutionScript =
        CommandExecutionScript(
            scriptId = "com.cereal-automation.monitor.tgtg",
            scriptPublicKey = null,
        )

    override suspend fun onStart(
        configuration: TgtgConfiguration,
        provider: ComponentProvider,
    ): Boolean = commandExecutionScript.onStart(provider)

    override suspend fun execute(
        configuration: TgtgConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): ExecutionResult {
        val commands = buildCommands(configuration, provider, statusUpdate)
        return commandExecutionScript.execute(provider, statusUpdate, commands)
    }

    override suspend fun onFinish(
        configuration: TgtgConfiguration,
        provider: ComponentProvider,
    ) {
        commandExecutionScript.onFinish()
    }

    private suspend fun buildCommands(
        configuration: TgtgConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): List<Command> {
        val factory = MonitorCommandFactory(provider)
        val monitorStrategies = buildMonitorStrategies()

        val logRepository = factory.logRepository(statusUpdate)
        val notificationRepository = factory.notificationRepository("TGTG")

        // Create TGTG API client and repository
        val cacheManager = PreferenceCacheManager(provider.preference())
        val playStoreApiClient = PlayStoreApiClient(logRepository, cacheManager)
        val tgtgApiClient =
            TgtgApiClient(
                logRepository = logRepository,
                preferenceComponent = provider.preference(),
                playStoreApiClient = playStoreApiClient,
                httpProxy = configuration.proxy()?.invoke(),
            )

        val tgtgRepository =
            TgtgItemRepository(
                tgtgApiClient = tgtgApiClient,
                latitude = configuration.latitude(),
                longitude = configuration.longitude(),
                radius = configuration.radius() ?: 50000,
                favoritesOnly = configuration.favoritesOnly(),
            )

        // Create auth repository
        val tgtgAuthRepository = TgtgAuthRepositoryImpl(tgtgApiClient)

        // Create login commands
        val loginCommand =
            TgtgLoginCommand(
                tgtgAuthRepository = tgtgAuthRepository,
                logRepository = logRepository,
                configuration = configuration,
            )

        val authPollCommand =
            TgtgAuthPollCommand(
                tgtgAuthRepository = tgtgAuthRepository,
                logRepository = logRepository,
                configuration = configuration,
                userInteractionComponent = provider.userInteraction(),
            )

        // Create monitor command
        val monitorCommand =
            factory.monitorCommand(
                tgtgRepository,
                logRepository,
                notificationRepository,
                monitorStrategies,
                configuration.monitorInterval()?.seconds,
            )

        // Return commands in order: login, poll for auth, then monitor
        return listOf(
            loginCommand,
            authPollCommand,
            monitorCommand,
        )
    }

    @OptIn(ExperimentalTime::class)
    private fun buildMonitorStrategies(): List<MonitorStrategy> =
        listOf(
            NewItemAvailableMonitorStrategy(Clock.System.now()),
            StockAvailableMonitorStrategy(notifyOnInitialRun = true),
        )
}

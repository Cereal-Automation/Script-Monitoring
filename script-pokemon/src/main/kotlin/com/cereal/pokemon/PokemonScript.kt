package com.cereal.pokemon

import com.cereal.command.monitor.MonitorCommandFactory
import com.cereal.command.monitor.data.bolcom.BolcomItemRepository
import com.cereal.command.monitor.models.ItemFilter
import com.cereal.command.monitor.strategy.NewItemAvailableMonitorStrategy
import com.cereal.command.monitor.strategy.StockAvailableMonitorStrategy
import com.cereal.script.CommandExecutionScript
import com.cereal.script.commands.Command
import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script
import com.cereal.sdk.component.ComponentProvider
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class PokemonScript : Script<PokemonConfiguration> {
    private val commandExecutionScript =
        CommandExecutionScript(
            scriptId = "com.cereal-automation.monitor.pokemon",
            scriptPublicKey = null,
        )

    override suspend fun onStart(
        configuration: PokemonConfiguration,
        provider: ComponentProvider,
    ): Boolean = commandExecutionScript.onStart(provider)

    override suspend fun execute(
        configuration: PokemonConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): ExecutionResult {
        val commands = buildCommands(configuration, provider, statusUpdate)
        return commandExecutionScript.execute(provider, statusUpdate, commands)
    }

    override suspend fun onFinish(
        configuration: PokemonConfiguration,
        provider: ComponentProvider,
    ) {
        commandExecutionScript.onFinish()
    }

    private fun buildCommands(
        configuration: PokemonConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): List<Command> {
        val factory = MonitorCommandFactory(provider)
        val logRepository = factory.logRepository(statusUpdate)
        val notificationRepository = factory.notificationRepository("Pokémon Monitor")

        val repository = BolcomItemRepository(logRepository, SEARCH_TERMS)

        val filters =
            buildList<ItemFilter> {
                configuration.maxPrice()?.let { add(ItemFilter.PriceAtMost(it.toBigDecimal())) }
            }

        val strategies =
            listOf(
                // Use case 1: notify when a brand-new product (release/drop) is listed.
                NewItemAvailableMonitorStrategy(Clock.System.now(), requiresBaseline = true),
                // Use case 2: notify when a staged drop goes live or an older product is restocked.
                StockAvailableMonitorStrategy(notifyOnInitialRun = false),
            )

        return listOf(
            factory.monitorCommand(
                itemRepository = repository,
                logRepository = logRepository,
                notificationRepository = notificationRepository,
                strategies = strategies,
                scrapeInterval = configuration.monitorInterval()?.seconds,
                filters = filters,
            ),
        )
    }

    companion object {
        /**
         * Hard-coded list of bol.com search terms, one per Pokémon TCG set/product line.
         *
         * This mirrors the notification taxonomy used by the StockMonster Pokémon community, which
         * tracks bol.com drops/restocks by set name rather than by product format. Refresh this list
         * as new expansions release.
         */
        private val SEARCH_TERMS =
            listOf(
                // Scarlet & Violet era
                "pokémon scarlet & violet",
                "pokémon obsidian flames",
                "pokémon 151",
                "pokémon paldea evolved",
                "pokémon paradox rift",
                "pokémon paldean fates",
                "pokémon temporal forces",
                "pokémon twilight masquerade",
                "pokémon shrouded fable",
                "pokémon stellar crown",
                "pokémon surging sparks",
                "pokémon prismatic evolutions",
                "pokémon journey together",
                "pokémon destined rivals",
                "pokémon black bolt",
                "pokémon white flare",
                // Mega Evolution era
                "pokémon mega evolution",
                "pokémon phantasmal flames",
                "pokémon ascended heroes",
                "pokémon perfect order",
                "pokémon chaos rising",
                "pokémon pitch black",
            )
    }
}

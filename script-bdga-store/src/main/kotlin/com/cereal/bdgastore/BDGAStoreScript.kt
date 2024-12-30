package com.cereal.bdgastore

import com.cereal.script.commands.CommandFactory
import com.cereal.script.commands.monitor.data.shopify.ShopifyItemRepository
import com.cereal.script.commands.monitor.data.shopify.ShopifyWebsite
import com.cereal.script.commands.monitor.strategy.StockAvailableMonitorStrategy
import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script
import com.cereal.sdk.component.ComponentProvider
import com.cereal.shared.CommandExecutionScript
import kotlin.time.Duration.Companion.seconds

class BDGAStoreScript : Script<BDGAStoreConfiguration> {
    private val commandExecutionScript =
        CommandExecutionScript(
            scriptId = "com.cereal-automation.monitor.bdgastore",
            scriptPublicKey = null,
        )

    override suspend fun onStart(
        configuration: BDGAStoreConfiguration,
        provider: ComponentProvider,
    ): Boolean = commandExecutionScript.onStart(provider)

    override suspend fun execute(
        configuration: BDGAStoreConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): ExecutionResult {
        val factory = CommandFactory(provider)

        val strategies = listOf(StockAvailableMonitorStrategy())
        val commands =
            listOf(
                factory.createMonitorCommand(
                    itemRepository =
                        ShopifyItemRepository(
                            website = ShopifyWebsite("New Arrivals", "https://bdgastore.com/collections/newarrivals"),
                            randomProxy = configuration.proxy(),
                        ),
                    strategies,
                    configuration.monitorInterval()?.seconds,
                    statusUpdate,
                    "BDGAStore Script",
                ),
            )
        return commandExecutionScript.execute(provider, statusUpdate, commands)
    }

    override suspend fun onFinish(
        configuration: BDGAStoreConfiguration,
        provider: ComponentProvider,
    ) {
        commandExecutionScript.onFinish()
    }
}

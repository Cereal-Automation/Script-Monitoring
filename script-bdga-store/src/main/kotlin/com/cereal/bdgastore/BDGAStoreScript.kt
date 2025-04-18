package com.cereal.bdgastore

import com.cereal.command.monitor.MonitorCommandFactory
import com.cereal.command.monitor.data.shopify.ShopifyItemRepository
import com.cereal.command.monitor.data.shopify.ShopifyWebsite
import com.cereal.command.monitor.strategy.StockAvailableMonitorStrategy
import com.cereal.script.CommandExecutionScript
import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script
import com.cereal.sdk.component.ComponentProvider
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
        val factory = MonitorCommandFactory(provider)

        val logRepository = factory.logRepository(statusUpdate)
        val notificationRepository = factory.notificationRepository("BDGAStore")

        val strategies = listOf(StockAvailableMonitorStrategy())
        val commands =
            listOf(
                factory.monitorCommand(
                    itemRepository =
                        ShopifyItemRepository(
                            logRepository = logRepository,
                            website = ShopifyWebsite("New Arrivals", "https://bdgastore.com/collections/newarrivals"),
                            randomProxy = configuration.proxy(),
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
        configuration: BDGAStoreConfiguration,
        provider: ComponentProvider,
    ) {
        commandExecutionScript.onFinish()
    }
}

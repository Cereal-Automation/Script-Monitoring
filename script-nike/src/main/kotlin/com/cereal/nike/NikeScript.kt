package com.cereal.nike

import com.cereal.script.monitoring.Monitor
import com.cereal.script.monitoring.data.item.nike.NikeApiItemRepository
import com.cereal.script.monitoring.domain.strategy.NewItemAvailableMonitorStrategy
import com.cereal.script.monitoring.domain.strategy.PriceDropMonitorStrategy
import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script
import com.cereal.sdk.component.ComponentProvider
import java.time.Instant
import kotlin.time.Duration.Companion.seconds

class NikeScript : Script<NikeConfiguration> {
    private lateinit var monitor: Monitor

    override suspend fun onStart(
        configuration: NikeConfiguration,
        provider: ComponentProvider,
    ): Boolean {
        val strategies =
            buildList {
                if (configuration.monitorNewProduct()) {
                    add(NewItemAvailableMonitorStrategy(Instant.now()))
                }
                if (configuration.monitorPriceDrops()) {
                    add(PriceDropMonitorStrategy())
                }
            }

        monitor =
            Monitor(
                scriptId = "com.cereal-automation.monitor.nike",
                scriptPublicKey = null,
                strategies = strategies,
                itemRepository = NikeApiItemRepository(configuration.categoryUrl()),
                sleep = configuration.monitorInterval()?.seconds,
            )

        return monitor.onStart(provider)
    }

    override suspend fun execute(
        configuration: NikeConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): ExecutionResult = monitor.execute(provider, statusUpdate)

    override suspend fun onFinish(
        configuration: NikeConfiguration,
        provider: ComponentProvider,
    ) {
        monitor.onFinish()
    }
}

package com.cereal.sample

import com.cereal.script.monitoring.Monitor
import com.cereal.script.monitoring.data.item.rss.RssFeedItemRepository
import com.cereal.script.monitoring.domain.strategy.NewItemAvailableMonitorStrategy
import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script
import com.cereal.sdk.component.ComponentProvider
import java.time.Instant
import kotlin.time.Duration.Companion.seconds

class SampleScript : Script<SampleConfiguration> {
    private lateinit var monitor: Monitor

    override suspend fun onStart(
        configuration: SampleConfiguration,
        provider: ComponentProvider,
    ): Boolean {
        monitor =
            Monitor(
                scriptId = "com.cereal-automation.monitor.sample",
                scriptPublicKey = null,
                strategies = listOf(NewItemAvailableMonitorStrategy(Instant.now())),
                itemRepository = RssFeedItemRepository("https://feeds.rijksoverheid.nl/nieuws.rss", provider.logger()),
                sleep = configuration.monitorInterval()?.seconds,
            )
        return monitor.onStart(provider)
    }

    override suspend fun execute(
        configuration: SampleConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit,
    ): ExecutionResult = monitor.execute(provider, statusUpdate)

    override suspend fun onFinish(
        configuration: SampleConfiguration,
        provider: ComponentProvider,
    ) {
        monitor.onFinish()
    }
}

package com.cereal.sample

import com.cereal.script.monitoring.ItemMonitor
import com.cereal.script.monitoring.domain.models.DataSource
import com.cereal.script.monitoring.domain.models.MonitorMode
import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script
import com.cereal.sdk.component.ComponentProvider
import java.time.Instant


class SampleScript : Script<SampleConfiguration> {

    private val itemMonitor = ItemMonitor(
        scriptId = "com.cereal-automation.sample-monitor",
        scriptPublicKey = null,
        monitorMode = MonitorMode.NewItemAvailable(Instant.now()),
        dataSource = DataSource.RssFeed("https://feeds.rijksoverheid.nl/nieuws.rss")
    )

    override suspend fun onStart(configuration: SampleConfiguration, provider: ComponentProvider): Boolean {
        return itemMonitor.onStart(provider)
    }

    override suspend fun execute(
        configuration: SampleConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit
    ): ExecutionResult {
        return itemMonitor.execute(provider, statusUpdate)
    }

    override suspend fun onFinish(configuration: SampleConfiguration, provider: ComponentProvider) {
        itemMonitor.onFinish(provider)
    }

}

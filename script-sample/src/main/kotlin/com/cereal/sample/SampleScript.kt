package com.cereal.sample

import com.cereal.script.monitoring.MonitorFactory
import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script
import com.cereal.sdk.component.ComponentProvider
import kotlin.time.Duration.Companion.seconds


class SampleScript : Script<SampleConfiguration> {

    private val monitor = MonitorFactory.createSampleMonitor()

    override suspend fun onStart(configuration: SampleConfiguration, provider: ComponentProvider): Boolean {
        return monitor.onStart(provider)
    }

    override suspend fun execute(
        configuration: SampleConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit
    ): ExecutionResult {
        return monitor.execute(provider, statusUpdate, sleep = configuration.monitorInterval()?.seconds)
    }

    override suspend fun onFinish(configuration: SampleConfiguration, provider: ComponentProvider) {
        monitor.onFinish()
    }

}

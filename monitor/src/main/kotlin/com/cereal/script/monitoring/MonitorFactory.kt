package com.cereal.script.monitoring

import com.cereal.script.monitoring.domain.models.DataSource
import com.cereal.script.monitoring.domain.models.MonitorMode
import java.time.Instant

object MonitorFactory {
    val allMonitors =
        listOf(
            createSampleMonitor(),
        )

    fun createSampleMonitor(): Monitor {
        return Monitor(
            scriptId = "com.cereal-automation.sample-monitor",
            scriptPublicKey = null,
            monitorMode = MonitorMode.NewItemAvailable(Instant.now()),
            dataSource = DataSource.RssFeed("https://feeds.rijksoverheid.nl/nieuws.rss"),
        )
    }
}

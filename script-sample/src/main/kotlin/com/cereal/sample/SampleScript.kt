package com.cereal.sample

import com.cereal.script.monitoring.MonitoringScript
import com.cereal.script.monitoring.domain.models.DataSource
import com.cereal.script.monitoring.domain.models.MonitorMode
import java.time.Instant


class SampleScript : MonitoringScript<SampleConfiguration>() {

    override val scriptId = null
    override val scriptPublicKey = null
    override val monitorMode = MonitorMode.NewItemAvailable(Instant.now())
    override val dataSource = DataSource.RssFeed("https://feeds.rijksoverheid.nl/nieuws.rss")
}

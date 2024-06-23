package com.cereal.sample

import com.cereal.script.monitoring.MonitoringScript
import com.cereal.script.monitoring.data.rssfeed.RssFeedItemMonitorRepository
import com.cereal.script.monitoring.domain.models.MonitorMode
import com.cereal.script.monitoring.domain.repository.ItemMonitorRepository


class SampleScript : MonitoringScript<SampleConfiguration>() {

    override val scriptId = null
    override val scriptPublicKey = null
    override val monitorMode = MonitorMode.NewItem

    override fun getItemMonitorRepository(): ItemMonitorRepository {
        return RssFeedItemMonitorRepository("https://feeds.rijksoverheid.nl/nieuws.rss")
    }
}

package com.cereal.sample

import com.cereal.script.monitoring.MonitoringScript
import com.cereal.script.monitoring.data.item.RssFeedItemRepository
import com.cereal.script.monitoring.domain.models.DataSource
import com.cereal.script.monitoring.domain.models.MonitorMode
import com.cereal.script.monitoring.domain.repository.ItemRepository
import java.time.Instant
import java.util.*


class SampleScript : MonitoringScript<SampleConfiguration>() {

    override val scriptId = null
    override val scriptPublicKey = null
    override val monitorMode = MonitorMode.NewItem(Date.from(Instant.now()))
    override val dataSource = DataSource.RssFeed("https://feeds.rijksoverheid.nl/nieuws.rss")
}

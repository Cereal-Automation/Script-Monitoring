package com.cereal.script.monitoring

import com.cereal.script.monitoring.data.ScriptLogRepository
import com.cereal.script.monitoring.data.ScriptNotificationRepository
import com.cereal.script.monitoring.data.item.RssFeedItemRepository
import com.cereal.script.monitoring.domain.MonitorInteractor
import com.cereal.script.monitoring.domain.models.DataSource
import com.cereal.script.monitoring.domain.repository.ItemRepository
import com.cereal.sdk.component.ComponentProvider

class MonitorFactory(private val provider: ComponentProvider, private val dataSource: DataSource) {

    fun createInteractor(statusUpdate: suspend (message: String) -> Unit): MonitorInteractor {
        val notificationRepository = ScriptNotificationRepository(provider.preference(), provider.notification())
        val logRepository = ScriptLogRepository(provider.logger(), statusUpdate)
        return MonitorInteractor(getItemMonitorRepository(provider), notificationRepository, logRepository)
    }

    private fun getItemMonitorRepository(provider: ComponentProvider): ItemRepository {
        return when(val source = dataSource) {
            is DataSource.RssFeed -> RssFeedItemRepository(source.rssFeedUrl, provider.logger())
        }
    }
}

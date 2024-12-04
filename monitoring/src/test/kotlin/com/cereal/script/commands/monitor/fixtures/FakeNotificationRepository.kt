package com.cereal.script.commands.monitor.fixtures

import com.cereal.script.commands.monitor.domain.NotificationRepository
import com.cereal.script.commands.monitor.domain.models.Item

class FakeNotificationRepository : NotificationRepository {
    private val notifiedItems = mutableListOf<Item>()

    override suspend fun setItemNotified(item: Item) {
        notifiedItems.add(item)
    }

    override suspend fun isItemNotified(item: Item): Boolean = notifiedItems.contains(item)

    override suspend fun notify(message: String) {
        // No-op
    }
}

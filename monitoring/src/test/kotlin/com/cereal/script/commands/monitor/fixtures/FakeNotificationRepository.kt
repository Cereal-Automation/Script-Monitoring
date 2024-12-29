package com.cereal.script.commands.monitor.fixtures

import com.cereal.script.commands.monitor.models.Item
import com.cereal.script.commands.monitor.repository.NotificationRepository

class FakeNotificationRepository : NotificationRepository {
    override suspend fun notify(
        message: String,
        item: Item,
    ) {
        // No-op
    }
}

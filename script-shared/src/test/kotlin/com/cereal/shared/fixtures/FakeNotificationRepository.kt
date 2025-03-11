package com.cereal.shared.fixtures

import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.repository.NotificationRepository

class FakeNotificationRepository : NotificationRepository {
    override suspend fun notify(
        message: String,
        item: Item,
    ) {
        // No-op
    }
}

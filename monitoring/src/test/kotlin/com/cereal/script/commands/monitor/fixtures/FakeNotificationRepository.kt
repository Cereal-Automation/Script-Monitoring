package com.cereal.script.commands.monitor.fixtures

import com.cereal.script.commands.monitor.domain.NotificationRepository
import com.cereal.script.commands.monitor.domain.models.Item

class FakeNotificationRepository : NotificationRepository {
    override suspend fun notify(
        message: String,
        item: Item,
    ) {
        // No-op
    }
}

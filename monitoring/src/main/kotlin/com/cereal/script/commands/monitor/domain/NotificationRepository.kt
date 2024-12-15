package com.cereal.script.commands.monitor.domain

import com.cereal.script.commands.monitor.domain.models.Item

interface NotificationRepository {
    suspend fun notify(
        message: String,
        item: Item,
    )
}

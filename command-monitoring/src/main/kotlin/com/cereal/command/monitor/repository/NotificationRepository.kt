package com.cereal.command.monitor.repository

import com.cereal.command.monitor.models.Item

interface NotificationRepository {
    suspend fun notify(
        message: String,
        item: Item,
    )
}

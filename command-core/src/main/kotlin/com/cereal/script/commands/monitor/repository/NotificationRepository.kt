package com.cereal.script.commands.monitor.repository

import com.cereal.script.commands.monitor.models.Item

interface NotificationRepository {
    suspend fun notify(
        message: String,
        item: Item,
    )
}

package com.cereal.script.commands.monitor.domain

import com.cereal.script.commands.monitor.domain.models.Item

interface NotificationRepository {
    suspend fun setItemNotified(item: Item)

    suspend fun isItemNotified(item: Item): Boolean

    suspend fun notify(message: String)
}

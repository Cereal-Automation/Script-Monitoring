package com.cereal.script.monitoring.domain.repository

import com.cereal.script.monitoring.domain.models.Item

interface NotificationRepository {
    suspend fun setItemNotified(item: Item)

    suspend fun isItemNotified(item: Item): Boolean

    suspend fun notify(message: String)
}

package com.cereal.script.monitoring.data

import com.cereal.script.monitoring.domain.models.Item
import com.cereal.script.monitoring.domain.repository.NotificationRepository
import com.cereal.sdk.component.notification.NotificationComponent
import com.cereal.sdk.component.notification.notification
import com.cereal.sdk.component.preference.PreferenceComponent

class ScriptNotificationRepository(
    private val preferenceComponent: PreferenceComponent,
    private val notificationComponent: NotificationComponent,
) : NotificationRepository {
    override suspend fun setItemNotified(item: Item) {
        preferenceComponent.setBoolean(item.isNotifiedKey(), true)
    }

    override suspend fun isItemNotified(item: Item): Boolean = preferenceComponent.getBoolean(item.isNotifiedKey()) ?: false

    override suspend fun notify(message: String) {
        val notification =
            notification(message) {
            }
        notificationComponent.sendNotification(notification)
    }
}

private fun Item.isNotifiedKey(): String = "item_notification_$id"

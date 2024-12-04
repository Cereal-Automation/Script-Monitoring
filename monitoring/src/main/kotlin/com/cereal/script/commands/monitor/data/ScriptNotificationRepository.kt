package com.cereal.script.commands.monitor.data

import com.cereal.script.commands.monitor.domain.NotificationRepository
import com.cereal.script.commands.monitor.domain.models.Item
import com.cereal.sdk.component.notification.NotificationComponent
import com.cereal.sdk.component.notification.notification
import com.cereal.sdk.component.preference.PreferenceComponent

private fun Item.isNotifiedKey(): String = "item_notification_$id"

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

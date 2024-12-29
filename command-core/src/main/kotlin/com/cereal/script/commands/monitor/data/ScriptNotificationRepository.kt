package com.cereal.script.commands.monitor.data

import com.cereal.script.commands.monitor.models.Item
import com.cereal.script.commands.monitor.repository.NotificationRepository
import com.cereal.sdk.component.notification.NotificationComponent
import com.cereal.sdk.component.notification.notification
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Repository responsible for handling the notification creation and dispatch process.
 * It utilizes the provided notification component and configures the message format
 * for integration with services like Discord.
 *
 * @param notificationComponent A component responsible for dispatching notifications.
 * @param discordUsername The username displayed in the Discord message.
 * @param discordAvatarUrl The optional avatar URL for the Discord user.
 * @param discordColor The color of the message embed in Discord, expressed as a stringified integer. Defaults to white.
 */
class ScriptNotificationRepository(
    private val notificationComponent: NotificationComponent,
    private val discordUsername: String,
    private val discordAvatarUrl: String? = null,
    private val discordColor: String = "16777215",
) : NotificationRepository {
    override suspend fun notify(
        message: String,
        item: Item,
    ) {
        val currentTime: Instant = Clock.System.now()

        val notification =
            notification(message) {
                discordMessage {
                    username = discordUsername
                    avatarUrl = discordAvatarUrl
                    embed {
                        title = message
                        description = item.description
                        url = item.url
                        thumbnail {
                            url = item.imageUrl
                        }
                        color = discordColor
                        footer {
                            text = "Generated by Cereal Automation"
                        }
                        timestamp =
                            currentTime.toString() // This gives YYYY-MM-DDTHH:MM:SS.MSSZ which is required by Discord.
                        item.properties.forEach {
                            field {
                                name = it.commonName
                                value = it.toString()
                            }
                        }
                    }
                }
            }
        notificationComponent.sendNotification(notification)
    }
}
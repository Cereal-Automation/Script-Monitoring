package com.cereal.command.monitor.data

import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.command.monitor.repository.NotificationRepository
import com.cereal.sdk.component.notification.NotificationComponent
import com.cereal.sdk.component.notification.notification
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

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
@OptIn(ExperimentalTime::class)
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
                            text = "Provided by Cereal"
                            iconUrl = "https://i.imgur.com/bJGntxN.png"
                        }
                        timestamp =
                            currentTime.toString() // This gives YYYY-MM-DDTHH:MM:SS.MSSZ which is required by Discord.
                        item.properties.forEach {
                            field {
                                val icon = it.getDiscordFieldNameIcon()
                                name = if (icon != null) "$icon ${it.commonName}" else it.commonName
                                value = it.getDiscordFieldValue()
                            }
                        }
                    }
                }
            }
        notificationComponent.sendNotification(notification)
    }

    private fun ItemProperty.getDiscordFieldNameIcon(): String? {
        return when (this) {
            is ItemProperty.Price -> ":moneybag:"
            is ItemProperty.Custom -> null
            is ItemProperty.PublishDate -> ":alarm_clock:"
            is ItemProperty.Stock -> ":bar_chart:"
        }
    }

    private fun ItemProperty.getDiscordFieldValue(): String {
        return when (this) {
            is ItemProperty.Stock -> if (this.isInStock) "✅" else "❌"
            else -> this.toString()
        }
    }
}

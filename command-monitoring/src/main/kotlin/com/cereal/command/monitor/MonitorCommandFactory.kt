package com.cereal.command.monitor

import com.cereal.command.monitor.data.ScriptNotificationRepository
import com.cereal.command.monitor.repository.ItemRepository
import com.cereal.command.monitor.repository.NotificationRepository
import com.cereal.command.monitor.strategy.MonitorStrategy
import com.cereal.script.data.ScriptLogRepository
import com.cereal.script.repository.LogRepository
import com.cereal.sdk.component.ComponentProvider
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class MonitorCommandFactory(
    private val provider: ComponentProvider,
) {
    fun monitorCommand(
        itemRepository: ItemRepository,
        logRepository: LogRepository,
        notificationRepository: NotificationRepository,
        strategies: List<MonitorStrategy>,
        scrapeInterval: Duration? = null,
    ): MonitorCommand =
        MonitorCommand(
            itemRepository = itemRepository,
            notificationRepository = notificationRepository,
            logRepository = logRepository,
            delayBetweenScrapes = scrapeInterval ?: Random.nextInt(15, 31).seconds,
            strategies = strategies,
        )

    fun logRepository(statusUpdate: suspend (message: String) -> Unit): LogRepository = ScriptLogRepository(provider.logger(), statusUpdate)

    fun notificationRepository(discordUsername: String): NotificationRepository =
        ScriptNotificationRepository(
            provider.notification(),
            discordUsername = discordUsername,
        )
}

package com.cereal.script.commands

import com.cereal.script.commands.monitor.MonitorCommand
import com.cereal.script.commands.monitor.data.ScriptNotificationRepository
import com.cereal.script.commands.monitor.domain.ItemRepository
import com.cereal.script.commands.monitor.domain.NotificationRepository
import com.cereal.script.commands.monitor.strategy.MonitorStrategy
import com.cereal.script.core.data.ScriptLogRepository
import com.cereal.script.core.domain.repository.LogRepository
import com.cereal.sdk.component.ComponentProvider
import kotlin.time.Duration

class CommandFactory(
    private val provider: ComponentProvider,
) {
    fun createMonitorCommand(
        itemRepository: ItemRepository,
        strategies: List<MonitorStrategy>,
        scrapeInterval: Duration? = null,
        statusUpdate: suspend (message: String) -> Unit,
        discordUsername: String,
    ): MonitorCommand =
        MonitorCommand(
            itemRepository = itemRepository,
            notificationRepository = createNotificationRepository(discordUsername),
            logRepository = createLogRepository(statusUpdate),
            delayBetweenScrapes = scrapeInterval ?: Duration.ZERO,
            strategies = strategies,
        )

    private fun createLogRepository(statusUpdate: suspend (message: String) -> Unit): LogRepository =
        ScriptLogRepository(provider.logger(), statusUpdate)

    private fun createNotificationRepository(discordUsername: String): NotificationRepository =
        ScriptNotificationRepository(
            provider.notification(),
            discordUsername = discordUsername,
        )
}

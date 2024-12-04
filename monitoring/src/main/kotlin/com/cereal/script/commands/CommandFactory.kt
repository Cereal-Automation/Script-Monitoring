package com.cereal.script.commands

import com.cereal.script.commands.monitor.MonitorCommand
import com.cereal.script.commands.monitor.data.ScriptNotificationRepository
import com.cereal.script.commands.monitor.domain.ItemRepository
import com.cereal.script.commands.monitor.strategy.MonitorStrategy
import com.cereal.script.core.data.ScriptLogRepository
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
    ): MonitorCommand =
        MonitorCommand(
            itemRepository = itemRepository,
            notificationRepository =
                ScriptNotificationRepository(
                    provider.preference(),
                    provider.notification(),
                ),
            logRepository = ScriptLogRepository(provider.logger(), statusUpdate),
            delayBetweenScrapes = scrapeInterval ?: Duration.ZERO,
            strategies = strategies,
        )
}

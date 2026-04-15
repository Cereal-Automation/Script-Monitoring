package com.cereal.command.monitor

import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.repository.ItemRepository
import com.cereal.command.monitor.repository.NotificationRepository
import com.cereal.command.monitor.strategy.ExecuteStrategyCommand
import com.cereal.command.monitor.strategy.MonitorStrategy
import com.cereal.script.commands.ChainContext
import com.cereal.script.commands.Command
import com.cereal.script.commands.RunDecision
import com.cereal.script.repository.LogRepository
import kotlin.time.Duration

class MonitorCommand(
    private val itemRepositories: List<ItemRepository>,
    private val notificationRepository: NotificationRepository,
    private val logRepository: LogRepository,
    private val delayBetweenScrapes: Duration,
    private val strategies: List<MonitorStrategy>,
) : Command {
    override suspend fun shouldRun(context: ChainContext): RunDecision {
        val monitorStatus = context.get<MonitorStatus>()

        val startDelay = monitorStatus?.monitorItems?.let { delayBetweenScrapes } ?: Duration.ZERO
        return RunDecision.RunRepeat(startDelay)
    }

    override suspend fun execute(context: ChainContext) {
        val monitorStatus = context.getOrCreate<MonitorStatus> { MonitorStatus() }

        var totalNumberOfItems = 0
        var totalNotifications = 0
        val items: MutableMap<String, Item> = monitorStatus.monitorItems?.toMutableMap() ?: hashMapOf()

        for (itemRepository in itemRepositories) {
            var nextPageToken: String? = null
            do {
                val message =
                    nextPageToken?.let {
                        "Retrieving items from $it"
                    } ?: "Retrieving items from first page"
                logRepository.info(message)

                val page = itemRepository.getItems(nextPageToken)
                logRepository.debug("Retrieved ${page.items.size} items.")

                totalNotifications += tryExecuteStrategies(page.items, monitorStatus.monitorItems)
                page.items.forEach { item -> items[item.id] = item }
                totalNumberOfItems += page.items.size
                nextPageToken = page.nextPageToken
            } while (nextPageToken != null)
        }

        logRepository.info(
            "Found and processed a total of $totalNumberOfItems items.",
        )

        if (totalNotifications == 0) {
            logRepository.info("No items matched the configured filters — no notifications sent.")
        }

        return context.put(
            monitorStatus.copy(
                monitorItems = items,
                monitorRunSequenceNumber = monitorStatus.monitorRunSequenceNumber + 1,
            ),
        )
    }

    override fun getDescription(): String = "Monitoring new products"

    private suspend fun tryExecuteStrategies(
        items: List<Item>,
        existingItems: Map<String, Item>?,
    ): Int {
        var notifications = 0
        items.forEach { item ->
            logRepository.debug("Processing item: ${item.id} - ${item.name}")
            strategies.forEach { strategy ->
                if (!strategy.requiresBaseline() || existingItems != null) {
                    val notified =
                        ExecuteStrategyCommand(
                            notificationRepository,
                            logRepository,
                            strategy,
                            item,
                            existingItems?.get(item.id),
                        ).execute()
                    if (notified) notifications++
                } else {
                    logRepository.debug(
                        "Skipping strategy ${strategy::class.simpleName} for item ${item.id} - requires baseline but none available",
                    )
                }
            }
        }
        return notifications
    }
}

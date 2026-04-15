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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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

        val allItems =
            coroutineScope {
                itemRepositories
                    .map { repository -> async { fetchAllItems(repository) } }
                    .awaitAll()
                    .flatten()
            }

        val items: MutableMap<String, Item> = monitorStatus.monitorItems?.toMutableMap() ?: hashMapOf()
        val totalNotifications = tryExecuteStrategies(allItems, monitorStatus.monitorItems)
        allItems.forEach { item -> items[item.id] = item }

        logRepository.info(
            "Found and processed a total of ${allItems.size} items.",
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

    private suspend fun fetchAllItems(itemRepository: ItemRepository): List<Item> {
        val items = mutableListOf<Item>()
        var nextPageToken: String? = null
        do {
            val message =
                nextPageToken?.let {
                    "Retrieving items from $it"
                } ?: "Retrieving items from first page"
            logRepository.info(message)

            val page = itemRepository.getItems(nextPageToken)
            logRepository.debug("Retrieved ${page.items.size} items.")

            items += page.items
            nextPageToken = page.nextPageToken
        } while (nextPageToken != null)
        return items
    }

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

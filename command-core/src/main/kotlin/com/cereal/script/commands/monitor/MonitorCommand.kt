package com.cereal.script.commands.monitor

import com.cereal.script.commands.ChainContext
import com.cereal.script.commands.Command
import com.cereal.script.commands.RunDecision
import com.cereal.script.commands.monitor.models.Item
import com.cereal.script.commands.monitor.repository.ItemRepository
import com.cereal.script.commands.monitor.repository.NotificationRepository
import com.cereal.script.commands.monitor.strategy.ExecuteStrategyCommand
import com.cereal.script.commands.monitor.strategy.MonitorStrategy
import com.cereal.script.repository.LogRepository
import kotlin.time.Duration

class MonitorCommand(
    private val itemRepository: ItemRepository,
    private val notificationRepository: NotificationRepository,
    private val logRepository: LogRepository,
    private val delayBetweenScrapes: Duration,
    private val strategies: List<MonitorStrategy>,
    private val maxLoopCount: Int = LOOP_INFINITE,
) : Command {
    override suspend fun shouldRun(context: ChainContext): RunDecision =
        if (maxLoopCount != LOOP_INFINITE && maxLoopCount == context.monitorRunSequenceNumber) {
            RunDecision.Skip
        } else if (context.monitorItems == null) {
            // First time so run immediately.
            RunDecision.RunNow
        } else {
            RunDecision.RunWithDelay(delayBetweenScrapes)
        }

    override suspend fun execute(context: ChainContext): ChainContext {
        var nextPageToken: String? = null
        var totalNumberOfItems = 0
        val items: MutableMap<String, Item> = context.monitorItems?.toMutableMap() ?: hashMapOf()

        do {
            val message =
                nextPageToken?.let {
                    "Retrieving items from $it"
                } ?: "Retrieving items from first page"
            logRepository.info(message)

            val page = itemRepository.getItems(nextPageToken)
            tryExecuteStrategies(page.items, context.monitorItems)
            page.items.forEach { item -> items[item.id] = item }
            totalNumberOfItems += page.items.size
            nextPageToken = page.nextPageToken
        } while (nextPageToken != null)

        logRepository.info(
            "Found and processed a total of $totalNumberOfItems items.",
        )

        return context.copy(monitorItems = items, monitorRunSequenceNumber = context.monitorRunSequenceNumber + 1)
    }

    override fun getDescription(): String = "Monitoring new products"

    private suspend fun tryExecuteStrategies(
        items: List<Item>,
        existingItems: Map<String, Item>?,
    ) {
        items.forEach { item ->
            strategies.forEach { strategy ->
                if (!strategy.requiresBaseline() || existingItems != null) {
                    ExecuteStrategyCommand(
                        notificationRepository,
                        logRepository,
                        strategy,
                        item,
                        existingItems?.get(item.id),
                    ).execute()
                }
            }
        }
    }

    companion object {
        const val LOOP_INFINITE: Int = -1
    }
}

package com.cereal.script.commands.monitor

import com.cereal.script.commands.Command
import com.cereal.script.commands.CommandResult
import com.cereal.script.commands.monitor.domain.ItemRepository
import com.cereal.script.commands.monitor.domain.NotificationRepository
import com.cereal.script.commands.monitor.domain.models.Item
import com.cereal.script.commands.monitor.strategy.MonitorStrategy
import com.cereal.script.core.domain.repository.LogRepository
import kotlinx.coroutines.delay
import kotlin.time.Duration

class MonitorCommand(
    private val itemRepository: ItemRepository,
    private val notificationRepository: NotificationRepository,
    private val logRepository: LogRepository,
    private val delayBetweenScrapes: Duration,
    private val strategies: List<MonitorStrategy>,
    private val maxLoopCount: Int = LOOP_INFINITE,
) : Command {
    private var nextPageToken: String? = null
    private var runSequenceNumber = 1
    private var totalNumberOfItems = 0

    override suspend fun shouldRun(): Boolean {
        // Always run the monitor, there's no "end state".
        return true
    }

    override suspend fun execute(): CommandResult {
        val page = itemRepository.getItems(nextPageToken)
        processItems(page.items)
        totalNumberOfItems += page.items.size
        nextPageToken = page.nextPageToken

        if (maxLoopCount != LOOP_INFINITE && maxLoopCount == runSequenceNumber) {
            return CommandResult.Completed
        }

        // When there's no next page delay for a while before starting over.
        if (nextPageToken == null) {
            logRepository.add(
                "Found and processed a total of $totalNumberOfItems items, waiting $delayBetweenScrapes before starting over.",
            )
            totalNumberOfItems = 0
            runSequenceNumber++

            delay(delayBetweenScrapes.inWholeMilliseconds)
        }

        return CommandResult.Repeat
    }

    override fun getDescription(): String =
        nextPageToken?.let {
            "Retrieving items from $it"
        } ?: "Retrieving items from first page"

    private suspend fun processItems(items: List<Item>) {
        items.forEach { item ->
            strategies.forEach { strategy ->
                ExecuteStrategyCommand(
                    notificationRepository,
                    logRepository,
                    strategy,
                    item,
                ).execute(runSequenceNumber)
            }
        }
    }

    companion object {
        const val LOOP_INFINITE: Int = -1
    }
}

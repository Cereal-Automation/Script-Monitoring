package com.cereal.script.commands.monitor

import com.cereal.script.commands.Command
import com.cereal.script.commands.CommandResult
import com.cereal.script.commands.monitor.models.Item
import com.cereal.script.commands.monitor.repository.ItemRepository
import com.cereal.script.commands.monitor.repository.NotificationRepository
import com.cereal.script.commands.monitor.strategy.ExecuteStrategyCommand
import com.cereal.script.commands.monitor.strategy.MonitorStrategy
import com.cereal.script.domain.repository.LogRepository
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
    private val lastItems: HashMap<String, Item> = HashMap()
    private var isBaselineSet = false

    override suspend fun shouldRun(): Boolean {
        // Always run the monitor, there's no "end state".
        return true
    }

    /**
     * Executes the command to retrieve and process a page of items from the item repository.
     * The items are processed using a set of predefined strategies.
     *
     * If there is no next page token available, it logs the total number of items processed, waits for a specified delay,
     * and then restarts the execution if the maximum loop count is not reached.
     *
     * @return [CommandResult] indicating whether the command execution is completed or should be repeated.
     */
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
            isBaselineSet = true
            logRepository.info(
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
                if (!strategy.requiresBaseline() || isBaselineSet) {
                    ExecuteStrategyCommand(
                        notificationRepository,
                        logRepository,
                        strategy,
                        item,
                        lastItems[item.id],
                    ).execute()
                }
            }

            lastItems[item.id] = item
        }
    }

    companion object {
        const val LOOP_INFINITE: Int = -1
    }
}

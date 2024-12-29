package com.cereal.script.commands.monitor.strategy

import com.cereal.script.commands.monitor.models.Item

interface MonitorStrategy {
    /**
     * Determines if a notification should be sent based on the current and previous state of the item.
     *
     * @param item The current state of the item to evaluate.
     * @param previousItem The previous state of the item, or null if unavailable (on first run).
     * @return the message if a notification should be sent, null otherwise.
     */
    suspend fun shouldNotify(
        item: Item,
        previousItem: Item?,
    ): String?

    /**
     * Indicates whether an initial baseline or previous item is required for the strategy to function correctly.
     * Note that previousItem can still be null in case new items are detected after the first run.
     *
     * @return True if a previous item is required, false otherwise. If true [shouldNotify] will not be called
     * when items are scraped for the first time.
     */
    fun requiresBaseline(): Boolean
}

package com.cereal.command.monitor.strategy

import com.cereal.command.monitor.models.Item

interface MonitorStrategy {
    /**
     * Sealed result type for [shouldNotify].
     */
    sealed class NotifyResult {
        /** A notification should be sent with this [message]. */
        data class Notify(val message: String) : NotifyResult()

        /** No notification should be sent. [reason] describes why. */
        data class Skip(val reason: String) : NotifyResult()
    }

    /**
     * Determines if a notification should be sent based on the current and previous state of the item.
     *
     * @param item The current state of the item to evaluate.
     * @param previousItem The previous state of the item, or null if unavailable (on first run).
     * @return [NotifyResult.Notify] with a message if a notification should be sent,
     *         [NotifyResult.Skip] with a reason otherwise.
     */
    suspend fun shouldNotify(
        item: Item,
        previousItem: Item?,
    ): NotifyResult

    /**
     * Indicates whether an initial baseline or previous item is required for the strategy to function correctly.
     * Note that previousItem can still be null in case new items are detected after the first run.
     *
     * @return True if a previous item is required, false otherwise. If true [shouldNotify] will not be called
     * when items are scraped for the first time.
     */
    fun requiresBaseline(): Boolean
}

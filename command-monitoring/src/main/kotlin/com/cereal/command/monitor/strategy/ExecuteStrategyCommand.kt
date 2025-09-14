package com.cereal.command.monitor.strategy

import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.repository.NotificationRepository
import com.cereal.script.repository.LogRepository

/**
 * The `ExecuteStrategyCommand` class is responsible for executing a monitoring strategy on a given item.
 * If the strategy determines that a notification should be sent, this class will handle the notification process
 * and log relevant messages.
 *
 * @property notificationRepository Repository responsible for managing notifications.
 * @property logRepository Repository used for logging messages and errors.
 * @property strategy The monitoring strategy to be executed.
 */
class ExecuteStrategyCommand(
    private val notificationRepository: NotificationRepository,
    private val logRepository: LogRepository,
    private val strategy: MonitorStrategy,
    private val item: Item,
    private val previousItem: Item?,
) {
    suspend fun execute() {
        val message =
            try {
                strategy.shouldNotify(item, previousItem)
            } catch (e: Exception) {
                logRepository.info(
                    "Unable to determine if a notification needs to be triggered for '${item.name}' because: ${e.message}",
                )
                null
            }

        if (message != null) {
            try {
                logRepository.info(
                    "Notification triggered for item '${item.name}': $message",
                )

                notificationRepository.notify(message, item)
            } catch (e: Exception) {
                logRepository.info("Unable to create a notification for '${item.name}' because: ${e.message}")
            }
        }
    }
}

package com.cereal.script.commands.monitor

import com.cereal.script.commands.monitor.domain.NotificationRepository
import com.cereal.script.commands.monitor.domain.models.Item
import com.cereal.script.commands.monitor.strategy.MonitorStrategy
import com.cereal.script.core.domain.repository.LogRepository

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
) {
    suspend fun execute(sequenceNumber: Int) {
        val notify =
            try {
                strategy.shouldNotify(item, sequenceNumber)
            } catch (e: Exception) {
                logRepository.info(
                    "Unable to determine if a notification needs to be triggered for '${item.name}' because: ${e.message}",
                )
                false
            }

        if (notify) {
            try {
                val message = strategy.getNotificationMessage(item)
                logRepository.info(message)

                notificationRepository.notify(message)
                notificationRepository.setItemNotified(item)
            } catch (e: Exception) {
                logRepository.info("Unable to create a notification for '${item.name}' because: ${e.message}")
            }
        }
    }
}

package com.cereal.script.monitoring.domain.command

import com.cereal.script.monitoring.domain.models.Item
import com.cereal.script.monitoring.domain.repository.ExecutionRepository
import com.cereal.script.monitoring.domain.repository.LogRepository
import com.cereal.script.monitoring.domain.repository.NotificationRepository
import com.cereal.script.monitoring.domain.strategy.MonitorStrategy

/**
 * The `ExecuteStrategyCommand` class is responsible for executing a monitoring strategy on a given item.
 * If the strategy determines that a notification should be sent, this class will handle the notification process
 * and log relevant messages.
 *
 * @property notificationRepository Repository responsible for managing notifications.
 * @property logRepository Repository used for logging messages and errors.
 * @property executionRepository Repository for handling execution state.
 * @property strategy The monitoring strategy to be executed.
 */
class ExecuteStrategyCommand(
    private val notificationRepository: NotificationRepository,
    private val logRepository: LogRepository,
    private val executionRepository: ExecutionRepository,
    private val strategy: MonitorStrategy,
) {
    suspend fun execute(item: Item) {
        val notify =
            try {
                val execution = executionRepository.get()
                strategy.shouldNotify(item, execution)
            } catch (e: Exception) {
                logRepository.add(
                    "Unable to determine if a notification needs to be triggered for '${item.name}' because: ${e.message}",
                )
                false
            }

        if (notify) {
            try {
                val message = strategy.getNotificationMessage(item)
                logRepository.add(message)

                notificationRepository.notify(message)
                notificationRepository.setItemNotified(item)
            } catch (e: Exception) {
                logRepository.add("Unable to create a notification for '${item.name}' because: ${e.message}")
            }
        }
    }
}

package com.cereal.script.monitoring.domain

import com.cereal.script.monitoring.domain.models.Item
import com.cereal.script.monitoring.domain.repository.ItemRepository
import com.cereal.script.monitoring.domain.repository.LogRepository
import com.cereal.script.monitoring.domain.repository.NotificationRepository
import com.cereal.script.monitoring.domain.strategy.MonitorStrategy
import kotlinx.coroutines.flow.catch

class MonitorInteractor(
    private val itemRepository: ItemRepository,
    private val notificationRepository: NotificationRepository,
    private val logRepository: LogRepository,
) {
    suspend operator fun invoke(config: Config) {
        val strategies = config.strategies

        logRepository.add("Start collecting data...")

        return itemRepository
            .getItems()
            .catch { logRepository.add("Error retrieving data with message: ${it.message}") }
            .collect { item ->
                logRepository.add(item.getItemFoundText())

                strategies.forEach { strategy ->
                    applyStrategy(strategy, item)
                }
            }
    }

    private suspend fun applyStrategy(
        strategy: MonitorStrategy,
        item: Item,
    ) {
        val notify =
            try {
                strategy.shouldNotify(item)
            } catch (e: Exception) {
                logRepository.add(
                    "Unable to determine if a notification needs to be triggered for '${item.name}' because: ${e.message}",
                )
                false
            }

        if (notify) {
            logRepository.add("Sending notification for '${item.name}'.")

            try {
                val message = strategy.getNotificationMessage(item)

                notificationRepository.notify(message)
                notificationRepository.setItemNotified(item)
            } catch (e: Exception) {
                logRepository.add("Unable to create a notification for '${item.name}' because: ${e.message}")
            }
        }
    }

    private fun Item.getItemFoundText(): String =
        buildString {
            append("Found item $name")

            if (values.isNotEmpty()) {
                append(" [")
                append(
                    values.joinToString(", ") {
                        "${it.commonName}: $it"
                    },
                )
                append("]")
            }
        }

    data class Config(
        val strategies: List<MonitorStrategy>,
    )
}

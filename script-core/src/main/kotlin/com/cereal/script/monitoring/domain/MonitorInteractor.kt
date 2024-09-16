package com.cereal.script.monitoring.domain

import com.cereal.script.monitoring.domain.models.Item
import com.cereal.script.monitoring.domain.models.MonitorMode
import com.cereal.script.monitoring.domain.repository.ItemRepository
import com.cereal.script.monitoring.domain.repository.LogRepository
import com.cereal.script.monitoring.domain.repository.NotificationRepository

class MonitorInteractor(
    private val monitorStrategyFactory: MonitorStrategyFactory,
    private val itemRepository: ItemRepository,
    private val notificationRepository: NotificationRepository,
    private val logRepository: LogRepository,
) {

    suspend operator fun invoke(config: Config) {
        val strategy = monitorStrategyFactory.create(config.mode)

        logRepository.add("Start collecting data...")

        return itemRepository.getItems().collect { item ->
            logRepository.add(item.getItemFoundText())

            val notify = try {
                strategy.shouldNotify(item)
            } catch (e: Exception) {
                logRepository.add("Unable to determine if a notification needs to be triggered for '${item.name}' because: ${e.message}")
                false
            }

            if (notify && !notificationRepository.isItemNotified(item)) {
                logRepository.add("Sending notification for '${item.name}'.")

                val message = try {
                    strategy.getNotificationMessage(item)
                } catch (e: Exception) {
                    "There was a change detected in '${item.name}'."
                }

                notificationRepository.notify(message)
                notificationRepository.setItemNotified(item)
            }
        }
    }

    private fun Item.getItemFoundText(): String {
        return buildString {
            append("Found item $name")

            if (values.isNotEmpty()) {
                append(" [")
                append(values.joinToString(", ") {
                    "${it.commonName}: $it"
                })
                append("]")
            }
        }
    }

    data class Config(val mode: MonitorMode)
}

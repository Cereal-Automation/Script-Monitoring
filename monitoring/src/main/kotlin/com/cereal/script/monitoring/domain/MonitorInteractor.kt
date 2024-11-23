package com.cereal.script.monitoring.domain

import com.cereal.script.monitoring.domain.models.Execution
import com.cereal.script.monitoring.domain.models.Item
import com.cereal.script.monitoring.domain.models.duration
import com.cereal.script.monitoring.domain.repository.ExecutionRepository
import com.cereal.script.monitoring.domain.repository.ItemRepository
import com.cereal.script.monitoring.domain.repository.LogRepository
import com.cereal.script.monitoring.domain.repository.NotificationRepository
import com.cereal.script.monitoring.domain.strategy.MonitorStrategy
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import java.time.Instant
import kotlin.math.pow
import kotlin.time.Duration.Companion.milliseconds

class MonitorInteractor(
    private val itemRepository: ItemRepository,
    private val notificationRepository: NotificationRepository,
    private val logRepository: LogRepository,
    private val executionRepository: ExecutionRepository,
) {
    suspend operator fun invoke(config: Config) {
        val strategies = config.strategies

        val executions = executionRepository.getExecutions()
        val execution = Execution(executions.last().sequenceNumber + 1, start = null, end = null)
        return itemRepository
            .getItems()
            .onStart {
                val updatedExecution = execution.copy(start = Instant.now())
                executionRepository.updateExecution(updatedExecution)
                logRepository.add("Start collecting data.", mapOf("seq_number" to execution.sequenceNumber))
            }.onCompletion { error ->
                val updatedExecution = execution.copy(end = Instant.now())
                executionRepository.updateExecution(updatedExecution)

                val logMessage =
                    error?.let {
                        "Error collecting data: ${it.message}"
                    } ?: "Finished collecting data."

                logRepository.add(
                    logMessage,
                    mapOf("seq_number" to execution.sequenceNumber, "duration" to execution.duration().toString()),
                )
            }.retryWhen { cause, attempt ->
                if (attempt < RETRY_ATTEMPTS) {
                    val delayTime = RETRY_DELAY * 2.0.pow(attempt.toDouble()).toLong()

                    logRepository.add(
                        "Retrying in ${delayTime.milliseconds} due to ${cause.message}",
                        mapOf("seq_number" to execution.sequenceNumber),
                    )
                    delay(delayTime)
                    true
                } else {
                    false
                }
            }.collect { item ->
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

    companion object {
        const val RETRY_ATTEMPTS = 25
        const val RETRY_DELAY = 1000L
    }
}

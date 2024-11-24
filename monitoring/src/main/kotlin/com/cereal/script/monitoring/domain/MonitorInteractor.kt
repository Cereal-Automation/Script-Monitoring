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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import kotlinx.datetime.Clock
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

        val executions = executionRepository.getAll()
        val execution = Execution((executions.lastOrNull()?.sequenceNumber ?: 0) + 1, start = null, end = null)
        executionRepository.create(execution)

        return itemRepository
            .getItems()
            .applyUpdateExecution(execution.sequenceNumber)
            .applyLogging(execution.sequenceNumber)
            .applyRetry(execution.sequenceNumber)
            .collect { item ->
                logRepository.add("Found item ${item.name}", item.values.associateBy { it.commonName })

                strategies.forEach { strategy ->
                    executeStrategy(strategy, item)
                }
            }
    }

    private fun <T> Flow<T>.applyUpdateExecution(sequenceNumber: Int): Flow<T> =
        this
            .onStart {
                val execution = executionRepository.get(sequenceNumber)
                val updatedExecution = execution.copy(start = Clock.System.now())
                executionRepository.update(updatedExecution)
            }.onCompletion { _ ->
                val execution = executionRepository.get(sequenceNumber)
                val updatedExecution = execution.copy(end = Clock.System.now())
                executionRepository.update(updatedExecution)
            }

    private fun <T> Flow<T>.applyLogging(executionSequenceNumber: Int): Flow<T> =
        this
            .onStart {
                logRepository.add("Start collecting data.", mapOf("seq_number" to executionSequenceNumber))
            }.onCompletion { error ->
                val execution = executionRepository.get(executionSequenceNumber)
                val logMessage =
                    error?.let {
                        "Error collecting data: ${it.message}"
                    } ?: "Finished collecting data."

                logRepository.add(
                    logMessage,
                    mapOf("seq_number" to execution.sequenceNumber, "duration" to execution.duration().toString()),
                )
            }

    private fun <T> Flow<T>.applyRetry(executionSequenceNumber: Int): Flow<T> =
        this.retryWhen { cause, attempt ->
            if (attempt < RETRY_ATTEMPTS_TOTAL) {
                val delayTime =
                    if (attempt < RETRY_ATTEMPTS_LINEAR) {
                        RETRY_DELAY
                    } else {
                        val backoffAttempt = attempt - RETRY_ATTEMPTS_LINEAR
                        RETRY_DELAY * 2.0.pow(backoffAttempt.toDouble()).toLong()
                    }

                logRepository.add(
                    "Retrying in ${delayTime.milliseconds} due to ${cause.message}",
                    mapOf("seq_number" to executionSequenceNumber),
                )
                delay(delayTime)
                true
            } else {
                false
            }
        }

    private suspend fun executeStrategy(
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

    data class Config(
        val strategies: List<MonitorStrategy>,
    )

    companion object {
        const val RETRY_ATTEMPTS_TOTAL = 15
        const val RETRY_ATTEMPTS_LINEAR = 5
        const val RETRY_DELAY = 500L
    }
}

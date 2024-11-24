package com.cereal.script.monitoring.domain

import com.cereal.script.monitoring.domain.command.ExecuteStrategyCommand
import com.cereal.script.monitoring.domain.models.Execution
import com.cereal.script.monitoring.domain.models.duration
import com.cereal.script.monitoring.domain.repository.ExecutionRepository
import com.cereal.script.monitoring.domain.repository.ItemRepository
import com.cereal.script.monitoring.domain.repository.LogRepository
import com.cereal.script.monitoring.domain.repository.NotificationRepository
import com.cereal.script.monitoring.domain.strategy.MonitorStrategy
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
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
    suspend operator fun invoke(strategies: List<MonitorStrategy>) {
        val execution =
            if (executionRepository.exists()) {
                Execution(executionRepository.get().sequenceNumber + 1)
            } else {
                Execution(START_SEQUENCE_NUMBER)
            }
        executionRepository.set(execution)

        return itemRepository
            .getItems()
            .applyUpdateExecution()
            .applyLogging()
            .applyRetry()
            .collect { item ->
                strategies.forEach { strategy ->
                    val command =
                        ExecuteStrategyCommand(notificationRepository, logRepository, executionRepository, strategy)
                    command.execute(item)
                }
            }
    }

    /**
     * Extension function for Flow<T> that updates the execution repository with start and end times.
     *
     * @return A Flow<T> that updates the execution with start and end times during the data collection process.
     */
    private fun <T> Flow<T>.applyUpdateExecution(): Flow<T> =
        this
            .onStart {
                val execution = executionRepository.get()
                val updatedExecution = execution.copy(start = Clock.System.now())
                executionRepository.set(updatedExecution)
            }.onEach {
                val execution = executionRepository.get()
                val updatedExecution = execution.copy(totalItems = execution.totalItems + 1)
                executionRepository.set(updatedExecution)
            }.onCompletion { _ ->
                val execution = executionRepository.get()
                val updatedExecution = execution.copy(end = Clock.System.now())
                executionRepository.set(updatedExecution)
            }

    /**
     * Extension function for Flow<T> that logs the start and end of data collection, including any errors that occur.
     *
     * @return A Flow<T> that logs messages to the log repository at the start and upon completion of the data collection process.
     */
    private fun <T> Flow<T>.applyLogging(): Flow<T> =
        this
            .onStart {
                val execution = executionRepository.get()
                logRepository.add("Start collecting data.", execution.logInfo())
            }.onCompletion { error ->
                val execution = executionRepository.get()
                val logMessage =
                    error?.let {
                        "Error collecting data: ${it.message}"
                    } ?: "Finished processing data."

                logRepository.add(
                    logMessage,
                    execution.logInfo(true),
                )
            }

    /**
     * Extension function for Flow<T> that applies a retry mechanism with exponential backoff strategy.
     *
     * The function retries the collection up to a specified number of attempts. The delay between retries increases
     * exponentially after a certain number of linear retry attempts.
     *
     * @return A Flow<T> that applies the retry mechanism during the data collection process.
     */
    private fun <T> Flow<T>.applyRetry(): Flow<T> =
        this.retryWhen { cause, attempt ->
            if (attempt < RETRY_ATTEMPTS_TOTAL) {
                val delayTime =
                    if (attempt < RETRY_ATTEMPTS_LINEAR) {
                        RETRY_DELAY
                    } else {
                        val backoffAttempt = attempt - RETRY_ATTEMPTS_LINEAR
                        RETRY_DELAY * 2.0.pow(backoffAttempt.toDouble()).toLong()
                    }

                val execution = executionRepository.get()
                logRepository.add(
                    "Retrying in ${delayTime.milliseconds} due to ${cause.message}",
                    execution.logInfo(),
                )
                delay(delayTime)
                true
            } else {
                false
            }
        }

    private fun Execution.logInfo(extended: Boolean = false): Map<String, Any> =
        mapOf(
            "seq_number" to sequenceNumber.toString(),
        ) +
            if (extended) {
                mapOf(
                    "total_items" to totalItems,
                    "duration" to duration().toString(),
                )
            } else {
                emptyMap()
            }

    companion object {
        const val RETRY_ATTEMPTS_TOTAL = 15
        const val RETRY_ATTEMPTS_LINEAR = 5
        const val RETRY_DELAY = 500L
        const val START_SEQUENCE_NUMBER = 1
    }
}

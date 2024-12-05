package com.cereal.script.core.domain

import com.cereal.script.commands.CommandResult
import com.cereal.script.core.domain.repository.LogRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import kotlin.math.pow
import kotlin.time.Duration.Companion.milliseconds

const val RETRY_ATTEMPTS_TOTAL = 15
const val RETRY_ATTEMPTS_LINEAR = 5
const val RETRY_DELAY = 500L

/**
 * Extension function for Flow<T> that applies a retry mechanism with exponential backoff strategy.
 *
 * The function retries the collection up to a specified number of attempts. The delay between retries increases
 * exponentially after a certain number of linear retry attempts.
 *
 * @return A Flow<T> that applies the retry mechanism during the data collection process.
 */
fun <T> Flow<T>.withRetry(
    action: String,
    logRepository: LogRepository,
): Flow<T> =
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
                "Retrying '$action' in ${delayTime.milliseconds} due to ${cause.message}",
            )
            delay(delayTime)
            true
        } else {
            false
        }
    }

/**
 * Extension function for Flow<T> that adds logging at the start and completion of the execution.
 *
 * The function logs the start and completion of the task, capturing any errors if they occur.
 *
 * @param action The action being performed.
 * @return A Flow<T> that logs messages during the data collection process.
 */
fun Flow<CommandResult>.withLogging(
    action: String,
    logRepository: LogRepository,
): Flow<CommandResult> =
    this
        .onStart {
            logRepository.add("$action.")
        }.onEach {
            when (it) {
                // No need to log something here because when repeating the "start" log makes sure the user knows what's going on.
                CommandResult.Repeat -> null
                CommandResult.Completed -> "Finished '$action'."
                CommandResult.Skip -> "Skipping '$action'."
            }?.let {
                logRepository.add(it)
            }
        }.onCompletion { error ->
            error?.let {
                logRepository.add("Error while '$action': ${it.message}")
            }
        }

package com.cereal.script.interactor

import com.cereal.script.commands.ChainContext
import com.cereal.script.repository.LogRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.math.pow
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

@ExperimentalCoroutinesApi
class CommandFlowExtTest {
    private lateinit var logRepository: LogRepository

    @BeforeEach
    fun setUp() {
        logRepository = mockk(relaxed = true)
    }

    @Test
    fun `withRetry should not retry on RuntimeException`() = runTest {
        // Given
        var attemptCount = 0
        val testFlow = flow<String> {
            attemptCount++
            throw RuntimeException("Test runtime exception")
        }

        // When
        val result = runCatching {
            testFlow.withRetry("test action", logRepository).toList()
        }

        // Then
        assertEquals(1, attemptCount)
        assertTrue(result.isFailure)
        coVerify { logRepository.info("Skip retrying 'test action' due to unrecoverable exception 'Test runtime exception'") }
    }

    @Test
    fun `withRetry should not retry on UnrecoverableException`() = runTest {
        // Given
        var attemptCount = 0
        val testFlow = flow<String> {
            attemptCount++
            throw UnrecoverableException("Test unrecoverable exception")
        }

        // When
        val result = runCatching {
            testFlow.withRetry("test action", logRepository).toList()
        }

        // Then
        assertEquals(1, attemptCount)
        assertTrue(result.isFailure)
        coVerify { logRepository.info("Skip retrying 'test action' due to unrecoverable exception 'Test unrecoverable exception'") }
    }

    @Test
    fun `withRetry should retry with linear backoff for first five attempts`() = runTest {
        // Given
        var attemptCount = 0
        val testFlow = flow {
            if (++attemptCount <= RETRY_ATTEMPTS_LINEAR) {
                throw Exception("Test exception")
            }
            emit(attemptCount)
        }

        // When
        val result = testFlow.withRetry("test action", logRepository).toList()

        // Then
        assertEquals(listOf(RETRY_ATTEMPTS_LINEAR + 1), result)
        repeat(RETRY_ATTEMPTS_LINEAR) {
            coVerify { logRepository.info("Retrying 'test action' in ${RETRY_DELAY.milliseconds} due to 'Test exception'") }
        }
    }

    @Test
    fun `withRetry should use exponential backoff after five attempts`() = runTest {
        // Given
        var attemptCount = 0
        val testFlow = flow {
            if (++attemptCount <= RETRY_ATTEMPTS_LINEAR + 2) {
                throw Exception("Test exception")
            }
            emit(attemptCount)
        }

        // When
        val result = testFlow.withRetry("test action", logRepository).toList()

        // Then
        assertEquals(listOf(RETRY_ATTEMPTS_LINEAR + 3), result)

        // Verify linear backoff for first RETRY_ATTEMPTS_LINEAR attempts
        repeat(RETRY_ATTEMPTS_LINEAR) {
            coVerify { logRepository.info("Retrying 'test action' in ${RETRY_DELAY.milliseconds} due to 'Test exception'") }
        }

        // Verify exponential backoff for attempts after RETRY_ATTEMPTS_LINEAR
        coVerify {
            logRepository.info(
                "Retrying 'test action' in ${
                    (RETRY_DELAY * 2.0.pow(0.0).toLong()).milliseconds
                } due to 'Test exception'"
            )
        }
        coVerify {
            logRepository.info(
                "Retrying 'test action' in ${
                    (RETRY_DELAY * 2.0.pow(1.0).toLong()).milliseconds
                } due to 'Test exception'"
            )
        }
    }

    @Test
    fun `withRetry should stop retrying after maximum attempts`() = runTest {
        // Given
        var attemptCount = 0
        val testFlow = flow<String> {
            attemptCount++
            throw Exception("Test exception")
        }

        // When
        val result = runCatching {
            testFlow.withRetry("test action", logRepository).toList()
        }

        // Then
        assertEquals(RETRY_ATTEMPTS_TOTAL + 1, attemptCount)
        assertTrue(result.isFailure)
    }

    @Test
    fun `withLogging should log start and completion`() = runTest {
        // Given
        val context = ChainContext()
        val testFlow: Flow<ChainContext> = flow {
            emit(context)
        }

        // When
        val result = testFlow.withLogging("test action", logRepository).toList()

        // Then
        assertEquals(listOf(context), result)
        coVerify { logRepository.info("Starting test action.") }
        coVerify { logRepository.info("Finished 'test action'.") }
    }

    @Test
    fun `withLogging should log error`() = runTest {
        // Given
        val testException = Exception("Test error")
        val testFlow: Flow<ChainContext> = flow {
            throw testException
        }

        // When
        val result = runCatching {
            testFlow.withLogging("test action", logRepository).toList()
        }

        // Then
        assertTrue(result.isFailure)
        coVerify { logRepository.info("Starting test action.") }
        coVerify { logRepository.debug("Error executing 'test action': \n${testException.stackTraceToString()}") }
    }

    @Test
    fun `withLogging should not log CancellationException as error`() = runTest {
        // Given
        val testException = CancellationException("Test cancellation")
        val testFlow: Flow<ChainContext> = flow {
            throw testException
        }

        // When
        val result = runCatching {
            testFlow.withLogging("test action", logRepository).toList()
        }

        // Then
        assertTrue(result.isFailure)
        coVerify { logRepository.info("Starting test action.") }
        coVerify(exactly = 0) { logRepository.debug(any()) }
    }
}
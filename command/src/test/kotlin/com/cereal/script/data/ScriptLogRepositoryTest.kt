package com.cereal.script.data

import com.cereal.sdk.component.logger.LoggerComponent
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ScriptLogRepositoryTest {
    private lateinit var loggerComponent: LoggerComponent
    private lateinit var statusUpdate: suspend (String) -> Unit
    private lateinit var scriptLogRepository: ScriptLogRepository

    @BeforeEach
    fun setUp() {
        loggerComponent = mockk(relaxed = true)
        statusUpdate = mockk(relaxed = true)
        scriptLogRepository = ScriptLogRepository(loggerComponent, statusUpdate)
    }

    @Test
    fun `info logs message and updates status`() = runTest {
        // Given
        val message = "Test info message"
        val args = mapOf("key1" to "value1", "key2" to 42)

        // When
        scriptLogRepository.info(message, args)

        // Then
        val expectedMessage = "Test info message [key1=value1, key2=42]"
        coVerify(exactly = 1) { loggerComponent.info(expectedMessage) }
        coVerify(exactly = 1) { statusUpdate(expectedMessage) }
    }

    @Test
    fun `debug logs message without updating status`() = runTest {
        // Given
        val message = "Test debug message"
        val args = mapOf("key1" to "value1", "key2" to 42)

        // When
        scriptLogRepository.debug(message, args)

        // Then
        val expectedMessage = "Test debug message [key1=value1, key2=42]"
        coVerify(exactly = 1) { loggerComponent.info(expectedMessage) }
        coVerify(exactly = 0) { statusUpdate(any()) }
    }

    @Test
    fun `info logs message without args`() = runTest {
        // Given
        val message = "Test info message without args"

        // When
        scriptLogRepository.info(message, null)

        // Then
        coVerify(exactly = 1) { loggerComponent.info(message) }
        coVerify(exactly = 1) { statusUpdate(message) }
    }

    @Test
    fun `debug logs message without args`() = runTest {
        // Given
        val message = "Test debug message without args"

        // When
        scriptLogRepository.debug(message, null)

        // Then
        coVerify(exactly = 1) { loggerComponent.info(message) }
        coVerify(exactly = 0) { statusUpdate(any()) }
    }

    @Test
    fun `info logs message with empty args map`() = runTest {
        // Given
        val message = "Test info message with empty args"
        val args = emptyMap<String, Any>()

        // When
        scriptLogRepository.info(message, args)

        // Then
        coVerify(exactly = 1) { loggerComponent.info(message) }
        coVerify(exactly = 1) { statusUpdate(message) }
    }
}
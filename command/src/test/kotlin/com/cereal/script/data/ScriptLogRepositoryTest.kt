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

    // region error

    @Test
    fun `error logs message, updates status, and passes throwable`() =
        runTest {
            val message = "Test error message"
            val args = mapOf("key1" to "value1", "key2" to 42)
            val throwable = RuntimeException("boom")

            scriptLogRepository.error(message, throwable, args)

            val expected = "Test error message [key1=value1, key2=42]"
            coVerify(exactly = 1) { loggerComponent.error(expected, throwable) }
            coVerify(exactly = 1) { statusUpdate(expected) }
        }

    @Test
    fun `error logs message without args`() =
        runTest {
            val message = "Test error message without args"

            scriptLogRepository.error(message, null, null)

            coVerify(exactly = 1) { loggerComponent.error(message, null) }
            coVerify(exactly = 1) { statusUpdate(message) }
        }

    // endregion

    // region warn

    @Test
    fun `warn logs message and updates status`() =
        runTest {
            val message = "Test warn message"
            val args = mapOf("key1" to "value1", "key2" to 42)

            scriptLogRepository.warn(message, args)

            val expected = "Test warn message [key1=value1, key2=42]"
            coVerify(exactly = 1) { loggerComponent.warn(expected) }
            coVerify(exactly = 1) { statusUpdate(expected) }
        }

    @Test
    fun `warn logs message without args`() =
        runTest {
            val message = "Test warn message without args"

            scriptLogRepository.warn(message, null)

            coVerify(exactly = 1) { loggerComponent.warn(message) }
            coVerify(exactly = 1) { statusUpdate(message) }
        }

    // endregion

    // region info

    @Test
    fun `info logs message and updates status`() =
        runTest {
            val message = "Test info message"
            val args = mapOf("key1" to "value1", "key2" to 42)

            scriptLogRepository.info(message, args)

            val expected = "Test info message [key1=value1, key2=42]"
            coVerify(exactly = 1) { loggerComponent.info(expected) }
            coVerify(exactly = 1) { statusUpdate(expected) }
        }

    @Test
    fun `info logs message without args`() =
        runTest {
            val message = "Test info message without args"

            scriptLogRepository.info(message, null)

            coVerify(exactly = 1) { loggerComponent.info(message) }
            coVerify(exactly = 1) { statusUpdate(message) }
        }

    @Test
    fun `info logs message with empty args map`() =
        runTest {
            val message = "Test info message with empty args"
            val args = emptyMap<String, Any>()

            scriptLogRepository.info(message, args)

            coVerify(exactly = 1) { loggerComponent.info(message) }
            coVerify(exactly = 1) { statusUpdate(message) }
        }

    // endregion

    // region debug

    @Test
    fun `debug logs message without updating status`() =
        runTest {
            val message = "Test debug message"
            val args = mapOf("key1" to "value1", "key2" to 42)

            scriptLogRepository.debug(message, args)

            val expected = "Test debug message [key1=value1, key2=42]"
            coVerify(exactly = 1) { loggerComponent.debug(expected) }
            coVerify(exactly = 0) { statusUpdate(any()) }
        }

    @Test
    fun `debug logs message without args`() =
        runTest {
            val message = "Test debug message without args"

            scriptLogRepository.debug(message, null)

            coVerify(exactly = 1) { loggerComponent.debug(message) }
            coVerify(exactly = 0) { statusUpdate(any()) }
        }

    // endregion
}

package com.cereal.script.monitoring.domain.command

import com.cereal.script.monitoring.domain.models.Item
import com.cereal.script.monitoring.domain.repository.ExecutionRepository
import com.cereal.script.monitoring.domain.repository.LogRepository
import com.cereal.script.monitoring.domain.repository.NotificationRepository
import com.cereal.script.monitoring.domain.strategy.MonitorStrategy
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ExecuteStrategyCommandTest {
    private lateinit var notificationRepository: NotificationRepository
    private lateinit var logRepository: LogRepository
    private lateinit var executionRepository: ExecutionRepository
    private lateinit var strategy: MonitorStrategy
    private lateinit var executeStrategyCommand: ExecuteStrategyCommand

    @BeforeEach
    fun setup() {
        notificationRepository = mockk()
        logRepository = mockk()
        executionRepository = mockk()
        strategy = mockk()
    }

    @Test
    fun `execute should log error and not notify when strategy throws exception`() =
        runBlocking {
            val item = Item("1", "url", "TestItem", emptyList())
            val exceptionMessage = "Test Exception"

            coEvery { executionRepository.get() } throws RuntimeException(exceptionMessage)
            coJustRun { logRepository.add(any()) }
            executeStrategyCommand =
                ExecuteStrategyCommand(notificationRepository, logRepository, executionRepository, strategy, item)
            executeStrategyCommand.execute()

            coVerify {
                logRepository.add(
                    "Unable to determine if a notification needs to be triggered for 'TestItem' because: $exceptionMessage",
                )
            }
            coVerify(exactly = 0) { notificationRepository.notify(any()) }
        }

    @Test
    fun `execute should send notification when shouldNotify returns true`() =
        runBlocking {
            val item = Item("1", "url", "TestItem", emptyList())
            val message = "Notify Message"

            coEvery { executionRepository.get() } returns mockk()
            coEvery { strategy.shouldNotify(item, any()) } returns true
            coEvery { strategy.getNotificationMessage(item) } returns message
            coJustRun { notificationRepository.notify(message) }
            coJustRun { logRepository.add(any()) }
            coJustRun { notificationRepository.setItemNotified(item) }

            executeStrategyCommand =
                ExecuteStrategyCommand(notificationRepository, logRepository, executionRepository, strategy, item)
            executeStrategyCommand.execute()

            coVerify { notificationRepository.notify(message) }
            coVerify { notificationRepository.setItemNotified(item) }
        }

    @Test
    fun `execute should log error when notification creation fails`() =
        runBlocking {
            val item = Item("1", "url", "TestItem", emptyList())
            val message = "Notify Message"
            val exceptionMessage = "Notification Exception"

            coEvery { executionRepository.get() } returns mockk()
            coEvery { strategy.shouldNotify(item, any()) } returns true
            coEvery { strategy.getNotificationMessage(item) } returns message
            coEvery { notificationRepository.notify(any()) } throws RuntimeException(exceptionMessage)
            coJustRun { logRepository.add(any()) }
            coJustRun { notificationRepository.setItemNotified(item) }

            executeStrategyCommand =
                ExecuteStrategyCommand(notificationRepository, logRepository, executionRepository, strategy, item)
            executeStrategyCommand.execute()

            coVerify { logRepository.add("Unable to create a notification for 'TestItem' because: $exceptionMessage") }
        }

    @Test
    fun `execute should not send notification when shouldNotify returns false`() =
        runBlocking {
            val item = Item("TestItem", "url", "item", emptyList())

            coEvery { executionRepository.get() } returns mockk()
            coEvery { strategy.shouldNotify(item, any()) } returns false

            executeStrategyCommand =
                ExecuteStrategyCommand(notificationRepository, logRepository, executionRepository, strategy, item)
            executeStrategyCommand.execute()

            coVerify(exactly = 0) { notificationRepository.notify(any()) }
            coVerify(exactly = 0) { logRepository.add("Sending notification for 'TestItem'.") }
        }
}

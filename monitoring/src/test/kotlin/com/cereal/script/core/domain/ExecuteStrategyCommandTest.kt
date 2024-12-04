package com.cereal.script.core.domain

import com.cereal.script.commands.monitor.ExecuteStrategyCommand
import com.cereal.script.commands.monitor.domain.NotificationRepository
import com.cereal.script.commands.monitor.domain.models.Item
import com.cereal.script.commands.monitor.strategy.MonitorStrategy
import com.cereal.script.core.domain.repository.LogRepository
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
    private lateinit var strategy: MonitorStrategy
    private lateinit var executeStrategyCommand: ExecuteStrategyCommand

    @BeforeEach
    fun setup() {
        notificationRepository = mockk()
        logRepository = mockk()
        strategy = mockk()
    }

    @Test
    fun `execute should send notification when shouldNotify returns true`() =
        runBlocking {
            val item = Item("1", "url", "TestItem", emptyList())
            val message = "Notify Message"

            coEvery { strategy.shouldNotify(item, any()) } returns true
            coEvery { strategy.getNotificationMessage(item) } returns message
            coJustRun { notificationRepository.notify(message) }
            coJustRun { logRepository.add(any()) }
            coJustRun { notificationRepository.setItemNotified(item) }

            executeStrategyCommand =
                ExecuteStrategyCommand(notificationRepository, logRepository, strategy, item)
            executeStrategyCommand.execute(1)

            coVerify { notificationRepository.notify(message) }
            coVerify { notificationRepository.setItemNotified(item) }
        }

    @Test
    fun `execute should log error when notification creation fails`() =
        runBlocking {
            val item = Item("1", "url", "TestItem", emptyList())
            val message = "Notify Message"
            val exceptionMessage = "Notification Exception"

            coEvery { strategy.shouldNotify(item, any()) } returns true
            coEvery { strategy.getNotificationMessage(item) } returns message
            coEvery { notificationRepository.notify(any()) } throws RuntimeException(exceptionMessage)
            coJustRun { logRepository.add(any()) }
            coJustRun { notificationRepository.setItemNotified(item) }

            executeStrategyCommand =
                ExecuteStrategyCommand(notificationRepository, logRepository, strategy, item)
            executeStrategyCommand.execute(1)

            coVerify { logRepository.add("Unable to create a notification for 'TestItem' because: $exceptionMessage") }
        }

    @Test
    fun `execute should not send notification when shouldNotify returns false`() =
        runBlocking {
            val item = Item("TestItem", "url", "item", emptyList())

            coEvery { strategy.shouldNotify(item, any()) } returns false

            executeStrategyCommand =
                ExecuteStrategyCommand(notificationRepository, logRepository, strategy, item)
            executeStrategyCommand.execute(1)

            coVerify(exactly = 0) { notificationRepository.notify(any()) }
            coVerify(exactly = 0) { logRepository.add("Sending notification for 'TestItem'.") }
        }
}

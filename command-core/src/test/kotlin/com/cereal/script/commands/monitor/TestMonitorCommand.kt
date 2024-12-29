package com.cereal.script.commands.monitor

import com.cereal.script.commands.CommandResult
import com.cereal.script.commands.monitor.models.Item
import com.cereal.script.commands.monitor.models.Page
import com.cereal.script.commands.monitor.repository.ItemRepository
import com.cereal.script.commands.monitor.repository.NotificationRepository
import com.cereal.script.commands.monitor.strategy.MonitorStrategy
import com.cereal.script.repository.LogRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class TestMonitorCommand {
    private val itemRepository = mockk<ItemRepository>()
    private val notificationRepository = mockk<NotificationRepository>(relaxed = true)
    private val logRepository = mockk<LogRepository>(relaxed = true)
    private val mockStrategy = mockk<MonitorStrategy>(relaxed = true)

    @Test
    fun `test execute with single page of items`() =
        runTest {
            val items =
                listOf(Item("1", url = "http://foo.bar", name = "Foo"), Item("2", url = "http://foo.bar", name = "Bar"))
            coEvery { itemRepository.getItems(null) } returns Page(null, items)

            val monitorCommand =
                MonitorCommand(
                    itemRepository = itemRepository,
                    notificationRepository = notificationRepository,
                    logRepository = logRepository,
                    delayBetweenScrapes = 1.seconds,
                    strategies = listOf(mockStrategy),
                    maxLoopCount = 2,
                )

            val result = monitorCommand.execute()

            coVerify {
                logRepository.info(
                    "Found and processed a total of 2 items, waiting 1s before starting over.",
                    any(),
                )
            }
            assert(result == CommandResult.Repeat)
        }

    @Test
    fun `test execute with continue page of items`() =
        runTest {
            val items =
                listOf(Item("1", url = "http://foo.bar", name = "Foo"), Item("2", url = "http://foo.bar", name = "Bar"))
            coEvery { itemRepository.getItems(null) } returns Page("nextToken", items)

            val monitorCommand =
                MonitorCommand(
                    itemRepository = itemRepository,
                    notificationRepository = notificationRepository,
                    logRepository = logRepository,
                    delayBetweenScrapes = 1.seconds,
                    strategies = listOf(mockStrategy),
                    maxLoopCount = MonitorCommand.LOOP_INFINITE,
                )

            val result = monitorCommand.execute()

            assert(result == CommandResult.Repeat)
        }

    @Test
    fun `test execute with max loop count reached`() =
        runTest {
            val items = listOf(Item("1", url = "http://foo.bar", name = "Foo"))
            coEvery { itemRepository.getItems(null) } returns Page(null, items)

            val monitorCommand =
                MonitorCommand(
                    itemRepository = itemRepository,
                    notificationRepository = notificationRepository,
                    logRepository = logRepository,
                    delayBetweenScrapes = 1.seconds,
                    strategies = listOf(mockStrategy),
                    maxLoopCount = 1,
                )

            val result = monitorCommand.execute()
            assert(result == CommandResult.Completed)
        }

    @Test
    fun `test shouldRun always true`() =
        runBlocking {
            val monitorCommand =
                MonitorCommand(
                    itemRepository = itemRepository,
                    notificationRepository = notificationRepository,
                    logRepository = logRepository,
                    delayBetweenScrapes = 1.seconds,
                    strategies = listOf(),
                    maxLoopCount = 0,
                )
            assert(monitorCommand.shouldRun())
        }
}

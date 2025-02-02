package com.cereal.script.commands.monitor

import com.cereal.script.RunDecision
import com.cereal.script.commands.ChainContext
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
import kotlin.test.assertNotNull
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

            val result = monitorCommand.execute(ChainContext())

            coVerify {
                logRepository.info(
                    "Found and processed a total of 2 items.",
                    any(),
                )
            }
            assertNotNull(result.monitorItems)
            assert(result.monitorItems?.size == 2)
        }

    @Test
    fun `test execute with continue page of items`() =
        runTest {
            val items =
                listOf(Item("1", url = "http://foo.bar", name = "Foo"), Item("2", url = "http://foo.bar", name = "Bar"))
            coEvery { itemRepository.getItems(null) } returns Page("nextToken", items)
            coEvery { itemRepository.getItems("nextToken") } returns Page(null, items)

            val monitorCommand =
                MonitorCommand(
                    itemRepository = itemRepository,
                    notificationRepository = notificationRepository,
                    logRepository = logRepository,
                    delayBetweenScrapes = 1.seconds,
                    strategies = listOf(mockStrategy),
                    maxLoopCount = MonitorCommand.LOOP_INFINITE,
                )

            val result = monitorCommand.execute(ChainContext())

            assertNotNull(result.monitorItems)
            assert(result.monitorItems?.size == 2)
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

            val result = monitorCommand.execute(ChainContext())
            assertNotNull(result.monitorItems)
            assert(result.monitorItems?.size == 1)
        }

    @Test
    fun `test shouldRun returns RunNow on first run`() =
        runBlocking {
            val monitorCommand =
                MonitorCommand(
                    itemRepository = itemRepository,
                    notificationRepository = notificationRepository,
                    logRepository = logRepository,
                    delayBetweenScrapes = 1.seconds,
                    strategies = listOf(),
                )
            assert(monitorCommand.shouldRun(ChainContext()) == RunDecision.RunNow)
        }

    @Test
    fun `test shouldRun returns RunWithDelay on consecutive runs`() =
        runBlocking {
            val monitorCommand =
                MonitorCommand(
                    itemRepository = itemRepository,
                    notificationRepository = notificationRepository,
                    logRepository = logRepository,
                    delayBetweenScrapes = 1.seconds,
                    strategies = listOf(),
                )
            assert(monitorCommand.shouldRun(ChainContext(monitorItems = emptyMap())) == RunDecision.RunWithDelay(1.seconds))
        }
}

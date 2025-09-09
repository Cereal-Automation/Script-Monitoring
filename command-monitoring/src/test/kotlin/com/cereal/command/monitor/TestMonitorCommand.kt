package com.cereal.command.monitor

import com.cereal.command.monitor.models.Currency
import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.command.monitor.models.Page
import com.cereal.command.monitor.repository.ItemRepository
import com.cereal.command.monitor.repository.NotificationRepository
import com.cereal.command.monitor.strategy.EqualsOrBelowPriceMonitorStrategy
import com.cereal.command.monitor.strategy.MonitorStrategy
import com.cereal.command.monitor.strategy.NewItemAvailableMonitorStrategy
import com.cereal.command.monitor.strategy.StockAvailableMonitorStrategy
import com.cereal.script.commands.ChainContext
import com.cereal.script.commands.RunDecision
import com.cereal.script.repository.LogRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
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
                )

            val context = ChainContext()
            monitorCommand.execute(context)

            coVerify {
                logRepository.info(
                    "Found and processed a total of 2 items.",
                    any(),
                )
            }
            val monitorStatus = context.get<MonitorStatus>()
            assertNotNull(monitorStatus?.monitorItems)
            assert(monitorStatus?.monitorItems?.size == 2)
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
                )

            val context = ChainContext()
            monitorCommand.execute(context)

            val monitorStatus = context.get<MonitorStatus>()
            assertNotNull(monitorStatus?.monitorItems)
            assert(monitorStatus?.monitorItems?.size == 2)
        }

    @Test
    fun `test execute with single item`() =
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
                )

            val context = ChainContext()
            monitorCommand.execute(context)

            val monitorStatus = context.get<MonitorStatus>()
            assertNotNull(monitorStatus?.monitorItems)
            assert(monitorStatus?.monitorItems?.size == 1)
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
            assert(monitorCommand.shouldRun(ChainContext()) == RunDecision.RunRepeat())
        }

    @Test
    fun `test shouldRun returns RunRepeat on consecutive runs`() =
        runBlocking {
            val monitorCommand =
                MonitorCommand(
                    itemRepository = itemRepository,
                    notificationRepository = notificationRepository,
                    logRepository = logRepository,
                    delayBetweenScrapes = 1.seconds,
                    strategies = listOf(),
                )
            val monitorStatus = MonitorStatus(monitorItems = emptyMap())
            val chainContext = ChainContext().apply { put(monitorStatus) }
            assert(monitorCommand.shouldRun(chainContext) == RunDecision.RunRepeat(1.seconds))
        }

    @ParameterizedTest
    @MethodSource("data")
    fun `test with strategies and verify notifications`(data: TestData) =
        runBlocking {
            coEvery { itemRepository.getItems(null) } returns
                    Page(
                        items =
                            listOf(
                                Item(
                                    id = "foo",
                                    url = "http://cereal-automation.com",
                                    name = "Foo",
                                    properties =
                                        listOf(
                                            ItemProperty.PublishDate(Clock.System.now()),
                                            ItemProperty.Stock(isInStock = true, amount = 1, null),
                                            ItemProperty.Price(BigDecimal("10.00"), Currency.EUR),
                                        ),
                                ),
                                Item(
                                    id = "bar",
                                    url = "http://cereal-automation.com",
                                    name = "Bar",
                                    properties =
                                        listOf(
                                            ItemProperty.PublishDate(Clock.System.now()),
                                            ItemProperty.Stock(isInStock = false, amount = 0, null),
                                            ItemProperty.Price(BigDecimal("10.00"), Currency.EUR),
                                        ),
                                ),
                                Item(
                                    id = "baz",
                                    url = "http://cereal-automation.com",
                                    name = "Baz",
                                    properties =
                                        listOf(
                                            ItemProperty.PublishDate(Clock.System.now().minus(60.seconds)),
                                            ItemProperty.Stock(isInStock = false, amount = 0, null),
                                            ItemProperty.Price(BigDecimal("10.00"), Currency.EUR),
                                        ),
                                ),
                                Item(
                                    id = "cux",
                                    url = "http://cereal-automation.com",
                                    name = "Foo",
                                    properties =
                                        listOf(
                                            ItemProperty.PublishDate(Clock.System.now()),
                                            ItemProperty.Stock(isInStock = true, amount = 1, null),
                                            ItemProperty.Price(BigDecimal("50.00"), Currency.EUR),
                                        ),
                                ),
                            ),
                        nextPageToken = null,
                    )

            val monitorCommand =
                MonitorCommand(
                    itemRepository,
                    notificationRepository,
                    logRepository,
                    delayBetweenScrapes = Duration.ZERO,
                    strategies = listOf(data.strategy),
                )

            val context = ChainContext()

            // Execute twice on purpose.
            monitorCommand.execute(context)
            monitorCommand.execute(context)

            coVerify(exactly = data.numberOfNotifications) { notificationRepository.notify(any(), any()) }
        }

    data class TestData(
        val strategy: MonitorStrategy,
        val numberOfNotifications: Int,
    )

    companion object {
        @JvmStatic
        fun data(): List<TestData> =
            listOf(
                // New item available
                TestData(
                    NewItemAvailableMonitorStrategy(
                        Clock.System.now().minus(1.seconds),
                    ),
                    3,
                ),
                TestData(
                    NewItemAvailableMonitorStrategy(
                        Clock.System.now().plus(60.seconds),
                    ),
                    0,
                ),
                TestData(
                    NewItemAvailableMonitorStrategy(
                        Clock.System.now().minus(100.seconds),
                    ),
                    4,
                ),
                // Stock available
                TestData(
                    StockAvailableMonitorStrategy(),
                    0,
                ),
                // Price equals or below
                TestData(
                    EqualsOrBelowPriceMonitorStrategy(BigDecimal("1"), Currency.EUR),
                    0,
                ),
                TestData(
                    EqualsOrBelowPriceMonitorStrategy(BigDecimal("10"), Currency.EUR),
                    3,
                ),
            )
    }
}

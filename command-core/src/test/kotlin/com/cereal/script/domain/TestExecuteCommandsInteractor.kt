package com.cereal.script.domain

import com.cereal.script.ExecuteCommandsInteractor
import com.cereal.script.commands.ChainContext
import com.cereal.script.commands.monitor.MonitorCommand
import com.cereal.script.commands.monitor.models.Currency
import com.cereal.script.commands.monitor.models.Item
import com.cereal.script.commands.monitor.models.ItemProperty
import com.cereal.script.commands.monitor.models.Page
import com.cereal.script.commands.monitor.repository.ItemRepository
import com.cereal.script.commands.monitor.repository.NotificationRepository
import com.cereal.script.commands.monitor.strategy.EqualsOrBelowPriceMonitorStrategy
import com.cereal.script.commands.monitor.strategy.MonitorStrategy
import com.cereal.script.commands.monitor.strategy.NewItemAvailableMonitorStrategy
import com.cereal.script.commands.monitor.strategy.StockAvailableMonitorStrategy
import com.cereal.script.fixtures.FakeLogRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TestExecuteCommandsInteractor {
    private lateinit var itemRepository: ItemRepository
    private lateinit var notificationRepository: NotificationRepository
    private lateinit var logRepository: FakeLogRepository

    private lateinit var interactor: ExecuteCommandsInteractor

    @BeforeEach
    fun init() {
        itemRepository = mockk<ItemRepository>(relaxed = true)
        notificationRepository = mockk<NotificationRepository>(relaxed = true)
        logRepository = FakeLogRepository()

        interactor = ExecuteCommandsInteractor(logRepository)
    }

    @ParameterizedTest
    @MethodSource("data")
    fun testNotification(data: TestData) =
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
                                        ItemProperty.AvailableStock(1),
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
                                        ItemProperty.AvailableStock(0),
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
                                        ItemProperty.AvailableStock(0),
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
                                        ItemProperty.AvailableStock(1),
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
                    maxLoopCount = 2,
                )
            interactor.invoke(listOf(monitorCommand), startContext = ChainContext()).collect()

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

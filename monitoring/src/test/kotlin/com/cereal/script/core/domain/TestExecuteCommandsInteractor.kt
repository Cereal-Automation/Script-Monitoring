package com.cereal.script.core.domain

import com.cereal.script.commands.monitor.MonitorCommand
import com.cereal.script.commands.monitor.domain.ItemRepository
import com.cereal.script.commands.monitor.domain.NotificationRepository
import com.cereal.script.commands.monitor.domain.models.Currency
import com.cereal.script.commands.monitor.domain.models.Item
import com.cereal.script.commands.monitor.domain.models.ItemValue
import com.cereal.script.commands.monitor.domain.models.Page
import com.cereal.script.commands.monitor.strategy.EqualsOrBelowPriceMonitorStrategy
import com.cereal.script.commands.monitor.strategy.MonitorStrategy
import com.cereal.script.commands.monitor.strategy.NewItemAvailableMonitorStrategy
import com.cereal.script.commands.monitor.strategy.StockAvailableMonitorStrategy
import com.cereal.script.core.fixtures.FakeLogRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.time.Instant
import kotlin.time.Duration

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
                                values =
                                    listOf(
                                        ItemValue.PublishDate(Instant.now()),
                                        ItemValue.AvailableStock(1),
                                        ItemValue.Price(BigDecimal("10.00"), Currency.EUR),
                                    ),
                            ),
                            Item(
                                id = "bar",
                                url = "http://cereal-automation.com",
                                name = "Bar",
                                values =
                                    listOf(
                                        ItemValue.PublishDate(Instant.now()),
                                        ItemValue.AvailableStock(0),
                                        ItemValue.Price(BigDecimal("10.00"), Currency.EUR),
                                    ),
                            ),
                            Item(
                                id = "baz",
                                url = "http://cereal-automation.com",
                                name = "Baz",
                                values =
                                    listOf(
                                        ItemValue.PublishDate(Instant.now().minusSeconds(60)),
                                        ItemValue.AvailableStock(0),
                                        ItemValue.Price(BigDecimal("10.00"), Currency.EUR),
                                    ),
                            ),
                            Item(
                                id = "foo",
                                url = "http://cereal-automation.com",
                                name = "Foo",
                                values =
                                    listOf(
                                        ItemValue.PublishDate(Instant.now()),
                                        ItemValue.AvailableStock(1),
                                        ItemValue.Price(BigDecimal("50.00"), Currency.EUR),
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
                    maxLoopCount = 1,
                )
            interactor.invoke(listOf(monitorCommand)).collect()

            coVerify(exactly = data.numberOfNotifications) { notificationRepository.notify(any()) }
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
                        Instant.now().minusSeconds(1),
                    ),
                    3,
                ),
                TestData(
                    NewItemAvailableMonitorStrategy(
                        Instant.now().plusSeconds(60),
                    ),
                    0,
                ),
                TestData(
                    NewItemAvailableMonitorStrategy(
                        Instant.now().minusSeconds(100),
                    ),
                    4,
                ),
                // Stock available
                TestData(
                    StockAvailableMonitorStrategy(),
                    2,
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
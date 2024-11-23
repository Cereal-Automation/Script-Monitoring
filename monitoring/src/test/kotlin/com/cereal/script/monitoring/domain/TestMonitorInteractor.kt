package com.cereal.script.monitoring.domain

import com.cereal.script.monitoring.domain.models.Currency
import com.cereal.script.monitoring.domain.models.Item
import com.cereal.script.monitoring.domain.models.ItemValue
import com.cereal.script.monitoring.domain.repository.ExecutionRepository
import com.cereal.script.monitoring.domain.repository.ItemRepository
import com.cereal.script.monitoring.domain.repository.LogRepository
import com.cereal.script.monitoring.domain.repository.NotificationRepository
import com.cereal.script.monitoring.domain.strategy.EqualsOrBelowPriceMonitorStrategy
import com.cereal.script.monitoring.domain.strategy.MonitorStrategy
import com.cereal.script.monitoring.domain.strategy.NewItemAvailableMonitorStrategy
import com.cereal.script.monitoring.domain.strategy.StockAvailableMonitorStrategy
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.time.Instant

class TestMonitorInteractor {
    private lateinit var itemRepository: ItemRepository
    private lateinit var notificationRepository: NotificationRepository
    private lateinit var logRepository: LogRepository
    private lateinit var executionRepository: ExecutionRepository

    private lateinit var interactor: MonitorInteractor

    @BeforeEach
    fun init() {
        itemRepository = mockk<ItemRepository>(relaxed = true)
        notificationRepository = mockk<NotificationRepository>(relaxed = true)
        logRepository = mockk<LogRepository>(relaxed = true)
        executionRepository = mockk<ExecutionRepository>(relaxed = true)

        interactor = MonitorInteractor(itemRepository, notificationRepository, logRepository, executionRepository)
    }

    @ParameterizedTest
    @MethodSource("data")
    fun testNotification(data: TestData) =
        runBlocking {
            coEvery { itemRepository.getItems() } returns
                flowOf(
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
                )

            interactor.invoke(
                config = MonitorInteractor.Config(listOf(data.strategy)),
            )

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
                        Instant.now().plusSeconds(1),
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

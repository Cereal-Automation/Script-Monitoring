package com.cereal.script.monitoring.domain

import com.cereal.script.monitoring.domain.models.Currency
import com.cereal.script.monitoring.domain.models.Item
import com.cereal.script.monitoring.domain.models.ItemValue
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
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigDecimal
import java.time.Instant

@RunWith(value = Parameterized::class)
class TestMonitorInteractor(
    val strategy: MonitorStrategy,
    val numberOfNotifications: Int,
) {
    private lateinit var itemRepository: ItemRepository
    private lateinit var notificationRepository: NotificationRepository
    private lateinit var logRepository: LogRepository

    private lateinit var interactor: MonitorInteractor

    @Before
    fun init() {
        itemRepository = mockk<ItemRepository>(relaxed = true)
        notificationRepository = mockk<NotificationRepository>(relaxed = true)
        logRepository = mockk<LogRepository>(relaxed = true)

        interactor = MonitorInteractor(itemRepository, notificationRepository, logRepository)
    }

    @Test
    fun testNotification() =
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
                config = MonitorInteractor.Config(listOf(strategy)),
            )

            coVerify(exactly = numberOfNotifications) { notificationRepository.notify(any()) }
        }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): List<Array<Any>> =
            listOf(
                // New item available
                arrayOf(
                    NewItemAvailableMonitorStrategy(
                        Instant.now().minusSeconds(1),
                    ),
                    3,
                ),
                arrayOf(
                    NewItemAvailableMonitorStrategy(
                        Instant.now().plusSeconds(1),
                    ),
                    0,
                ),
                arrayOf(
                    NewItemAvailableMonitorStrategy(
                        Instant.now().minusSeconds(100),
                    ),
                    4,
                ),
                // Stock available
                arrayOf(
                    StockAvailableMonitorStrategy(),
                    2,
                ),
                // Price equals or below
                arrayOf(
                    EqualsOrBelowPriceMonitorStrategy(BigDecimal("1"), Currency.EUR),
                    0,
                ),
                arrayOf(
                    EqualsOrBelowPriceMonitorStrategy(BigDecimal("50"), Currency.EUR),
                    4,
                ),
            )
    }
}

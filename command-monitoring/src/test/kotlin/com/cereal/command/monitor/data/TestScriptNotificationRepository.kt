package com.cereal.command.monitor.data

import com.cereal.command.monitor.models.Currency
import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.ItemProperty
import com.cereal.sdk.component.notification.NotificationComponent
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import kotlin.test.Test

@OptIn(kotlin.time.ExperimentalTime::class)
class TestScriptNotificationRepository {
    private val mockNotificationComponent = mockk<NotificationComponent>(relaxed = true)
    private val repository =
        ScriptNotificationRepository(
            notificationComponent = mockNotificationComponent,
            discordUsername = "TestBot",
            discordAvatarUrl = "https://example.com/avatar.png",
            discordColor = "123456",
        )

    @Test
    fun `notify should call sendNotification with a notification`() =
        runBlocking {
            // Given
            val message = "Test notification"
            val item = createTestItem()

            // When
            repository.notify(message, item)

            // Then
            coVerify { mockNotificationComponent.sendNotification(any()) }
        }

    private fun createTestItem(): Item =
        Item(
            id = "test-id",
            url = "https://example.com/item",
            name = "Test Item",
            description = "Test Description",
            imageUrl = "https://example.com/image.jpg",
            variants = emptyList(),
            properties =
                listOf(
                    ItemProperty.Price(BigDecimal("99.99"), Currency.USD),
                    ItemProperty.Stock(true, 5, null),
                ),
        )
}

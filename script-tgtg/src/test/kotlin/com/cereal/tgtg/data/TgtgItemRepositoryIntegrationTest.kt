package com.cereal.tgtg.data

import com.cereal.script.fixtures.FakeLogRepository
import com.cereal.sdk.component.preference.PreferenceComponent
import com.cereal.tgtg.data.apiclients.PlayStoreApiClient
import com.cereal.tgtg.data.apiclients.TgtgApiClient
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class TgtgItemRepositoryIntegrationTest {
    @Tag("integration")
    @Test
    fun testGetItems() =
        runBlocking {
            val repository =
                TgtgItemRepository(
                    FakeLogRepository(),
                    TgtgApiClient(
                        FakeLogRepository(),
                        mockk<PreferenceComponent>(relaxed = true),
                        mockk<PlayStoreApiClient>(relaxed = true),
                        null,
                    ),
                    latitude = 52.3676,
                    longitude = 4.9041,
                    radius = 10000,
                )

            val result = repository.getItems(null)
            assertNotNull(result)
        }
}

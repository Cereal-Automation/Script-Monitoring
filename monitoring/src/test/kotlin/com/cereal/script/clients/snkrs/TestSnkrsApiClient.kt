package com.cereal.script.clients.snkrs

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Tag
import kotlin.test.Test
import kotlin.test.assertNotNull

class TestSnkrsApiClient {
    @Tag("integration")
    @Test
    fun `test login success`() =
        runTest {
            val client = SnkrsApiClient()

            val response = client.getProducts(Locale.NL_NL, 0, 50)
            assertNotNull(response)
        }
}

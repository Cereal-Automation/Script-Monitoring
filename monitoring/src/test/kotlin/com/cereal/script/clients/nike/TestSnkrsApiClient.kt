package com.cereal.script.clients.nike

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

            val response = client.getProducts(Locale.NL_NL)
            assertNotNull(response)
        }
}
package com.cereal.script.clients.toogoodtogo

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Tag
import kotlin.test.Test
import kotlin.test.assertNotNull

class TestTgtgClient {
    @Tag("integration")
    @Test
    fun `test login success`() =
        runTest {
            val client = TgtgClient(email = "robderijk89@gmail.com")

            val response = client.login()
            assertNotNull(response)
        }
}

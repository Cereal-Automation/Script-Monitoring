package com.cereal.tgtg

import com.cereal.sdk.component.ComponentProvider
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class TgtgScriptTest {
    @Test
    fun `onStart should return true`() =
        runBlocking {
            val script = TgtgScript()
            val configuration = createTestConfiguration()
            val provider = mockk<ComponentProvider>(relaxed = true)

            val result = script.onStart(configuration, provider)

            assertTrue(result)
        }

    private fun createTestConfiguration(): TgtgConfiguration =
        object : TgtgConfiguration {
            override fun email(): String = "test@example.com"

            override fun latitude(): Double = 52.3676

            override fun longitude(): Double = 4.9041

            override fun radius(): Int = 50000

            override fun favoritesOnly(): Boolean = false

            override fun monitorNewItems(): Boolean = true

            override fun monitorStockChanges(): Boolean = true

            override fun enableInteractiveAuth(): Boolean = true

            override fun proxy() = null

            override fun monitorInterval(): Int = 30
        }
}

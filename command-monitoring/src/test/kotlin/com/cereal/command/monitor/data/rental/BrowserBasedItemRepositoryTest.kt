package com.cereal.command.monitor.data.rental

import com.cereal.command.monitor.fixtures.repositories.FakeLogRepository
import com.cereal.command.monitor.models.Item
import com.cereal.script.exception.ChromeNotInstalledException
import dev.kdriver.core.browser.Browser
import dev.kdriver.core.exceptions.NoBrowserExecutablePathException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BrowserBasedItemRepositoryTest {
    private class StartupFailureRepository(
        private val startupError: Exception,
    ) : BrowserBasedItemRepository(
            cities = listOf("amsterdam"),
            logRepository = FakeLogRepository(),
        ) {
        override val name: String = "Test"

        override suspend fun startBrowser(scope: CoroutineScope): Browser = throw startupError

        override suspend fun fetchCity(
            city: String,
            browser: Browser,
        ): List<Item> = error("Should not be reached when browser startup fails")
    }

    @Test
    fun `runtime exception during browser startup skips the run instead of throwing`() =
        runTest {
            val repository = StartupFailureRepository(IllegalStateException())

            val page = repository.getItems(null)

            assertTrue(page.items.isEmpty())
        }

    @Test
    fun `missing browser executable still throws ChromeNotInstalledException`() =
        runTest {
            val repository = StartupFailureRepository(NoBrowserExecutablePathException())

            assertThrows<ChromeNotInstalledException> {
                repository.getItems(null)
            }
        }
}

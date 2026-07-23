package com.cereal.rental.data

import com.cereal.command.monitor.models.Item
import com.cereal.command.monitor.models.Page
import com.cereal.command.monitor.repository.ItemRepository
import com.cereal.script.exception.ChromeNotInstalledException
import com.cereal.script.repository.LogRepository
import dev.kdriver.core.browser.Browser
import dev.kdriver.core.browser.createBrowser
import dev.kdriver.core.exceptions.BrowserExecutableNotFoundException
import dev.kdriver.core.exceptions.NoBrowserExecutablePathException
import dev.kdriver.core.tab.ReadyState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import java.math.BigDecimal

abstract class BrowserBasedItemRepository(
    protected val cities: List<String>,
    protected val logRepository: LogRepository,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ItemRepository {
    override suspend fun getItems(nextPageToken: String?): Page {
        val items = mutableListOf<Item>()
        val browserScope = CoroutineScope(coroutineDispatcher + SupervisorJob())
        val browser =
            try {
                withTimeout(BROWSER_START_TIMEOUT_MS) {
                    startBrowser(browserScope)
                }
            } catch (e: TimeoutCancellationException) {
                browserScope.coroutineContext[Job]?.cancel()
                logRepository.warn("$name: browser startup timed out, skipping run: ${e.message}")
                return Page(nextPageToken = null, items = items)
            } catch (e: CancellationException) {
                browserScope.coroutineContext[Job]?.cancel()
                throw e
            } catch (e: NoBrowserExecutablePathException) {
                browserScope.coroutineContext[Job]?.cancel()
                throw ChromeNotInstalledException(e)
            } catch (e: BrowserExecutableNotFoundException) {
                browserScope.coroutineContext[Job]?.cancel()
                throw ChromeNotInstalledException(e)
            } catch (e: Exception) {
                // Browser startup can fail transiently (e.g. a stale Chrome process or profile lock).
                // Skip this run instead of killing the script; the next scheduled run starts fresh.
                browserScope.coroutineContext[Job]?.cancel()
                logRepository.warn(
                    "$name: browser failed to start, skipping run: ${e.message ?: e::class.simpleName}",
                )
                return Page(nextPageToken = null, items = items)
            }

        try {
            for ((index, city) in cities.withIndex()) {
                logRepository.info("$name: scraping city ${index + 1}/${cities.size}: '$city'")
                try {
                    items += fetchCity(city, browser)
                } catch (e: Exception) {
                    logRepository.info("$name: failed to fetch listings for city '$city': ${e.message}")
                }
            }
        } finally {
            runCatching {
                withTimeout(BROWSER_STOP_TIMEOUT_MS) { browser.stop() }
            }.onFailure { e ->
                logRepository.warn("$name: browser failed to stop cleanly: ${e.message}")
            }
            browserScope.coroutineContext[Job]?.cancel()
        }

        return Page(nextPageToken = null, items = items)
    }

    protected open suspend fun startBrowser(scope: CoroutineScope): Browser = createBrowser(scope) { headless = true }

    protected abstract suspend fun fetchCity(
        city: String,
        browser: Browser,
    ): List<Item>

    protected suspend fun fetchWithBrowser(
        url: String,
        browser: Browser,
    ): String {
        var lastError: Throwable? = null
        repeat(5) {
            try {
                return withTimeout(PAGE_FETCH_TIMEOUT_MS) {
                    val page = browser.get(url)
                    page.waitForReadyState(ReadyState.COMPLETE, timeout = READY_STATE_TIMEOUT_MS)
                    page.getContent()
                }
            } catch (e: TimeoutCancellationException) {
                lastError = e
                delay(500)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                lastError = e
                delay(500)
            }
        }
        throw IllegalStateException("Could not get page from browser: $url", lastError)
    }

    companion object {
        const val BROWSER_START_TIMEOUT_MS: Long = 60_000
        const val BROWSER_STOP_TIMEOUT_MS: Long = 10_000
        const val PAGE_FETCH_TIMEOUT_MS: Long = 30_000
        const val READY_STATE_TIMEOUT_MS: Long = 10_000
        const val LISTING_PROGRESS_EVERY: Int = 10

        fun parsePrice(raw: String): BigDecimal? {
            if (raw.isBlank()) return null
            val digits = raw.replace(Regex("[^\\d]"), "")
            return digits.toBigDecimalOrNull()
        }

        fun parseSizeM2(raw: String): Int? {
            if (raw.isBlank()) return null
            return Regex("""\d+""").find(raw)?.value?.toIntOrNull()
        }

        fun parseRooms(raw: String): Int? {
            if (raw.isBlank()) return null
            return raw.trim().split(" ").firstOrNull()?.toIntOrNull()
        }
    }
}

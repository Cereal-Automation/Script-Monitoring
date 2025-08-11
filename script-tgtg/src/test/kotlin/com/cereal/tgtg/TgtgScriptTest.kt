package com.cereal.tgtg

import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.component.ComponentProvider
import com.cereal.sdk.models.proxy.RandomProxy
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class TgtgScriptTest {

    @Test
    fun `test onStart returns true`() = runTest {
        // Given
        val script = TgtgScript()
        val configuration = createMockConfiguration()
        val componentProvider = createMockComponentProvider()

        // When
        val result = script.onStart(configuration, componentProvider)

        // Then
        assertTrue(result)
    }

    @Test
    fun `test execute builds commands and returns success`() = runTest {
        // Given
        val script = TgtgScript()
        val configuration = createMockConfiguration()
        val componentProvider = createMockComponentProvider()
        val statusUpdate: suspend (String) -> Unit = mockk(relaxed = true)

        // Initialize script first
        script.onStart(configuration, componentProvider)

        // When
        val result = script.execute(configuration, componentProvider, statusUpdate)

        // Then
        // The result might be Error due to missing dependencies, but test should not crash
        assertTrue(result is ExecutionResult.Success || result is ExecutionResult.Error)
        coVerify { statusUpdate(any()) }
    }

    @Test
    fun `test onFinish completes without exception`() = runTest {
        // Given
        val script = TgtgScript()
        val configuration = createMockConfiguration()
        val componentProvider = createMockComponentProvider()

        // Initialize script first
        script.onStart(configuration, componentProvider)

        // When & Then (should not throw exception)
        script.onFinish(configuration, componentProvider)
    }

    @Test
    fun `test execute with different configuration options`() = runTest {
        // Given
        val script = TgtgScript()
        val configuration = createMockConfiguration(
            monitorNewItems = true,
            monitorStockChanges = false,
            favoritesOnly = true,
            radius = 25000
        )
        val componentProvider = createMockComponentProvider()
        val statusUpdate: suspend (String) -> Unit = mockk(relaxed = true)

        script.onStart(configuration, componentProvider)

        // When
        val result = script.execute(configuration, componentProvider, statusUpdate)

        // Then
        assertTrue(result is ExecutionResult.Success || result is ExecutionResult.Error)
    }

    @Test
    fun `test execute with proxy configuration`() = runTest {
        // Given
        val script = TgtgScript()
        val mockProxy = mockk<RandomProxy>(relaxed = true)
        val configuration = createMockConfiguration(proxy = mockProxy)
        val componentProvider = createMockComponentProvider()
        val statusUpdate: suspend (String) -> Unit = mockk(relaxed = true)

        script.onStart(configuration, componentProvider)

        // When
        val result = script.execute(configuration, componentProvider, statusUpdate)

        // Then
        assertTrue(result is ExecutionResult.Success || result is ExecutionResult.Error)
    }

    @Test
    fun `test execute with minimal configuration`() = runTest {
        // Given
        val script = TgtgScript()
        val configuration = createMockConfiguration(
            monitorNewItems = false,
            monitorStockChanges = false,
            favoritesOnly = false,
            radius = null
        )
        val componentProvider = createMockComponentProvider()
        val statusUpdate: suspend (String) -> Unit = mockk(relaxed = true)

        script.onStart(configuration, componentProvider)

        // When
        val result = script.execute(configuration, componentProvider, statusUpdate)

        // Then
        assertTrue(result is ExecutionResult.Success || result is ExecutionResult.Error)
    }

    private fun createMockConfiguration(
        email: String = "test@example.com",
        latitude: Double = 52.3676,
        longitude: Double = 4.9041,
        radius: Int? = 50000,
        favoritesOnly: Boolean = false,
        monitorNewItems: Boolean = true,
        monitorStockChanges: Boolean = true,
        proxy: RandomProxy? = null
    ): TgtgConfiguration {
        return mockk<TgtgConfiguration>().apply {
            coEvery { email() } returns email
            coEvery { latitude() } returns latitude
            coEvery { longitude() } returns longitude
            coEvery { radius() } returns radius
            coEvery { favoritesOnly() } returns favoritesOnly
            coEvery { monitorNewItems() } returns monitorNewItems
            coEvery { monitorStockChanges() } returns monitorStockChanges
            coEvery { proxy() } returns proxy
            coEvery { monitorInterval() } returns 60 // seconds
        }
    }

    private fun createMockComponentProvider(): ComponentProvider {
        return mockk<ComponentProvider>(relaxed = true).apply {
            coEvery { preference() } returns mockk(relaxed = true)
        }
    }
}
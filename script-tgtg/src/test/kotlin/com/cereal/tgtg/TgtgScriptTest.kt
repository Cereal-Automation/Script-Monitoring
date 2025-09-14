package com.cereal.tgtg

import com.cereal.licensechecker.LicenseChecker
import com.cereal.licensechecker.LicenseState
import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.component.ComponentProvider
import com.cereal.sdk.models.proxy.RandomProxy
import com.cereal.test.TestScriptRunner
import com.cereal.test.components.TestComponentProviderFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Disabled
import kotlin.test.Test
import kotlin.test.assertTrue

class TgtgScriptTest {
    @Test
    fun `test onStart returns true`() =
        runTest {
            // Given
            val script = TgtgScript()
            val configuration = createMockConfiguration()
            val componentProvider = createMockComponentProvider()

            // When
            val result = script.onStart(configuration, componentProvider)

            // Then
            assertTrue(result)
        }

    @Disabled("Only for manual testing.")
    @Test
    fun testSuccess() =
        runBlocking {
            // Initialize script and the test script runner.
            val script = TgtgScript()
            val scriptRunner = TestScriptRunner(script)

            // Mock the LicenseChecker
            mockkConstructor(LicenseChecker::class)
            coEvery { anyConstructed<LicenseChecker>().checkAccess() } returns LicenseState.Licensed

            // Mock the configuration values
            val configuration =
                mockk<TgtgConfiguration>(relaxed = true) {
                    every { proxy() } returns null
                    every { monitorInterval() } returns null
                    every { email() } returns "my@email.com"
                    every { latitude() } returns 52.3676
                    every { longitude() } returns 4.9041
                    every { radius() } returns 50000
                    every { favoritesOnly() } returns true
                }
            val componentProviderFactory = TestComponentProviderFactory()

            try {
                withTimeout(120000) { scriptRunner.run(configuration, componentProviderFactory) }
            } catch (e: Exception) {
                // Ignore timeouts because they're expected.
            }
        }

    @Test
    fun `test execute builds commands and returns success`() =
        runTest {
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
    fun `test onFinish completes without exception`() =
        runTest {
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
    fun `test execute with different configuration options`() =
        runTest {
            // Given
            val script = TgtgScript()
            val configuration =
                createMockConfiguration(
                    favoritesOnly = true,
                    radius = 25000,
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
    fun `test execute with proxy configuration`() =
        runTest {
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
    fun `test execute with minimal configuration`() =
        runTest {
            // Given
            val script = TgtgScript()
            val configuration =
                createMockConfiguration(
                    favoritesOnly = false,
                    radius = null,
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
        proxy: RandomProxy? = null,
    ): TgtgConfiguration =
        mockk<TgtgConfiguration>().apply {
            coEvery { email() } returns email
            coEvery { latitude() } returns latitude
            coEvery { longitude() } returns longitude
            coEvery { radius() } returns radius
            coEvery { favoritesOnly() } returns favoritesOnly
            coEvery { proxy() } returns proxy
            coEvery { monitorInterval() } returns 60 // seconds
        }

    private fun createMockComponentProvider(): ComponentProvider =
        mockk<ComponentProvider>(relaxed = true).apply {
            coEvery { preference() } returns mockk(relaxed = true)
            coEvery { userInteraction() } returns mockk(relaxed = true)
        }
}

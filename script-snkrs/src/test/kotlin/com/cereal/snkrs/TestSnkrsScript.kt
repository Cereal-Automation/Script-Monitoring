package com.cereal.snkrs

import com.cereal.command.monitor.data.snkrs.Locale
import com.cereal.licensechecker.LicenseChecker
import com.cereal.licensechecker.LicenseState
import com.cereal.test.TestScriptRunner
import com.cereal.test.components.TestComponentProviderFactory
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class TestSnkrsScript {
    @Disabled("Only for manual testing.")
    @Test
    fun testSuccess() =
        runBlocking {
            // Initialize script and the test script runner.
            val script = SnkrsScript()
            val scriptRunner = TestScriptRunner(script)

            // Mock the LicenseChecker
            mockkConstructor(LicenseChecker::class)
            coEvery { anyConstructed<LicenseChecker>().checkAccess() } returns LicenseState.Licensed

            // Mock the configuration values
            val configuration =
                mockk<SnkrsConfiguration>(relaxed = true) {
                    every { proxy() } returns null
                    every { monitorInterval() } returns null
                    every { locale() } returns Locale.BE_NL
                }
            val componentProviderFactory = TestComponentProviderFactory()

            try {
                withTimeout(10000) { scriptRunner.run(configuration, componentProviderFactory) }
            } catch (e: Exception) {
                // Ignore timeouts because they're expected.
            }
        }
}

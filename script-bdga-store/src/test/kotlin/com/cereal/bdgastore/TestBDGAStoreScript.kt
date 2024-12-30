package com.cereal.bdgastore

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

class TestBDGAStoreScript {
    @Test
    @Disabled("Only for manual testing.")
    fun testSuccess() =
        runBlocking {
            // Initialize script and the test script runner.
            val script = BDGAStoreScript()
            val scriptRunner = TestScriptRunner(script)

            // Mock the LicenseChecker
            mockkConstructor(LicenseChecker::class)
            coEvery { anyConstructed<LicenseChecker>().checkAccess() } returns LicenseState.Licensed

            // Mock the configuration values
            val configuration =
                mockk<BDGAStoreConfiguration>(relaxed = true) {
                    every { proxy() } returns null
                    every { monitorInterval() } returns null
                }
            val componentProviderFactory = TestComponentProviderFactory()

            try {
                withTimeout(10000) { scriptRunner.run(configuration, componentProviderFactory) }
            } catch (e: Exception) {
                // Ignore timeouts because they're expected.
            }
        }
}

package com.cereal.script.monitoring

import com.cereal.licensechecker.LicenseChecker
import com.cereal.licensechecker.LicenseState
import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.component.ComponentProvider
import com.cereal.test.components.TestComponentProviderFactory
import io.mockk.coEvery
import io.mockk.mockkConstructor
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.time.Duration.Companion.seconds

@RunWith(value = Parameterized::class)
class TestMonitor(private val monitor: Monitor) {
    @Test
    fun testMonitor() =
        runBlocking {
            // Mock the LicenseChecker
            mockkConstructor(LicenseChecker::class)
            coEvery { anyConstructed<LicenseChecker>().checkAccess() } returns LicenseState.Licensed

            val componentProviderFactory = TestComponentProviderFactory()
            val componentProvider: ComponentProvider = componentProviderFactory.create()

            withTimeout(15.seconds) {
                monitor.onStart(componentProvider)
                val result = monitor.execute(componentProvider, {})
                monitor.onFinish()

                assert(result is ExecutionResult.Loop)
            }
        }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): List<Array<Monitor>> {
            return MonitorFactory.allMonitors.map {
                arrayOf(it)
            }
        }
    }
}

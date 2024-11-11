package com.cereal.script.monitoring

import com.cereal.licensechecker.LicenseChecker
import com.cereal.licensechecker.LicenseState
import com.cereal.script.monitoring.domain.models.MonitorMode
import com.cereal.script.monitoring.fixtures.FakeItemRepository
import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.component.ComponentProvider
import com.cereal.test.components.TestComponentProviderFactory
import io.mockk.coEvery
import io.mockk.mockkConstructor
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.time.Instant

class TestMonitor {
    @Test
    fun testMonitor() =
        runBlocking {
            // Mock the LicenseChecker
            mockkConstructor(LicenseChecker::class)
            coEvery { anyConstructed<LicenseChecker>().checkAccess() } returns LicenseState.Licensed

            val componentProviderFactory = TestComponentProviderFactory()
            val componentProvider: ComponentProvider = componentProviderFactory.create()

            val monitor =
                Monitor(
                    scriptId = "com.cereal-automation.test",
                    scriptPublicKey = null,
                    monitorMode = MonitorMode.NewItemAvailable(Instant.now()),
                    itemRepository = FakeItemRepository(emptyList()),
                )
            monitor.onStart(componentProvider)
            val result = monitor.execute(componentProvider, {})
            monitor.onFinish()

            assert(result is ExecutionResult.Loop)
        }
}

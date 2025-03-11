package com.cereal.shared

import com.cereal.licensechecker.LicenseChecker
import com.cereal.licensechecker.LicenseState
import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.component.ComponentProvider
import com.cereal.shared.fixtures.FooCommand
import com.cereal.test.components.TestComponentProviderFactory
import io.mockk.coEvery
import io.mockk.mockkConstructor
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class TestCommandExecutionScript {
    @Test
    fun testSuccess() =
        runBlocking {
            // Mock the LicenseChecker
            mockkConstructor(LicenseChecker::class)
            coEvery { anyConstructed<LicenseChecker>().checkAccess() } returns LicenseState.Licensed

            val componentProviderFactory = TestComponentProviderFactory()
            val componentProvider: ComponentProvider = componentProviderFactory.create()

            val commandExecutionScript =
                CommandExecutionScript(
                    scriptId = "com.cereal-automation.test",
                    scriptPublicKey = null,
                )
            commandExecutionScript.onStart(componentProvider)

            val commands =
                listOf(
                    FooCommand(
                        numberOfRuns = 1,
                    ),
                )
            val result = commandExecutionScript.execute(componentProvider, {}, commands)
            commandExecutionScript.onFinish()

            assert(result is ExecutionResult.Success)
        }
}

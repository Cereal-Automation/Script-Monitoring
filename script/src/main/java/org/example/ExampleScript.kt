package org.example

import com.cereal.sdk.ExecutionResult
import com.cereal.sdk.Script
import com.cereal.sdk.component.ComponentProvider

class ExampleScript : Script<ExampleConfiguration> {

    override suspend fun execute(
        configuration: ExampleConfiguration,
        provider: ComponentProvider,
        statusUpdate: suspend (message: String) -> Unit
    ): ExecutionResult {
        TODO("Not yet implemented")
    }

    override suspend fun onFinish(
        configuration: ExampleConfiguration,
        provider: ComponentProvider
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun onStart(
        configuration: ExampleConfiguration,
        provider: ComponentProvider
    ): Boolean {
        TODO("Not yet implemented")
    }
}
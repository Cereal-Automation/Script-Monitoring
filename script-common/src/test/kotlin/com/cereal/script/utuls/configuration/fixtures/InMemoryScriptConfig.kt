package com.cereal.script.utuls.configuration.fixtures

import com.cereal.sdk.statemodifier.ScriptConfig
import com.cereal.sdk.statemodifier.ScriptConfigValue

class InMemoryScriptConfig(
    private val configuration: Map<String, ScriptConfigValue>,
) : ScriptConfig {
    override fun valueForKey(key: String): ScriptConfigValue = configuration[key] ?: ScriptConfigValue.NullScriptConfigValue
}

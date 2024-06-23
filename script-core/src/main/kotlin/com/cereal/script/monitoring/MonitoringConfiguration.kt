package com.cereal.script.monitoring

import com.cereal.sdk.ScriptConfiguration
import com.cereal.sdk.ScriptConfigurationItem

interface MonitoringConfiguration : ScriptConfiguration {

    @ScriptConfigurationItem(
        keyName = "StringKey",
        name = "KeyOfString",
        description = "A very long long looooong description text which should describe the function of this configuration"
    )
    fun keyString(): String {
        return "default"
    }

}

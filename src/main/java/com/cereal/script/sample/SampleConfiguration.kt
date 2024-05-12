package com.cereal.script.sample

import com.cereal.sdk.ScriptConfiguration
import com.cereal.sdk.ScriptConfigurationItem

interface SampleConfiguration : ScriptConfiguration {

    @ScriptConfigurationItem(
        keyName = "StringKey",
        name = "KeyOfString",
        description = "A very long long looooong description text which should describe the function of this configuration"
    )
    fun keyString(): String {
        return "default"
    }

    @ScriptConfigurationItem(
        keyName = "BooleanKey",
        name = "KeyOfBoolean",
        description = "A very long long looooong description text which should describe the function of this configuration"
    )
    fun keyBoolean(): Boolean {
        return true
    }

    @ScriptConfigurationItem(
        keyName = "IntegerKey",
        name = "KeyOfInteger",
        description = "A very long long looooong description text which should describe the function of this configuration"
    )
    fun keyInteger(): Int {
        return 5000
    }

    @ScriptConfigurationItem(
        keyName = "FloatingKey",
        name = "KeyOfFloat",
        description = "A very long long looooong description text which should describe the function of this configuration"
    )
    fun keyFloat(): Float {
        return 1337.1337f
    }

    @ScriptConfigurationItem(
        keyName = "DoubleKey",
        name = "KeyOfDouble",
        description = "A very long long looooong description text which should describe the function of this configuration"
    )
    fun keyDouble(): Double {
        return 1337.1337
    }
}

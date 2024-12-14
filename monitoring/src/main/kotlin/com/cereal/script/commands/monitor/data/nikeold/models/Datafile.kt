package com.cereal.script.commands.monitor.data.nikeold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Datafile(
    @SerialName("accountId")
    val accountId: String = "",
    @SerialName("anonymizeIP")
    val anonymizeIP: Boolean = false,
    @SerialName("attributes")
    val attributes: List<Attribute> = listOf(),
    @SerialName("audiences")
    val audiences: List<Audience> = listOf(),
    @SerialName("botFiltering")
    val botFiltering: Boolean = false,
    @SerialName("environmentKey")
    val environmentKey: String = "",
    @SerialName("events")
    val events: List<Event> = listOf(),
    @SerialName("experiments")
    val experiments: List<Experiment> = listOf(),
    @SerialName("featureFlags")
    val featureFlags: List<FeatureFlag> = listOf(),
    @SerialName("projectId")
    val projectId: String = "",
    @SerialName("revision")
    val revision: String = "",
    @SerialName("rollouts")
    val rollouts: List<Rollout> = listOf(),
    @SerialName("sdkKey")
    val sdkKey: String = "",
    @SerialName("version")
    val version: String = "",
)

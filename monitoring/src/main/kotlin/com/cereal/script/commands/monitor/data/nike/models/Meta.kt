package com.cereal.script.commands.monitor.data.nike.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Meta(
    @SerialName("content")
    val content: String = "",
    @SerialName("data-api-branch-name")
    val dataApiBranchName: String? = null,
    @SerialName("data-api-build-number")
    val dataApiBuildNumber: String? = null,
    @SerialName("data-api-commit-sha")
    val dataApiCommitSha: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("property")
    val `property`: String? = null,
)

package com.cereal.script.commands.monitor.data.nikeold.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiMetaData(
    @SerialName("x-b3-traceid")
    val xB3Traceid: String = "",
    @SerialName("x-branch-name")
    val xBranchName: String = "",
    @SerialName("x-build-number")
    val xBuildNumber: String = "",
    @SerialName("x-commit-sha")
    val xCommitSha: String = "",
)

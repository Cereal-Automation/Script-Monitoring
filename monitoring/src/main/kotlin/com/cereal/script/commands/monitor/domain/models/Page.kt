package com.cereal.script.commands.monitor.domain.models

data class Page(
    val nextPageToken: String?,
    val items: List<Item>,
)

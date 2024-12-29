package com.cereal.script.commands.monitor.models

data class Page(
    val nextPageToken: String?,
    val items: List<Item>,
)

package com.cereal.command.monitor.models

data class Page(
    val nextPageToken: String?,
    val items: List<Item>,
)

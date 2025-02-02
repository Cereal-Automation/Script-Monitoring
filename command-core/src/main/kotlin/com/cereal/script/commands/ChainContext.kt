package com.cereal.script.commands

import com.cereal.script.commands.monitor.models.Item

data class ChainContext(
    val monitorItems: Map<String, Item>? = null,
    val monitorRunSequenceNumber: Int = 0,
)

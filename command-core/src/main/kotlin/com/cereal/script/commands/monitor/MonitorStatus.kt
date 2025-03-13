package com.cereal.script.commands.monitor

import com.cereal.script.commands.monitor.models.Item

data class MonitorStatus(
    val monitorItems: Map<String, Item>? = null,
    val monitorRunSequenceNumber: Int = 0,
)

package com.cereal.command.monitor

import com.cereal.command.monitor.models.Item

data class MonitorStatus(
    val monitorItems: Map<String, Item>? = null,
    val monitorRunSequenceNumber: Int = 0,
)

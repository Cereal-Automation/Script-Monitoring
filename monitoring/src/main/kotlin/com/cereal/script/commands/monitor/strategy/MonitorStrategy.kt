package com.cereal.script.commands.monitor.strategy

import com.cereal.script.commands.monitor.domain.models.Item

interface MonitorStrategy {
    suspend fun shouldNotify(
        item: Item,
        runSequenceNumber: Int,
    ): Boolean

    fun getNotificationMessage(item: Item): String
}

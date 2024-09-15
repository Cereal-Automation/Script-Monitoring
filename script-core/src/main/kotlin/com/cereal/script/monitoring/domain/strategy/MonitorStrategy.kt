package com.cereal.script.monitoring.domain.strategy

import com.cereal.script.monitoring.domain.models.Item

interface MonitorStrategy {

    suspend fun shouldNotify(item: Item): Boolean

    fun getNotificationMessage(item: Item): String

}

package com.cereal.script.monitoring.domain.strategy

import com.cereal.script.monitoring.domain.models.Item
import com.cereal.script.monitoring.domain.models.Value
import kotlin.reflect.KClass

interface MonitorStrategy {

    fun shouldNotify(item: Item): Boolean

}

package com.cereal.script.monitoring.domain.strategy

import com.cereal.script.monitoring.domain.models.Item

class EqualsOrBelowPriceMonitorStrategy: MonitorStrategy {

    override fun shouldNotify(item: Item): Boolean {
        TODO("Not yet implemented")
    }
}

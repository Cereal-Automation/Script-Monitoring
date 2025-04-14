package com.cereal.command.monitor.data.zalando

enum class ZalandoMonitorType(
    private val text: String,
) {
    NewReleases("New releases"), ;

    override fun toString(): String = text
}

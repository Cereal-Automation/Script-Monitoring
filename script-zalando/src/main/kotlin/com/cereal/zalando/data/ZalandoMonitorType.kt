package com.cereal.zalando.data

enum class ZalandoMonitorType(
    private val text: String,
) {
    NewReleases("New releases"), ;

    override fun toString(): String = text
}

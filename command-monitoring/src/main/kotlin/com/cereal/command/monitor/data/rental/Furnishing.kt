package com.cereal.command.monitor.data.rental

enum class Furnishing(
    val parariusSegment: String,
    val fundaSegment: String,
) {
    UNFURNISHED("shell", "ongemeubileerd"),
    UPHOLSTERED("upholstered", "gestoffeerd"),
    FURNISHED("furnished", "gemeubileerd"),
}

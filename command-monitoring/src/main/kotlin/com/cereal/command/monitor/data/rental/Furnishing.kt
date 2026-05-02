package com.cereal.command.monitor.data.rental

enum class Furnishing(
    val parariusSegment: String,
    val fundaSegment: String,
    private val displayName: String,
) {
    UNFURNISHED("shell", "ongemeubileerd", "Unfurnished"),
    UPHOLSTERED("upholstered", "gestoffeerd", "Upholstered"),
    FURNISHED("furnished", "gemeubileerd", "Furnished"),
    ;

    override fun toString(): String = displayName
}

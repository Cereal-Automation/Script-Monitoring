package com.cereal.command.monitor.data.rental

enum class PropertyType(
    val parariusSegment: String?,
    val fundaSegment: String,
    private val displayName: String,
) {
    APARTMENT(null, "appartement", "Apartment"),
    HOUSE("house", "woonhuis", "House"),
    STUDIO("studio", "studio", "Studio"),
    ROOM("room", "kamer", "Room"),
    ;

    override fun toString(): String = displayName
}

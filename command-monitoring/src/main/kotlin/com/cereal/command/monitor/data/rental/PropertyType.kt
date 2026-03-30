package com.cereal.command.monitor.data.rental

enum class PropertyType(
    val parariusSegment: String?,
    val fundaSegment: String,
) {
    APARTMENT(null, "appartement"),
    HOUSE("house", "woonhuis"),
    STUDIO("studio", "studio"),
    ROOM("room", "kamer"),
}

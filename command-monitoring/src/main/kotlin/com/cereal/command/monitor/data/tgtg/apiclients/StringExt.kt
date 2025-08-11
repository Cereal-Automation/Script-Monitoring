package com.cereal.command.monitor.data.tgtg.apiclients

/**
 * Extension function for String that compares version strings.
 *
 * Compares two version strings by splitting them into parts and comparing each part numerically.
 * Supports version strings with different numbers of parts (e.g., "1.2" vs "1.2.3").
 *
 * @param other The version string to compare against
 * @return A negative integer if this version is less than the other,
 *         zero if they are equal, or a positive integer if this version is greater
 */
fun String.compareVersions(other: String): Int {
    val parts1 = this.split(".").map { it.toIntOrNull() ?: 0 }
    val parts2 = other.split(".").map { it.toIntOrNull() ?: 0 }

    val maxLength = maxOf(parts1.size, parts2.size)

    for (i in 0 until maxLength) {
        val part1 = parts1.getOrNull(i) ?: 0
        val part2 = parts2.getOrNull(i) ?: 0

        when {
            part1 < part2 -> return -1
            part1 > part2 -> return 1
        }
    }

    return 0
}
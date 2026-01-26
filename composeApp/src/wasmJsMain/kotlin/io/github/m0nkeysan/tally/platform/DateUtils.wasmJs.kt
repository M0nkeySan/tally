package io.github.m0nkeysan.tally.platform

import kotlin.js.Date

actual fun getCurrentDateTimeString(): String {
    val date = Date()
    val day = date.getDate().toString().padStart(2, '0')
    val month = (date.getMonth() + 1).toString().padStart(2, '0')
    val year = date.getFullYear()
    val hours = date.getHours().toString().padStart(2, '0')
    val minutes = date.getMinutes().toString().padStart(2, '0')
    
    return "$day/$month/$year at $hours:$minutes"
}

actual fun formatTimestamp(timestamp: Long): String {
    return try {
        val date = Date(timestamp)
        val hours = date.getHours().toString().padStart(2, '0')
        val minutes = date.getMinutes().toString().padStart(2, '0')
        val seconds = date.getSeconds().toString().padStart(2, '0')
        "$hours:$minutes:$seconds"
    } catch (e: Exception) {
        "N/A"
    }
}

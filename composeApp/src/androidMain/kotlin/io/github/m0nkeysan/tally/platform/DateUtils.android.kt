package io.github.m0nkeysan.tally.platform

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

actual fun getCurrentDateTimeString(): String {
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'at' HH:mm")
    return "${current.format(formatter)}"
}

actual fun formatTimestamp(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val date = Date(timestamp)
        sdf.format(date)
    } catch (e: Exception) {
        "N/A"
    }
}

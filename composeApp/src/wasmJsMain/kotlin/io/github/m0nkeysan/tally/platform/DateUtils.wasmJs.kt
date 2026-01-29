package io.github.m0nkeysan.tally.platform

// Helper functions using js() that must be single expressions
private fun jsDateNow(): Double = js("Date.now()")
private fun jsDateGetDate(millis: Double): Int = js("new Date(millis).getDate()")
private fun jsDateGetMonth(millis: Double): Int = js("new Date(millis).getMonth()")
private fun jsDateGetFullYear(millis: Double): Int = js("new Date(millis).getFullYear()")
private fun jsDateGetHours(millis: Double): Int = js("new Date(millis).getHours()")
private fun jsDateGetMinutes(millis: Double): Int = js("new Date(millis).getMinutes()")
private fun jsDateGetSeconds(millis: Double): Int = js("new Date(millis).getSeconds()")

actual fun getCurrentDateTimeString(): String {
    val now = jsDateNow()
    val day = jsDateGetDate(now).toString().padStart(2, '0')
    val month = (jsDateGetMonth(now) + 1).toString().padStart(2, '0')
    val year = jsDateGetFullYear(now)
    val hours = jsDateGetHours(now).toString().padStart(2, '0')
    val minutes = jsDateGetMinutes(now).toString().padStart(2, '0')
    
    return "$day/$month/$year at $hours:$minutes"
}

actual fun formatTimestamp(timestamp: Long): String {
    return try {
        val millis = timestamp.toDouble()
        val hours = jsDateGetHours(millis).toString().padStart(2, '0')
        val minutes = jsDateGetMinutes(millis).toString().padStart(2, '0')
        val seconds = jsDateGetSeconds(millis).toString().padStart(2, '0')
        "$hours:$minutes:$seconds"
    } catch (e: Exception) {
        "N/A"
    }
}

package io.github.m0nkeysan.gamekeeper.platform

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.dateWithTimeIntervalSince1970

actual fun getCurrentDateTimeString(): String {
    val date = NSDate()
    val formatter = NSDateFormatter()
    formatter.dateFormat = "dd/MM/yyyy 'at' HH:mm"
    return formatter.stringFromDate(date)
}

actual fun formatTimestamp(timestamp: Long): String {
    return try {
        val date = NSDate.dateWithTimeIntervalSince1970(timestamp / 1000.0)

        val formatter = NSDateFormatter()
        formatter.dateFormat = "HH:mm:ss"

        formatter.stringFromDate(date)
    } catch (e: Exception) {
        "N/A"
    }
}
package io.github.m0nkeysan.gamekeeper.platform

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

actual fun getCurrentDateTimeString(): String {
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'at' HH:mm")
    return "${current.format(formatter)}"
}

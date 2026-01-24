package io.github.m0nkeysan.tally.core.utils

import kotlin.random.Random

/**
 * Multiplatform UUID generator.
 * Generates RFC 4122 version 4 compliant UUIDs.
 */
object UuidGenerator {
    /**
     * Generates a random UUID string.
     * Format: xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx
     * where x is any hexadecimal digit and y is one of 8, 9, A, or B.
     * 
     * Example: "550e8400-e29b-41d4-a716-446655440000"
     */
    fun randomUUID(): String {
        val random = Random(getCurrentTimeMillis() + Random.nextInt())
        
        // Generate 16 random bytes
        val bytes = ByteArray(16)
        random.nextBytes(bytes)
        
        // Set version to 4 (random UUID)
        bytes[6] = ((bytes[6].toInt() and 0x0F) or 0x40).toByte()
        
        // Set variant to RFC 4122
        bytes[8] = ((bytes[8].toInt() and 0x3F) or 0x80).toByte()
        
        // Convert to hex string with dashes
        return buildString {
            bytes.forEachIndexed { index, byte ->
                append(String.format("%02x", byte.toInt() and 0xFF))
                when (index) {
                    3, 5, 7, 9 -> append('-')
                }
            }
        }
    }
}

/**
 * Extension function for String.format that works on both platforms.
 * For hex formatting.
 */
private fun String.Companion.format(format: String, value: Int): String {
    return when (format) {
        "%02x" -> {
            val hex = value.toString(16)
            if (hex.length < 2) "0$hex" else hex
        }
        else -> value.toString()
    }
}

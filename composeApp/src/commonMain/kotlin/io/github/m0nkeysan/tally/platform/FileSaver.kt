package io.github.m0nkeysan.tally.platform

import androidx.compose.runtime.Composable

interface FileSaver {
    suspend fun saveJsonFile(filename: String, content: String): Result<Unit>

    suspend fun pickJsonFile(): Result<String>
}

@Composable
expect fun rememberFileSaver(): FileSaver

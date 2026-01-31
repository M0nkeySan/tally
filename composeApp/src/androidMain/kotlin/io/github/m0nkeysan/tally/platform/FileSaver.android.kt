package io.github.m0nkeysan.tally.platform

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withTimeout
import java.io.IOException

class AndroidFileSaver(private val activity: AppCompatActivity) : FileSaver {

    private val createDocumentChannel = Channel<Result<String>>(Channel.CONFLATED)
    private val openDocumentChannel = Channel<Result<String>>(Channel.CONFLATED)

    private val createDocumentLauncher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
            if (uri != null) {
                try {
                    createDocumentChannel.trySend(Result.success(uri.toString()))
                } catch (e: Exception) {
                    createDocumentChannel.trySend(Result.failure(e))
                }
            } else {
                createDocumentChannel.trySend(Result.failure(IOException("File creation cancelled")))
            }
        }

    private val openDocumentLauncher: ActivityResultLauncher<Array<String>> =
        activity.registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                try {
                    val content =
                        activity.contentResolver.openInputStream(uri)?.use { inputStream ->
                            inputStream.bufferedReader().use { it.readText() }
                        } ?: throw IOException("Could not read file")

                    openDocumentChannel.trySend(Result.success(content))
                } catch (e: Exception) {
                    openDocumentChannel.trySend(Result.failure(e))
                }
            } else {
                openDocumentChannel.trySend(Result.failure(IOException("File selection cancelled")))
            }
        }

    override suspend fun saveJsonFile(filename: String, content: String): Result<Unit> {
        return try {
            createDocumentLauncher.launch(filename)

            // Wait for result with timeout
            val result = withTimeout(60000) { // 60 second timeout
                createDocumentChannel.receive()
            }

            result.fold(
                onSuccess = { uriString ->
                    try {
                        val uri = android.net.Uri.parse(uriString)
                        activity.contentResolver.openOutputStream(uri)?.use { outputStream ->
                            outputStream.bufferedWriter().use { it.write(content) }
                        } ?: throw IOException("Could not open output stream")
                        Result.success(Unit)
                    } catch (e: Exception) {
                        Result.failure(e)
                    }
                },
                onFailure = { Result.failure(it) }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun pickJsonFile(): Result<String> {
        return try {
            openDocumentLauncher.launch(arrayOf("application/json", "text/plain"))

            // Wait for result with timeout
            withTimeout(60000) { // 60 second timeout
                openDocumentChannel.receive()
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

@Composable
actual fun rememberFileSaver(): FileSaver {
    val context = LocalContext.current

    val createDocumentChannel = remember { Channel<Result<Uri>>(Channel.CONFLATED) }
    val openDocumentChannel = remember { Channel<Result<String>>(Channel.CONFLATED) }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            createDocumentChannel.trySend(Result.success(uri))
        } else {
            createDocumentChannel.trySend(Result.failure(IOException("File creation cancelled")))
        }
    }

    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                val content = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.bufferedReader().use { it.readText() }
                } ?: throw IOException("Could not read file")

                openDocumentChannel.trySend(Result.success(content))
            } catch (e: Exception) {
                openDocumentChannel.trySend(Result.failure(e))
            }
        } else {
            openDocumentChannel.trySend(Result.failure(IOException("File selection cancelled")))
        }
    }

    return remember(createDocumentLauncher, openDocumentLauncher, context) {
        object : FileSaver {
            override suspend fun saveJsonFile(filename: String, content: String): Result<Unit> {
                return try {
                    createDocumentLauncher.launch(filename)

                    val uriResult = withTimeout(60000) {
                        createDocumentChannel.receive()
                    }

                    uriResult.fold(
                        onSuccess = { uri ->
                            try {
                                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                                    outputStream.bufferedWriter().use { writer ->
                                        writer.write(content)
                                    }
                                } ?: throw IOException("Could not open output stream")
                                Result.success(Unit)
                            } catch (e: Exception) {
                                Result.failure(e)
                            }
                        },
                        onFailure = { error ->
                            Result.failure(error)
                        }
                    )
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }

            override suspend fun pickJsonFile(): Result<String> {
                return try {
                    openDocumentLauncher.launch(arrayOf("application/json", "text/plain"))

                    withTimeout(60000) {
                        openDocumentChannel.receive()
                    }
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
        }
    }
}

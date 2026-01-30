package io.github.m0nkeysan.tally.platform

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withTimeout
import java.io.IOException

/**
 * Android implementation of FileSaver using Activity Result APIs
 */
class AndroidFileSaver(private val activity: AppCompatActivity) : FileSaver {
    
    private val createDocumentChannel = Channel<Result<String>>(Channel.CONFLATED)
    private val openDocumentChannel = Channel<Result<String>>(Channel.CONFLATED)
    
    private val createDocumentLauncher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
            if (uri != null) {
                try {
                    // Will be used to write content in saveJsonFile
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
                    val content = activity.contentResolver.openInputStream(uri)?.use { inputStream ->
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

/**
 * Provides an AndroidFileSaver instance
 * Note: This requires an Activity context
 */
actual fun getFileSaver(): FileSaver {
    // This will be called from a Composable context where we have access to the Activity
    // The Activity will be passed from the UI layer
    throw UnsupportedOperationException(
        "getFileSaver() cannot be called directly on Android. " +
        "Use getFileSaver(activity: AppCompatActivity) instead."
    )
}

/**
 * Android-specific helper to create FileSaver with Activity
 */
fun getFileSaver(activity: AppCompatActivity): FileSaver {
    return AndroidFileSaver(activity)
}

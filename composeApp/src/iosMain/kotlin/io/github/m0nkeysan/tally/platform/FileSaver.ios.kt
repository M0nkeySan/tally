package io.github.m0nkeysan.tally.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Foundation.writeToFile
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerMode
import platform.UIKit.UIDocumentPickerViewController
import platform.darwin.NSObject
import kotlin.coroutines.resume

/**
 * iOS implementation of FileSaver using UIDocumentPicker
 */
@OptIn(ExperimentalForeignApi::class)
class IOSFileSaver : FileSaver {
    
    override suspend fun saveJsonFile(filename: String, content: String): Result<Unit> {
        return try {
            // Save to temporary directory first
            val documentsPath = NSSearchPathForDirectoriesInDomains(
                NSDocumentDirectory,
                NSUserDomainMask,
                true
            ).firstOrNull() as? String ?: return Result.failure(Exception("Could not access documents directory"))
            
            val filePath = "$documentsPath/$filename"
            val nsString = NSString.create(string = content)
            val data = nsString.dataUsingEncoding(NSUTF8StringEncoding)
                ?: return Result.failure(Exception("Could not encode string"))
            
            // Write file
            val success = (data as NSData).writeToFile(filePath, atomically = true)
            if (!success) {
                return Result.failure(Exception("Could not write file"))
            }
            
            // Share file using UIActivityViewController
            suspendCancellableCoroutine { continuation ->
                try {
                    val url = platform.Foundation.NSURL.fileURLWithPath(filePath)
                    val activityViewController = UIActivityViewController(
                        activityItems = listOf(url),
                        applicationActivities = null
                    )
                    
                    val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
                    rootViewController?.presentViewController(
                        activityViewController,
                        animated = true,
                        completion = {
                            continuation.resume(Result.success(Unit))
                        }
                    )
                } catch (e: Exception) {
                    continuation.resume(Result.failure(e))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun pickJsonFile(): Result<String> {
        return suspendCancellableCoroutine { continuation ->
            try {
                val documentPicker = UIDocumentPickerViewController(
                    documentTypes = listOf("public.json", "public.text"),
                    inMode = UIDocumentPickerMode.UIDocumentPickerModeImport
                )
                
                val delegate = object : NSObject(), UIDocumentPickerDelegateProtocol {
                    override fun documentPicker(
                        controller: UIDocumentPickerViewController,
                        didPickDocumentAtURL: platform.Foundation.NSURL
                    ) {
                        try {
                            val content = NSString.create(
                                contentsOfURL = didPickDocumentAtURL,
                                encoding = NSUTF8StringEncoding,
                                error = null
                            ) as? String
                            
                            if (content != null) {
                                continuation.resume(Result.success(content))
                            } else {
                                continuation.resume(Result.failure(Exception("Could not read file content")))
                            }
                        } catch (e: Exception) {
                            continuation.resume(Result.failure(e))
                        }
                    }
                    
                    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
                        continuation.resume(Result.failure(Exception("File selection cancelled")))
                    }
                }
                
                documentPicker.delegate = delegate
                
                val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
                rootViewController?.presentViewController(
                    documentPicker,
                    animated = true,
                    completion = null
                )
            } catch (e: Exception) {
                continuation.resume(Result.failure(e))
            }
        }
    }
}

/**
 * Provides an IOSFileSaver instance
 */
actual fun getFileSaver(): FileSaver {
    return IOSFileSaver()
}

/**
 * Remember a FileSaver instance for iOS
 */
@Composable
actual fun rememberFileSaver(): FileSaver {
    return remember { IOSFileSaver() }
}

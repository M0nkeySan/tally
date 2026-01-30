package io.github.m0nkeysan.tally.platform

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * WASM implementation of FileSaver using JavaScript File API
 */
class WasmFileSaver : FileSaver {
    
    override suspend fun saveJsonFile(filename: String, content: String): Result<Unit> {
        return try {
            downloadFileJs(filename, content)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun pickJsonFile(): Result<String> {
        return suspendCancellableCoroutine { continuation ->
            pickFileJs { success, data, error ->
                if (success) {
                    continuation.resume(Result.success(data ?: ""))
                } else {
                    continuation.resume(Result.failure(Exception(error ?: "File selection failed")))
                }
            }
        }
    }
}

/**
 * Provides a WasmFileSaver instance
 */
actual fun getFileSaver(): FileSaver {
    return WasmFileSaver()
}

/**
 * Download file using JavaScript Blob API
 */
@JsFun("(filename, content) => { const blob = new Blob([content], { type: 'application/json' }); const url = URL.createObjectURL(blob); const a = document.createElement('a'); a.href = url; a.download = filename; document.body.appendChild(a); a.click(); document.body.removeChild(a); URL.revokeObjectURL(url); }")
private external fun downloadFileJs(filename: String, content: String)

/**
 * Pick and read file using JavaScript FileReader API
 */
@JsFun("""
(callback) => {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = 'application/json,.json,text/plain,.txt';
    
    input.onchange = (e) => {
        const file = e.target.files[0];
        if (!file) {
            callback(false, null, 'No file selected');
            return;
        }
        
        const reader = new FileReader();
        reader.onload = (event) => {
            try {
                const content = event.target.result;
                callback(true, content, null);
            } catch (error) {
                callback(false, null, error.message);
            }
        };
        reader.onerror = () => {
            callback(false, null, 'Failed to read file');
        };
        reader.readAsText(file);
    };
    
    input.click();
}
""")
private external fun pickFileJs(callback: (Boolean, String?, String?) -> Unit)

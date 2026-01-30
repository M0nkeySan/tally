package io.github.m0nkeysan.tally.platform

/**
 * Platform-specific file operations for export/import
 */
interface FileSaver {
    /**
     * Save a JSON string to a file
     * @param filename Suggested filename for the file
     * @param content JSON content to save
     * @return Result with Unit on success or error on failure
     */
    suspend fun saveJsonFile(filename: String, content: String): Result<Unit>
    
    /**
     * Pick and read a JSON file
     * @return Result with file content on success or error on failure
     */
    suspend fun pickJsonFile(): Result<String>
}

/**
 * Composable function to get a FileSaver instance
 * Platform-specific implementations will provide the actual file saver
 */
expect fun getFileSaver(): FileSaver

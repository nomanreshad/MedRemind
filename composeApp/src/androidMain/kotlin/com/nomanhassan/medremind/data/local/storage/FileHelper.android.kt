package com.nomanhassan.medremind.data.local.storage

import android.content.Context
import android.util.Log
import com.mohamedrejeb.calf.io.KmpFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlinx.io.IOException

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class FileHelper(
    private val context: Context
) {
    
    actual suspend fun KmpFile.readBytes(): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                ensureActive()
                
                val uri = this@readBytes.uri
                
                context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            } catch (e: IOException) {
                Log.e(TAG, "IOException reading image", e)
                null
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error reading image", e)
                null
            }
        }
    }
    
    companion object {
        private const val TAG = "FileHelper"
    }
}
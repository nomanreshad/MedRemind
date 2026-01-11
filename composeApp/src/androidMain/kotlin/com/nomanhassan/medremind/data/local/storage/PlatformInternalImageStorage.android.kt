package com.nomanhassan.medremind.data.local.storage

import android.content.Context
import android.util.Log
import com.nomanhassan.medremind.core.domain.DataError
import com.nomanhassan.medremind.core.domain.Result
import com.nomanhassan.medremind.data.local.storage.InternalImageStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import java.io.File

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class PlatformInternalImageStorage(
    private val context: Context
) : InternalImageStorage {
    
    actual override suspend fun saveImage(
        bytes: ByteArray,
        fileName: String
    ): Result<String, DataError.Local> {
        return withContext(Dispatchers.IO) {
            try {
                ensureActive()
                
                val directory = File(context.filesDir, "prescription_medication_saved_images")
                
                if (!directory.exists() && !directory.mkdirs()) {
                    throw IOException("Failed to create directory: ${directory.absolutePath}")
                }
                
                val file = File(directory, fileName)
                file.writeBytes(bytes)
                
                Result.Success(file.absolutePath)
            } catch (e: FileNotFoundException) {
                Log.e(TAG, "FileNotFoundException saving image", e)
                Result.Error(DataError.Local.UNKNOWN)
            } catch (e: IOException) {
                Log.e(TAG, "IOException saving image", e)
                Result.Error(DataError.Local.DISK_FULL)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error saving image", e)
                Result.Error(DataError.Local.UNKNOWN)
            }
        }
    }

    actual override suspend fun getImage(filePath: String): Result<ByteArray?, DataError.Local> {
        return withContext(Dispatchers.IO) {
            try {
                ensureActive()
                
                val file = File(filePath)
                
                if (!file.exists()) {
                    return@withContext Result.Success(null)
                }
                
                Result.Success(file.readBytes())
            } catch (e: SecurityException) {
                Log.e(TAG, "SecurityException reading image", e)
                Result.Error(DataError.Local.UNKNOWN)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error reading image", e)
                Result.Error(DataError.Local.UNKNOWN)
            }
        }
    }
    
    actual override suspend fun deleteImage(filePath: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                ensureActive()
                
                val file = File(filePath)
                
                if (!file.exists()) {
                    return@withContext false
                }
                
                file.delete()
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                Log.e(TAG, "IOException deleting image", e)
                false
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error deleting image", e)
                false
            }
        }
    }

    actual override suspend fun exists(filePath: String): Boolean {
        return withContext(Dispatchers.IO) {
            File(filePath).exists()
        }
    }

    companion object {
        private const val TAG = "InternalImageStorage"
    }
}
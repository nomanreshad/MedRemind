@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)

package com.nomanhassan.medremind.data.local.storage

import com.nomanhassan.medremind.core.domain.DataError
import com.nomanhassan.medremind.core.domain.Result
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.dataWithBytes
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.writeToFile
import platform.posix.memcpy
import kotlin.coroutines.cancellation.CancellationException

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class PlatformInternalImageStorage : InternalImageStorage {
    
    private val fileManager = NSFileManager.defaultManager

    actual override suspend fun saveImage(
        bytes: ByteArray,
        fileName: String
    ): Result<String, DataError.Local> {
        return withContext(Dispatchers.IO) {
            try {
                ensureActive()
                
                val documentsDirectory = fileManager.URLsForDirectory(
                    directory = NSDocumentDirectory,
                    inDomains = NSUserDomainMask
                ).firstOrNull() as? NSURL ?: throw IOException("Could not access documents directory")
                
                val directoryURL = documentsDirectory.URLByAppendingPathComponent(
                    pathComponent = "prescription_medication_saved_images",
                    isDirectory = true
                ) ?: throw IOException("Could not create directory URL")
                
                val directoryPath = directoryURL.path
                    ?: throw IOException("Could not get directory path")
                
                if (!fileManager.fileExistsAtPath(directoryPath)) {
                    fileManager.createDirectoryAtPath(
                        path = directoryPath,
                        withIntermediateDirectories = true,
                        attributes = null,
                        error = null
                    )
                }
                
                val fileURL = directoryURL.URLByAppendingPathComponent(fileName)
                    ?: throw IOException("Could not create file URL")
                
                val filePath = fileURL.path
                    ?: throw IOException("Could not get file path")
                
                val nsData = bytes.toNSData()
                
                val success = nsData.writeToFile(
                    path = filePath,
                    atomically = true
                )
                
                if (!success) {
                    throw IOException("Failed to write file")
                }
                
                Result.Success(filePath)
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                println("IOException saving image: ${e.message}")
                Result.Error(DataError.Local.DISK_FULL)
            } catch (e: Exception) {
                println("Unexpected error deleting image: ${e.message}")
                Result.Error(DataError.Local.UNKNOWN)
            }
        }
    }

    actual override suspend fun getImage(filePath: String): Result<ByteArray?, DataError.Local> {
        return withContext(Dispatchers.IO) {
            try {
                ensureActive()
                
                if (!fileManager.fileExistsAtPath(filePath)) {
                    return@withContext Result.Success(null)
                }
                
                val nsData = NSData.dataWithContentsOfFile(filePath)
                    ?: return@withContext Result.Success(null)
                
                val byteArray = nsData.toByteArray()
                
                Result.Success(byteArray)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                println("Unexpected error deleting image: ${e.message}")
                Result.Error(DataError.Local.UNKNOWN)
            }
        }
    }

    actual override suspend fun deleteImage(filePath: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                ensureActive()
                
                if (!fileManager.fileExistsAtPath(filePath)) {
                    return@withContext false
                }
                
                fileManager.removeItemAtPath(
                    path = filePath,
                    error = null
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                println("IOException deleting image: ${e.message}")
                false
            } catch (e: Exception) {
                println("Unexpected error deleting image: ${e.message}")
                false
            }
        }
    }

    actual override suspend fun exists(filePath: String): Boolean {
        return withContext(Dispatchers.IO) {
            fileManager.fileExistsAtPath(filePath)
        }
    }
}

fun ByteArray.toNSData(): NSData = usePinned {
    NSData.dataWithBytes(
        bytes = it.addressOf(0),
        length = this.size.toULong()
    )
}

fun NSData.toByteArray(): ByteArray {
    return ByteArray(this.length.toInt()).apply {
        usePinned {
            memcpy(
                it.addressOf(0),
                this@toByteArray.bytes,
                this@toByteArray.length
            )
        }
    }
}
package com.nomanhassan.medremind.data.local.storage

import com.mohamedrejeb.calf.io.KmpFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfURL

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class FileHelper {
    
    actual suspend fun KmpFile.readBytes(): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                ensureActive()
                
                val nsUrl = this@readBytes.url
                
                val nsData = NSData.dataWithContentsOfURL(nsUrl) ?: return@withContext null
                
                nsData.toByteArray()
            } catch (e: IOException) {
                println("IOException reading image: ${e.message}")
                null
            } catch (e: Exception) {
                println("Exception reading image: ${e.message}")
                null
            }
        }
    }
}
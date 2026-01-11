package com.nomanhassan.medremind.data.local.storage

import com.nomanhassan.medremind.core.domain.DataError
import com.nomanhassan.medremind.core.domain.Result

interface InternalImageStorage {
    
    suspend fun saveImage(
        bytes: ByteArray,
        fileName: String
    ): Result<String, DataError.Local>
    
    suspend fun getImage(filePath: String): Result<ByteArray?, DataError.Local>
    
    suspend fun deleteImage(filePath: String): Boolean
    
    suspend fun exists(filePath: String): Boolean
}
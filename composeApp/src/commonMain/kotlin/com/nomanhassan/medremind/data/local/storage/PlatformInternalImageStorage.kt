package com.nomanhassan.medremind.data.local.storage

import com.nomanhassan.medremind.core.domain.DataError
import com.nomanhassan.medremind.core.domain.Result

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class PlatformInternalImageStorage: InternalImageStorage {

    override suspend fun saveImage(
        bytes: ByteArray,
        fileName: String
    ): Result<String, DataError.Local>

    override suspend fun getImage(filePath: String): Result<ByteArray?, DataError.Local>

    override suspend fun deleteImage(filePath: String): Boolean

    override suspend fun exists(filePath: String): Boolean
}
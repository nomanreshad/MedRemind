package com.nomanhassan.medremind.data.local.storage

import com.mohamedrejeb.calf.io.KmpFile

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class FileHelper {
    suspend fun KmpFile.readBytes(): ByteArray?
}
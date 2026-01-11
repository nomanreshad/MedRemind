@file:OptIn(ExperimentalForeignApi::class)

package com.nomanhassan.medremind.data.local.database

import androidx.room.Room
import androidx.room.RoomDatabase
import com.nomanhassan.medremind.data.local.database.MedRemindDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<MedRemindDatabase> {
        val dbFile = documentDirectory() + "/${MedRemindDatabase.DB_NAME}"
        return Room.databaseBuilder<MedRemindDatabase>(
            name = dbFile
        )
    }
    
    private fun documentDirectory(): String {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        )
        return requireNotNull(documentDirectory?.path)
    }
}
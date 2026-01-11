package com.nomanhassan.medremind.data.local.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nomanhassan.medremind.data.local.database.MedRemindDatabase

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DatabaseFactory(
    private val context: Context
) {
    actual fun create(): RoomDatabase.Builder<MedRemindDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(MedRemindDatabase.DB_NAME)
        
        return Room.databaseBuilder(
            context = appContext,
            name = dbFile.absolutePath
        )
    }
}
package com.nomanhassan.medremind.data.local.database

import androidx.room.RoomDatabase

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class DatabaseFactory {
    fun create(): RoomDatabase.Builder<MedRemindDatabase>
}
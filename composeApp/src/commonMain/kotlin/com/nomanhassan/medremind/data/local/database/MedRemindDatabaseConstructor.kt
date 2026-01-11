package com.nomanhassan.medremind.data.local.database

import androidx.room.RoomDatabaseConstructor

@Suppress("NO_ACTUAL_FOR_EXPECT", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object MedRemindDatabaseConstructor: RoomDatabaseConstructor<MedRemindDatabase> {
    override fun initialize(): MedRemindDatabase
}
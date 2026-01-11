package com.nomanhassan.medremind.data.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [MedicationEntity::class],
    version = 1
)
@TypeConverters(MedicationConverters::class)
@ConstructedBy(MedRemindDatabaseConstructor::class)
abstract class MedRemindDatabase : RoomDatabase() {
    abstract val medicationDao: MedicationDao

    companion object {
        const val DB_NAME = "med_remind.db"
    }
}
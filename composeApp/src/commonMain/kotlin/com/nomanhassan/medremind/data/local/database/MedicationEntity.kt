package com.nomanhassan.medremind.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nomanhassan.medremind.core.enums.Frequency
import com.nomanhassan.medremind.core.enums.MedicationType

@Entity(tableName = "medications")
data class MedicationEntity(
    @PrimaryKey(autoGenerate = true)
    val medicationId: Int = 0,
    val medicineName: String,
    val dosageStrength: String?,
    val medicationType: MedicationType?,
    val frequency: Frequency,
    val timesEpochMillis: List<Long?> = emptyList(),
    val startDate: Long,
    val endDate: Long?,
    val notes: String?,
    val hospitalName: String?,
    val doctorName: String?,
    val hospitalAddress: String?,
    val prescriptionImagePath: String?,
    val medicationImagePath: String?,
    val isActive: Boolean = true
)
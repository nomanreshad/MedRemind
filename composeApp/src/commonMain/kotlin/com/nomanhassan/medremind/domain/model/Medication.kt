package com.nomanhassan.medremind.domain.model

data class Medication(
    val medicationId: Int = 0,
    val medicineName: String,
    val dosageStrength: String?,
    val medicationType: String?,
    val frequency: String,
    val formattedTimes: List<String?>,
    val formattedStartDate: String,
    val formattedEndDate: String?,
    val notes: String?,
    val hospitalName: String?,
    val doctorName: String?,
    val hospitalAddress: String?,
    val prescriptionImagePath: String?,
    val medicationImagePath: String?,
    val isActive: Boolean
)
package com.nomanhassan.medremind.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class AiMedicationItem(
    val medicineName: String? = null,
    val dosageStrength: String? = null,
    val medicationType: String? = null,
    val frequency: String? = null,
    val notes: String? = null
)
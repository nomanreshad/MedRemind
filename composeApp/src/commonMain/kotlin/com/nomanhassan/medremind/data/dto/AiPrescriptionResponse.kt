package com.nomanhassan.medremind.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class AiPrescriptionResponse(
    val imageAnalysisResult: String,
    val doctorName: String? = null,
    val hospitalName: String? = null,
    val hospitalAddress: String? = null,
    val medications: List<AiMedicationItem> = emptyList()
)
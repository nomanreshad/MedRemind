package com.nomanhassan.medremind.data.mapper

import com.nomanhassan.medremind.core.enums.AiImageAnalysisResult
import com.nomanhassan.medremind.core.enums.Frequency
import com.nomanhassan.medremind.core.enums.MedicationType
import com.nomanhassan.medremind.data.dto.AiPrescriptionResponse
import com.nomanhassan.medremind.domain.model.ImageExtractionResult
import com.nomanhassan.medremind.presentation.add_edit_medication.MedicationFormState

fun AiPrescriptionResponse.toExtractionResult(
    prescriptionImagePath: String? = null
): ImageExtractionResult {
    val analysisResult = AiImageAnalysisResult.fromName(this.imageAnalysisResult)
        ?: AiImageAnalysisResult.NOT_A_PRESCRIPTION

    val doctorName = this.doctorName.orEmpty()
    val hospitalName = this.hospitalName.orEmpty()
    val hospitalAddress = this.hospitalAddress.orEmpty()

    if (analysisResult != AiImageAnalysisResult.PRESCRIPTION_FOUND) {
        val emptyForm = MedicationFormState(
            doctorName = doctorName,
            hospitalName = hospitalName,
            hospitalAddress = hospitalAddress,
            prescriptionImagePath = prescriptionImagePath
        )
        return ImageExtractionResult(
            analysisResult = analysisResult,
            forms = listOf(emptyForm)
        )
    }

    if (medications.isEmpty()) {
        val emptyForm = MedicationFormState(
            doctorName = doctorName,
            hospitalName = hospitalName,
            hospitalAddress = hospitalAddress,
            prescriptionImagePath = prescriptionImagePath
        )
        return ImageExtractionResult(
            analysisResult = analysisResult,
            forms = listOf(emptyForm)
        )
    }

    val forms = medications.map { item ->
        val medicineName = item.medicineName.orEmpty()
        val dosageStrength = item.dosageStrength.orEmpty()
        val notes = item.notes.orEmpty()
        val medicationType = matchMedicationType(item.medicationType)
        val frequency = matchFrequency(item.frequency)
        val times = frequency?.let {
            when (it) {
                Frequency.ONCE_DAILY,
                Frequency.EVERY_FOUR_HOURS,
                Frequency.EVERY_SIX_HOURS,
                Frequency.WEEKLY,
                Frequency.MONTHLY -> listOf(null)
                Frequency.TWICE_DAILY -> listOf(null, null)
                Frequency.THRICE_DAILY -> listOf(null, null, null)
                Frequency.AS_NEEDED -> emptyList()
            }
        } ?: emptyList()

        MedicationFormState(
            medicineName = medicineName,
            dosageStrength = dosageStrength,
            notes = notes,
            doctorName = doctorName,
            hospitalName = hospitalName,
            hospitalAddress = hospitalAddress,
            prescriptionImagePath = prescriptionImagePath,
            medicationType = medicationType,
            frequency = frequency,
            times = times
        )
    }

    return ImageExtractionResult(
        analysisResult = analysisResult,
        forms = forms
    )
}

private fun matchMedicationType(raw: String?): MedicationType? {
    if (raw.isNullOrBlank()) return null
    return MedicationType.fromName(raw)
}

private fun matchFrequency(raw: String?): Frequency? {
    if (raw.isNullOrBlank()) return null
    return Frequency.fromName(raw)
}
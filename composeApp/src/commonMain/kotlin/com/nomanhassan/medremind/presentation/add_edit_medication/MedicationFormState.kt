package com.nomanhassan.medremind.presentation.add_edit_medication

import com.nomanhassan.medremind.core.enums.Frequency
import com.nomanhassan.medremind.core.enums.MedicationType
import com.nomanhassan.medremind.core.presentation.UiText
import com.nomanhassan.medremind.core.enums.ImageType

data class MedicationFormState(
    val id: Int? = null,

    // Medication Information
    val medicineName: String = "",
    val medicineNameError: UiText? = null,

    val dosageStrength: String = "",

    val medicationType: MedicationType? = null,
    val medicationTypeError: UiText? = null,

    val frequency: Frequency? = null,
    val frequencyError: UiText? = null,

    val times: List<String?> = emptyList(),
    val timesError: UiText? = null,

    val startDate: String = "",
    val startDateError: UiText? = null,

    val endDate: String? = null,
    val endDateError: UiText? = null,

    val notes: String = "",

    // Other Information
    val hospitalName: String = "",
    val doctorName: String = "",
    val hospitalAddress: String = "",

    val imageType: ImageType = ImageType.PRESCRIPTION,
    val prescriptionImagePath: String? = null,
    val medicationImagePath: String? = null
)
package com.nomanhassan.medremind.presentation.medication_list

import com.nomanhassan.medremind.core.presentation.UiText
import com.nomanhassan.medremind.domain.model.Medication

data class MedicationItem(
    val medication: Medication,
    val formattedFrequency: UiText = UiText.DynamicString(""),
    val formattedType: UiText? = UiText.DynamicString(""),
    val nextDoseTime: String? = null,
    val nextDoseIn: UiText? = null,
)
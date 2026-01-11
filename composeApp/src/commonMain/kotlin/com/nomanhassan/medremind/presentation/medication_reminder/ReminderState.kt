package com.nomanhassan.medremind.presentation.medication_reminder

import com.nomanhassan.medremind.core.presentation.UiText
import com.nomanhassan.medremind.domain.model.Medication

data class ReminderState(
    val isLoading: Boolean = true,
    val reminderTime: String = "",
    val dueMedications: List<Medication> = emptyList(),
    val isAcknowledged: Boolean = false,
    val errorMessage: UiText? = null
)

data class ReminderMedicationItem(
    val id: Int,
    val name: String,
    val dosage: String?,
    val medicationImagePath: String?
)
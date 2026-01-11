package com.nomanhassan.medremind.presentation.medication_list

sealed interface MedicationListEvent {
    data class OnMedicationDeleteSuccess(val message: String): MedicationListEvent
    data class OnMedicationDeleteError(val message: String): MedicationListEvent
    data class OnShowUndoSnackbar(val message: String): MedicationListEvent
}
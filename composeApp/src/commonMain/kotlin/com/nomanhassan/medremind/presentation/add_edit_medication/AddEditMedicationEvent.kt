package com.nomanhassan.medremind.presentation.add_edit_medication

sealed interface AddEditMedicationEvent {
    data object OnSaveSuccess: AddEditMedicationEvent
    data class OnSaveError(val message: String): AddEditMedicationEvent

    data class OnImageSaveError(val message: String): AddEditMedicationEvent
    data class OnImageDeleteError(val message: String): AddEditMedicationEvent
    
    data class PrescriptionFound(val message: String): AddEditMedicationEvent
    data class PrescriptionBlurryOrUnclear(val message: String): AddEditMedicationEvent
    data class NotAPrescription(val message: String): AddEditMedicationEvent
    data class OnImageAnalysisError(val message: String): AddEditMedicationEvent
    
    data object RequestNotificationPermission: AddEditMedicationEvent
}
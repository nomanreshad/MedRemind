package com.nomanhassan.medremind.presentation.medication_detail

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.nomanhassan.medremind.core.enums.ImageType

sealed interface MedicationDetailAction {
    data object OnClickGoBack : MedicationDetailAction

    data object OnToggleOptionsMenu : MedicationDetailAction
    data class OnEditMedicationClick(val id: Int) : MedicationDetailAction
    data object OnDeleteMedicationClick : MedicationDetailAction
    data object ConfirmDeleteMedication : MedicationDetailAction
    data object DismissDeleteMedicationDialog : MedicationDetailAction

    data class OnTabSelected(val index: Int) : MedicationDetailAction

    data class OnImageTypeSelected(val imageType: ImageType) : MedicationDetailAction
    
    data class OnImageTransformChanged(val pan: Offset, val zoom: Float, val containerSize: IntSize): MedicationDetailAction
    
//    data object OnUploadPrescriptionImageClick : MedicationDetailAction
//    class OnPrescriptionImageSelected(val imageBytes: ByteArray) : MedicationDetailAction
//    data object OnDeletePrescriptionImageClick : MedicationDetailAction
//    data object ConfirmDeletePrescriptionImage : MedicationDetailAction
//    data object DismissDeletePrescriptionDialog : MedicationDetailAction
    
//    data object OnUploadMedicationImageClick : MedicationDetailAction
//    class OnMedicationImageSelected(val imageBytes: ByteArray) : MedicationDetailAction
//    data object OnDeleteMedicationImageClick : MedicationDetailAction
//    data object ConfirmDeleteMedicationImage : MedicationDetailAction
//    data object DismissDeleteMedicationImageDialog : MedicationDetailAction
}
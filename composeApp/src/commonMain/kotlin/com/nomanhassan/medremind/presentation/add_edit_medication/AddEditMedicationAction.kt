package com.nomanhassan.medremind.presentation.add_edit_medication

import com.mohamedrejeb.calf.io.KmpFile
import com.nomanhassan.medremind.core.enums.Frequency
import com.nomanhassan.medremind.core.enums.MedicationType
import com.nomanhassan.medremind.core.enums.ImageType

sealed interface AddEditMedicationAction {
    // Carousel Actions
    data object OnNextMedication : AddEditMedicationAction
    data object OnPreviousMedication : AddEditMedicationAction
    data object OnAddAnotherMedication : AddEditMedicationAction
    data class OnRemoveMedication(val index: Int) : AddEditMedicationAction

    // Field Changes
    data class OnMedicineNameChanged(val name: String) : AddEditMedicationAction
    data class OnDosageStrengthChanged(val strength: String) : AddEditMedicationAction
    data class OnMedicationTypeChanged(val type: MedicationType) : AddEditMedicationAction
    data class OnFrequencyChanged(val frequency: Frequency) : AddEditMedicationAction
    
    data class OnTimeSelected(val index: Int, val time: Long) : AddEditMedicationAction
    data class OnStartDateSelected(val date: Long) : AddEditMedicationAction
    data class OnEndDateSelected(val date: Long) : AddEditMedicationAction
    data class OnNotesChanged(val notes: String) : AddEditMedicationAction

    // Other Information
    data class OnHospitalNameChanged(val name: String) : AddEditMedicationAction
    data class OnDoctorNameChanged(val name: String) : AddEditMedicationAction
    data class OnHospitalAddressChanged(val address: String) : AddEditMedicationAction

    // Image Actions
    data class OnUploadImageClick(val imageType: ImageType) : AddEditMedicationAction
    data class OnImagePicked(val kmpFile: KmpFile, val imageType: ImageType) : AddEditMedicationAction
    data class OnDeleteImageClick(val imageType: ImageType) : AddEditMedicationAction
    
    // AI Prescription Scan
    data class OnPrescriptionImagePicked(val kmpFile: KmpFile) : AddEditMedicationAction

    // Picker Actions
    data class OnShowTimePicker(val index: Int) : AddEditMedicationAction
    data object OnDismissTimePicker : AddEditMedicationAction
    
    data object OnShowStartDatePicker : AddEditMedicationAction
    data object OnDismissStartDatePicker : AddEditMedicationAction

    data object OnShowEndDatePicker : AddEditMedicationAction
    data object OnDismissEndDatePicker : AddEditMedicationAction

    // Dropdown Actions
    data object OnShowMedicationTypeDropdown : AddEditMedicationAction
    data object OnShowFrequencyDropdown : AddEditMedicationAction
    data object OnHideDropdown : AddEditMedicationAction

    // Main Actions
    data object OnSaveClick : AddEditMedicationAction
    data object OnGoBack : AddEditMedicationAction
    
    // Permissions Result & Dialog Actions
    data class OnPermissionResult(val isGranted: Boolean): AddEditMedicationAction
    data object OnShowRationaleDialog : AddEditMedicationAction
    data object OnConfirmSaveWithoutNotifications : AddEditMedicationAction
    data object OnDismissSaveWithoutNotifications : AddEditMedicationAction
    
    data object OnConfirmLeaveWithoutSaving: AddEditMedicationAction
    data object OnDismissLeaveWithoutSaving: AddEditMedicationAction
}
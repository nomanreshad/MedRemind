package com.nomanhassan.medremind.presentation.add_edit_medication

import com.nomanhassan.medremind.core.presentation.UiText

data class AddEditMedicationState(
    val isLoading: Boolean = false,
    val medicationForms: List<MedicationFormState> = listOf(MedicationFormState()),
    val currentMedicationIndex: Int = 0,
    val isSaving: Boolean = false,

    // Picker Visibility States
    val timePickerIndexToShow: Int? = null,
    val isStartDatePickerVisible: Boolean = false,
    val isEndDatePickerVisible: Boolean = false,
    val isPrescriptionImagePickerVisible: Boolean = false,
    val isMedicationImagePickerVisible: Boolean = false,
    
    val shouldShowRationale: Boolean = false,
    val isSavePendingPermission: Boolean = false,

    val isAnalyzingPrescription: Boolean = false,
    val isDeletingImage: Boolean = false,

    // Dropdown Visibility State
    val activeDropdownMenu: ActiveDropdownMenu = ActiveDropdownMenu.NONE,

    val errorMessage: UiText? = null,

    val originalForms: List<MedicationFormState> = emptyList(),
    val isLeavingWithoutSaving: Boolean = false,
    val hasLeftForm: Boolean = false,
) {
    val currentMedicationForm: MedicationFormState?
        get() = medicationForms.getOrNull(currentMedicationIndex)
}

//sealed interface ActiveDropdownMenu {
//    data object None : ActiveDropdownMenu
//    data object MedicationType : ActiveDropdownMenu
//    data object Frequency : ActiveDropdownMenu
//}

enum class ActiveDropdownMenu {
    NONE,
    MEDICATION_TYPE,
    FREQUENCY
}
package com.nomanhassan.medremind.presentation.medication_list

import com.nomanhassan.medremind.core.enums.TimeSlot
import com.nomanhassan.medremind.domain.model.Medication

sealed interface MedicationListAction {
    // Search and Settings
    data class OnSearchQueryChange(val query: String) : MedicationListAction
    data object OnClearSearch : MedicationListAction
    data object OnToggleSearch : MedicationListAction
    data object OnClickSettings : MedicationListAction

    // Medication Card
    data class OnMedicationReminderToggled(
        val medication: Medication, val isEnabled: Boolean
    ) : MedicationListAction
    data class OnClickMedicationItem(val id: Int): MedicationListAction

    // Filtering
    data object OnToggleFilterMenu : MedicationListAction
    data class OnFilterOptionSelected(val timeSlot: TimeSlot) : MedicationListAction

    // --- FAB Menu Actions for Android ---
    data object OnToggleFabMenu : MedicationListAction

    data object OnClickAddMedication : MedicationListAction

    // --- Multiselect Actions ---
    data class OnToggleSelection(val id: Int) : MedicationListAction
    data object OnToggleSelectAll : MedicationListAction
    data object OnCancelSelection : MedicationListAction
    data object OnClickDelete : MedicationListAction
    
    data object OnConfirmDelete : MedicationListAction
    data object OnDismissDeleteDialog : MedicationListAction

    data object OnUndoDelete : MedicationListAction
    data class OnDeleteIndividual(val medication: Medication): MedicationListAction
}
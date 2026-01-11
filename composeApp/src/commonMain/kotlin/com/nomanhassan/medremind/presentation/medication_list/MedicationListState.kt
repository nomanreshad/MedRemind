package com.nomanhassan.medremind.presentation.medication_list

import com.nomanhassan.medremind.core.enums.TimeSlot
import com.nomanhassan.medremind.core.presentation.UiText

data class MedicationListState(
    val medicationItems: List<MedicationItem> = emptyList(),
    val searchQuery: String = "",
    val isSearchBarVisible: Boolean = false,
    val isFilterMenuVisible: Boolean = false,
    val selectedTimeSlot: TimeSlot = TimeSlot.ALL,
    val isFabMenuExpanded: Boolean = false,
    val isAddMedicationMenuVisible: Boolean = false,
    val isLoading: Boolean = true,
    val isUploadingImage: Boolean = false,
    val eventMessage: UiText? = null,
    val dayOfWeek: UiText? = null,
    val dayOfMonth: String = "",
    val month: UiText? = null,
    val year: String = "",
    val isSelectionMode: Boolean = false,
    val isDeletingMedications: Boolean = false,
    val selectedMedicationIds: Set<Int> = emptySet(),
    val showDeleteMedicationDialog: Boolean = false,
    val temporarilyDeletedIds: Set<Int> = emptySet()
)
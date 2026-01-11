package com.nomanhassan.medremind.presentation.medication_detail

import androidx.compose.ui.geometry.Offset
import com.nomanhassan.medremind.core.presentation.UiText
import com.nomanhassan.medremind.domain.model.Medication
import com.nomanhassan.medremind.core.enums.ImageType
import com.nomanhassan.medremind.presentation.medication_list.MedicationItem

data class MedicationDetailState(
    val isLoading: Boolean = false,
    val medicationItem: MedicationItem? = null,
    val isOptionsMenuVisible: Boolean = false,
    val selectedTabIndex: Int = 0,
    val selectedImageType: ImageType = ImageType.PRESCRIPTION,
    val imageScale: Float = 1f,
    val imageOffset: Offset = Offset.Zero,
    val showDeleteMedicationDialog: Boolean = false,
    val isDeletingMedication: Boolean = false,
    val isMedicationDeletedSuccessfully: Boolean = false,
    val isDeletingImage: Boolean = false,
    val errorMessage: UiText? = null
)
package com.nomanhassan.medremind.presentation.medication_detail

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nomanhassan.medremind.app.navigation.Route
import com.nomanhassan.medremind.core.enums.Frequency
import com.nomanhassan.medremind.core.enums.MedicationType
import com.nomanhassan.medremind.core.presentation.UiText
import com.nomanhassan.medremind.core.presentation.toUiText
import com.nomanhassan.medremind.data.local.storage.InternalImageStorage
import com.nomanhassan.medremind.domain.repository.MedicationRepository
import com.nomanhassan.medremind.presentation.medication_list.MedicationItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.failed_to_delete_medication
import medremind.composeapp.generated.resources.failed_to_load_medication

class MedicationDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val medicationRepository: MedicationRepository,
    private val internalImageStorage: InternalImageStorage
) : ViewModel() {
    
    private val medicationId = savedStateHandle.toRoute<Route.MedicationDetail>().id
    
    private val _state = MutableStateFlow(MedicationDetailState())
    
    val state = _state
        .onStart {
            loadMedication(medicationId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value
        )

    fun onAction(action: MedicationDetailAction) {
        when (action) {
            MedicationDetailAction.OnToggleOptionsMenu -> _state.update { it.copy(isOptionsMenuVisible = !it.isOptionsMenuVisible) }
            
            MedicationDetailAction.OnDeleteMedicationClick -> _state.update { it.copy(showDeleteMedicationDialog = true) }
            MedicationDetailAction.ConfirmDeleteMedication -> deleteMedication()
            MedicationDetailAction.DismissDeleteMedicationDialog -> _state.update { it.copy(showDeleteMedicationDialog = false) }

            is MedicationDetailAction.OnTabSelected -> _state.update { it.copy(selectedTabIndex = action.index) }

            is MedicationDetailAction.OnImageTypeSelected -> _state.update { it.copy(
                selectedImageType = action.imageType,
                imageScale = 1f,
                imageOffset = Offset.Zero
            ) }
            is MedicationDetailAction.OnImageTransformChanged -> imageTransformChanged(action.pan, action.zoom, action.containerSize)
            
            else -> Unit
        }
    }

    private fun imageTransformChanged(
        pan: Offset,
        zoom: Float,
        containerSize: IntSize
    ) {
        _state.update {
            val newScale = (it.imageScale * zoom).coerceIn(1f, 5f)
            val newOffset = it.imageOffset + pan

            val maxX = (containerSize.width * (newScale - 1)) / 2f
            val maxY = (containerSize.height * (newScale - 1)) / 2f
            
            val constrainedOffset = Offset(
                x = newOffset.x.coerceIn(-maxX, maxX),
                y = newOffset.y.coerceIn(-maxY, maxY)
            )

            it.copy(
                imageScale = newScale,
                imageOffset = constrainedOffset
            )
        }
    }

    private fun loadMedication(medicationId: Int) {
        _state.update { it.copy(
            isLoading = true,
            errorMessage = null
        ) }
        
        viewModelScope.launch {
            try {
                medicationRepository.getMedicationById(medicationId)?.let { medication ->
                    _state.update { it.copy(
                        medicationItem = MedicationItem(
                            medication = medication,
                            formattedFrequency = Frequency.fromName(medication.frequency).toUiText(),
                            formattedType = MedicationType.fromName(medication.medicationType)?.toUiText()
                        ),
                        isLoading = false
                    ) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    errorMessage = UiText.StringResourceId(Res.string.failed_to_load_medication)
                ) }
            }
        }
    }

    private fun deleteMedication() {
        _state.update { it.copy(
            isDeletingImage = true,
            isDeletingMedication = true,
            showDeleteMedicationDialog = false
        ) }
        
        viewModelScope.launch {
            _state.value.medicationItem?.medication?.let { medication ->
                try {
                    medication.prescriptionImagePath?.let { safeDeleteImage(it) }
                    medication.medicationImagePath?.let { safeDeleteImage(it) }
                    
                    medicationRepository.deleteMedication(medication)
                    
                    _state.update { it.copy(
                        isDeletingImage = false,
                        isDeletingMedication = false,
                        isMedicationDeletedSuccessfully = true
                    ) }
                } catch (e: Exception) {
                    _state.update { it.copy(
                        isDeletingImage = false,
                        isDeletingMedication = false,
                        errorMessage = UiText.PluralStringResourceId(Res.plurals.failed_to_delete_medication, 1)
                    ) }
                }
            }
        }
    }

    private suspend fun safeDeleteImage(filePath: String) {
        val usageCount = medicationRepository.getImageUsageCount(filePath)
        if (usageCount <= 1) {
            internalImageStorage.deleteImage(filePath)
        }
    }
}
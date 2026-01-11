package com.nomanhassan.medremind.presentation.medication_reminder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nomanhassan.medremind.app.navigation.Route
import com.nomanhassan.medremind.core.notifications.ReminderScheduler
import com.nomanhassan.medremind.core.presentation.UiText
import com.nomanhassan.medremind.domain.repository.MedicationRepository
import com.nomanhassan.medremind.core.util.DateTimeFormatterUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.failed_to_load_reminder_details

class ReminderViewModel(
    savedStateHandle: SavedStateHandle,
    private val reminderScheduler: ReminderScheduler,
    private val medicationRepository: MedicationRepository
): ViewModel() {
    
    private val triggerTimeId = savedStateHandle.toRoute<Route.MedicationReminder>().reminderTimeId
    
    private val _state = MutableStateFlow(ReminderState())
    val state = _state.asStateFlow()
    
    init {
        loadReminderMedications(triggerTimeId)
    }
    
    fun onAction(action: ReminderAction) {
        when (action) {
            ReminderAction.OnAcknowledgeReminder -> onAcknowledgeReminder()
        }
    }
    
    private fun loadReminderMedications(triggerTimeId: Long) {
        _state.update { it.copy(
            isLoading = true
        ) }
        
        viewModelScope.launch {
            try {
                val dueMedications = medicationRepository.getDueMedicationsByTime(triggerTimeId)
                
                dueMedications?.let { meds ->
                    _state.update { it.copy(
                        isLoading = false,
                        reminderTime = DateTimeFormatterUtil.formatTime(triggerTimeId),
                        dueMedications = meds,
                        errorMessage = null
                    ) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    errorMessage = UiText.StringResourceId(Res.string.failed_to_load_reminder_details)
                ) }
            }
        }
    }
    
    private fun onAcknowledgeReminder() {
        _state.update { it.copy(
            isLoading = true
        ) }
        
        viewModelScope.launch {
            try {
                reminderScheduler.cancelReminder(triggerTimeId)
                
                _state.update { it.copy(
                    isLoading = false,
                    isAcknowledged = true
                ) }
            } catch (e: Exception) {
                e.printStackTrace()

                _state.update { it.copy(
                    isLoading = false,
                    isAcknowledged = true
                ) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        onAcknowledgeReminder()
    }
}
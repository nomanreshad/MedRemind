@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class, ExperimentalTime::class, ExperimentalUuidApi::class)

package com.nomanhassan.medremind.presentation.medication_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.SQLiteException
import com.nomanhassan.medremind.core.domain.DataError
import com.nomanhassan.medremind.core.enums.Frequency
import com.nomanhassan.medremind.core.enums.TimeSlot
import com.nomanhassan.medremind.core.presentation.UiText
import com.nomanhassan.medremind.core.presentation.toUiText
import com.nomanhassan.medremind.core.util.DateResourceMapper
import com.nomanhassan.medremind.core.util.DateTimeFormatterUtil
import com.nomanhassan.medremind.core.util.NextOccurrenceCalculator
import com.nomanhassan.medremind.data.local.storage.InternalImageStorage
import com.nomanhassan.medremind.data.mapper.toMedicationEntity
import com.nomanhassan.medremind.domain.model.Medication
import com.nomanhassan.medremind.domain.repository.MedicationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.duration_days_hours
import medremind.composeapp.generated.resources.duration_hours_minutes
import medremind.composeapp.generated.resources.duration_minutes
import medremind.composeapp.generated.resources.duration_overdue
import medremind.composeapp.generated.resources.duration_weeks
import medremind.composeapp.generated.resources.duration_weeks_days
import medremind.composeapp.generated.resources.failed_to_delete_medication
import medremind.composeapp.generated.resources.less_then_a_minute
import medremind.composeapp.generated.resources.medication_deleted_success
import org.jetbrains.compose.resources.getPluralString
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

class MedicationListViewModel(
    private val medicationRepository: MedicationRepository,
    private val internalImageStorage: InternalImageStorage
) : ViewModel() {

    private val _state = MutableStateFlow(MedicationListState())
    
    private val tickerFlow = flow { 
        while (true) {
            val now = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
            val secondsUntilNextMinute = 60 - now.second
            val delayMillis = (secondsUntilNextMinute * 1_000L) - (now.nanosecond / 1_000_000)
            
            emit(Unit)
            delay(delayMillis)
        }
    }

    private val pendingDeletions = mutableListOf<Medication>()
    private var undoDeleteJob: Job? = null

    val state = combine(
        _state,
        searchAndFilterMedications(),
        tickerFlow
    ) { state, medications, _ ->
        val visibleMedications = medications.filter {
            !state.temporarilyDeletedIds.contains(it.medicationId)
        }
        
        val uiItems = visibleMedications.map { med ->
            val nowMillis = Clock.System.now().toEpochMilliseconds()
            val nextDoseMillis = findNextDoseTime(med, nowMillis)
            val nextDoseIn = formatDurationUntilNextDose(nextDoseMillis)

            MedicationItem(
                medication = med,
                formattedFrequency = Frequency.fromName(med.frequency).toUiText(),
                nextDoseTime = nextDoseMillis?.let {
                    DateTimeFormatterUtil.formatTime(it)
                },
                nextDoseIn = nextDoseIn
            )
        }

        val now = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
        val dayOfWeek = UiText.StringResourceId(DateResourceMapper.mapDayOfWeek(now.dayOfWeek))
        val dayOfMonth = now.day.toString()
        val monthName = UiText.StringResourceId(DateResourceMapper.mapMonth(now.month))
        val year = now.year.toString()
        
        state.copy(
            medicationItems = uiItems,
            dayOfWeek = dayOfWeek,
            dayOfMonth = dayOfMonth,
            month = monthName,
            year = year,
            isLoading = false
        )
    }.catch { throwable ->
        val errorMessage = when (throwable) {
            is SQLiteException -> DataError.Local.DISK_FULL
            else -> DataError.Local.UNKNOWN
        }
        _state.update { it.copy(
            isLoading = false,
            eventMessage = errorMessage.toUiText()
        ) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = _state.value
    )
    
    private val eventChannel = Channel<MedicationListEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: MedicationListAction) {
        when (action) {
            // --- Search Actions ---
            is MedicationListAction.OnSearchQueryChange -> _state.update { it.copy(
                searchQuery = action.query,
                selectedMedicationIds = emptySet(),
                isSelectionMode = it.isSelectionMode
            ) }
            MedicationListAction.OnClearSearch -> _state.update { it.copy(
                searchQuery = "",
                selectedMedicationIds = emptySet(),
                isSelectionMode = it.isSelectionMode
            ) }
            MedicationListAction.OnToggleSearch -> _state.update { it.copy(isSearchBarVisible = !it.isSearchBarVisible) }

            // --- Filter Actions ---
            MedicationListAction.OnToggleFilterMenu -> _state.update { it.copy(isFilterMenuVisible = !it.isFilterMenuVisible) }
            is MedicationListAction.OnFilterOptionSelected -> _state.update { it.copy(
                selectedTimeSlot = action.timeSlot,
                selectedMedicationIds = emptySet(),
                isSelectionMode = it.isSelectionMode
            ) }

            MedicationListAction.OnToggleFabMenu -> _state.update { it.copy(isFabMenuExpanded = !it.isFabMenuExpanded) }

            // --- Medication Card Action ---
            is MedicationListAction.OnMedicationReminderToggled -> toggleReminder(medication = action.medication, isActive = action.isEnabled)

            // --- Multiselect Actions ---
            is MedicationListAction.OnToggleSelection -> toggleSelection(action.id)
            MedicationListAction.OnToggleSelectAll -> toggleSelectAll()
            MedicationListAction.OnCancelSelection -> _state.update { it.copy(
                isSelectionMode = false,
                selectedMedicationIds = emptySet()
            ) }
            MedicationListAction.OnClickDelete -> _state.update { it.copy(showDeleteMedicationDialog = true) }
            MedicationListAction.OnConfirmDelete -> deleteSelectedMedications()
            MedicationListAction.OnDismissDeleteDialog -> _state.update { it.copy(showDeleteMedicationDialog = false) }
            
            is MedicationListAction.OnDeleteIndividual -> prepareForDeletion(action.medication)
            MedicationListAction.OnUndoDelete -> undoDeletion()

            else -> Unit
        }
    }

    private fun toggleSelection(id: Int) {
        _state.update { currentState ->
            val newSelection = currentState.selectedMedicationIds.toMutableSet().apply {
                if (contains(id)) remove(id) else add(id)
            }
            currentState.copy(
                selectedMedicationIds = newSelection,
                isSelectionMode = newSelection.isNotEmpty()
            )
        }
    }

    private fun toggleSelectAll() {
        val visibleItems = state.value.medicationItems

        _state.update { currentState ->
            val allVisibleIds = visibleItems.map { it.medication.medicationId }.toSet()

            val areAllSelected = allVisibleIds.isNotEmpty() &&
                    currentState.selectedMedicationIds.containsAll(allVisibleIds)

            val newSelection = if (areAllSelected) emptySet() else allVisibleIds

            currentState.copy(
                selectedMedicationIds = newSelection,
                isSelectionMode = newSelection.isNotEmpty()
            )
        }
    }

    private fun prepareForDeletion(medication: Medication) {
        undoDeleteJob?.cancel()

        if (!pendingDeletions.contains(medication)) {
            pendingDeletions.add(medication)
        }

        _state.update {
            it.copy(temporarilyDeletedIds = it.temporarilyDeletedIds + medication.medicationId)
        }

        viewModelScope.launch {
            eventChannel.send(MedicationListEvent.OnShowUndoSnackbar(
                getPluralString(Res.plurals.medication_deleted_success, pendingDeletions.size)
            ))
        }

        undoDeleteJob = viewModelScope.launch {
            delay(4_000L)
            commitDeletions()
        }
    }

    private fun undoDeletion() {
        undoDeleteJob?.cancel()
        pendingDeletions.clear()
        _state.update { it.copy(temporarilyDeletedIds = emptySet()) }
    }

    private suspend fun commitDeletions() {
        if (pendingDeletions.isEmpty()) return

        val itemsToDelete = pendingDeletions.toList()
        pendingDeletions.clear()

        _state.update { it.copy(
            temporarilyDeletedIds = it.temporarilyDeletedIds - itemsToDelete.map { m -> m.medicationId }.toSet()
        ) }

        try {
            itemsToDelete.forEach { medication ->
                medication.prescriptionImagePath?.let { safeDeleteImage(it) }
                medication.medicationImagePath?.let { safeDeleteImage(it) }
            }
            
            medicationRepository.deleteMedications(itemsToDelete)
        } catch (e: Exception) {
            _state.update { it.copy(
                temporarilyDeletedIds = emptySet()
            ) }
            
            eventChannel.send(MedicationListEvent.OnMedicationDeleteError(
                getPluralString(Res.plurals.failed_to_delete_medication, pendingDeletions.size)
            ))
        }
    }

    private fun deleteSelectedMedications() {
        val idsToDelete = _state.value.selectedMedicationIds
        if (idsToDelete.isEmpty()) return

        _state.update { it.copy(isDeletingMedications = true) }

        viewModelScope.launch {
            try {
                val medicationsToDelete = medicationRepository.getMedicationsByIds(idsToDelete.toList())

                medicationsToDelete.forEach { medication ->
                    medication.prescriptionImagePath?.let { safeDeleteImage(it) }
                    medication.medicationImagePath?.let { safeDeleteImage(it) }
                }

                medicationRepository.deleteMedications(medicationsToDelete)

                _state.update { it.copy(
                    isDeletingMedications = false,
                    isSelectionMode = false,
                    showDeleteMedicationDialog = false,
                    selectedMedicationIds = emptySet()
                ) }

                eventChannel.send(MedicationListEvent.OnMedicationDeleteSuccess(
                    getPluralString(Res.plurals.medication_deleted_success, pendingDeletions.size)
                ))
            } catch (e: Exception) {
                _state.update { it.copy(
                    isDeletingMedications = false,
                    showDeleteMedicationDialog = false
                ) }

                eventChannel.send(MedicationListEvent.OnMedicationDeleteError(
                    getPluralString(Res.plurals.failed_to_delete_medication, pendingDeletions.size)
                ))
            }
        }
    }

    private suspend fun safeDeleteImage(filePath: String) {
        val usageCount = medicationRepository.getImageUsageCount(filePath)
        if (usageCount <= 1) {
            internalImageStorage.deleteImage(filePath)
        }
    }

    private fun searchAndFilterMedications(): Flow<List<Medication>> = combine(
        _state.map { it.searchQuery }
            .distinctUntilChanged()
            .debounce(500L),
        _state.map { it.selectedTimeSlot }
    ) { query, timeSlot ->
        query to timeSlot
    }.flatMapLatest { (query, timeSlot) ->
        when {
            query.isNotBlank() -> medicationRepository.searchMedications(query)
            timeSlot != TimeSlot.ALL -> medicationRepository.getMedicationsByTimeSlot(timeSlot)
            else -> medicationRepository.getAllMedications()
        }
    }
    
    private fun toggleReminder(medication: Medication, isActive: Boolean) {
        viewModelScope.launch { 
            medicationRepository.toggleActive(medication.medicationId, isActive)
        }
    }

    private fun findNextDoseTime(med: Medication, afterTime: Long): Long? {
        if (!med.isActive || med.frequency == Frequency.AS_NEEDED.name) return null

        return try {
            val entity = med.toMedicationEntity()
            NextOccurrenceCalculator.calculateNextSingleTrigger(entity, afterTime)
        } catch (e: Exception) { null }
    }

    private fun formatDurationUntilNextDose(nextDoseMillis: Long?): UiText? {
        if (nextDoseMillis == null) return null

        val duration = Instant.fromEpochMilliseconds(nextDoseMillis) - Clock.System.now()

        if (duration.isNegative()) return UiText.StringResourceId(Res.string.duration_overdue)

        val days = duration.inWholeDays
        val hours = duration.inWholeHours % 24
        val minutes = duration.inWholeMinutes % 60

        return when {
            days > 7 -> {
                val weeks = days / 7
                val remainingDays = days % 7
                if (remainingDays > 0) {
                    UiText.StringResourceId(Res.string.duration_weeks_days, arrayOf(weeks, remainingDays))
                } else {
                    UiText.StringResourceId(Res.string.duration_weeks, arrayOf(weeks))
                }
            }
            days > 0 -> UiText.StringResourceId(Res.string.duration_days_hours, arrayOf(days, hours))
            hours > 0 -> UiText.StringResourceId(Res.string.duration_hours_minutes, arrayOf(hours, minutes))
            minutes > 0 -> UiText.StringResourceId(Res.string.duration_minutes, arrayOf(minutes))
            else -> UiText.StringResourceId(Res.string.less_then_a_minute)
        }
    }
}
@file:OptIn(ExperimentalUuidApi::class)

package com.nomanhassan.medremind.presentation.add_edit_medication

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mohamedrejeb.calf.io.KmpFile
import com.nomanhassan.medremind.app.navigation.Route
import com.nomanhassan.medremind.core.domain.onError
import com.nomanhassan.medremind.core.domain.onSuccess
import com.nomanhassan.medremind.core.enums.AiImageAnalysisResult
import com.nomanhassan.medremind.core.enums.Frequency
import com.nomanhassan.medremind.core.enums.MedicationType
import com.nomanhassan.medremind.data.local.storage.FileHelper
import com.nomanhassan.medremind.data.local.storage.InternalImageStorage
import com.nomanhassan.medremind.domain.model.Medication
import com.nomanhassan.medremind.domain.repository.MedicationRepository
import com.nomanhassan.medremind.core.util.DateTimeFormatterUtil
import com.nomanhassan.medremind.presentation.add_edit_medication.utils.FieldValidation
import com.nomanhassan.medremind.presentation.add_edit_medication.utils.Validators
import com.nomanhassan.medremind.core.enums.ImageType
import com.nomanhassan.medremind.data.local.settings.AppearancePreferences
import com.nomanhassan.medremind.domain.repository.AiPrescriptionRepository
import com.nomanhassan.medremind.domain.settings.Language
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.error_analysis_failed
import medremind.composeapp.generated.resources.error_disk_full
import medremind.composeapp.generated.resources.error_failed_save_image
import medremind.composeapp.generated.resources.error_image_blurry
import medremind.composeapp.generated.resources.error_not_a_prescription
import medremind.composeapp.generated.resources.msg_prescription_detected
import org.jetbrains.compose.resources.getString
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AddEditMedicationViewModel(
    savedStateHandle: SavedStateHandle,
    private val medicationRepository: MedicationRepository,
    private val fileHelper: FileHelper,
    private val internalImageStorage: InternalImageStorage,
    private val aiPrescriptionRepository: AiPrescriptionRepository,
    private val preferences: AppearancePreferences
) : ViewModel() {
    
    private val medicationId = savedStateHandle.toRoute<Route.AddEditMedication>().id
    
    private val _state = MutableStateFlow(AddEditMedicationState())
    
    val state = _state
        .onStart {
            medicationId?.let { id ->
                loadMedicationForEdit(id)
            } ?: setInitialFormState(listOf(MedicationFormState()))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value
        )

    private val eventChannel = Channel<AddEditMedicationEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: AddEditMedicationAction) {
        when (action) {
            // Carousel Actions
            AddEditMedicationAction.OnNextMedication -> nextMedication()
            AddEditMedicationAction.OnPreviousMedication -> previousMedication()
            AddEditMedicationAction.OnAddAnotherMedication -> addAnotherMedication()
            is AddEditMedicationAction.OnRemoveMedication -> removeMedicationFormAt(action.index)

            // Field Changes
            is AddEditMedicationAction.OnMedicineNameChanged -> updateFormField { it.copy(medicineName = action.name) }
            is AddEditMedicationAction.OnDosageStrengthChanged -> updateFormField { it.copy(dosageStrength = action.strength) }
            is AddEditMedicationAction.OnMedicationTypeChanged -> {
                updateFormField { it.copy(medicationType = action.type) }
                _state.update { it.copy(activeDropdownMenu = ActiveDropdownMenu.NONE) }
            }
            is AddEditMedicationAction.OnFrequencyChanged -> {
                handleFrequencyChange(action.frequency)
                _state.update { it.copy(activeDropdownMenu = ActiveDropdownMenu.NONE) }
            }
            is AddEditMedicationAction.OnTimeSelected -> {
                updateTime(
                    index = action.index,
                    time = DateTimeFormatterUtil.formatTime(action.time)
                )
                _state.update { it.copy(timePickerIndexToShow = null) }
            }
            is AddEditMedicationAction.OnStartDateSelected -> {
                updateFormField { it.copy(
                    startDate = DateTimeFormatterUtil.formatDate(action.date)
                ) }
                _state.update { it.copy(isStartDatePickerVisible = false) }
            }
            is AddEditMedicationAction.OnEndDateSelected -> {
                updateFormField { it.copy(
                    endDate = DateTimeFormatterUtil.formatDate(action.date)
                ) }
                _state.update { it.copy(isEndDatePickerVisible = false) }
            }
            is AddEditMedicationAction.OnNotesChanged -> updateFormField { it.copy(notes = action.notes) }
            
            // Other Information
            is AddEditMedicationAction.OnHospitalNameChanged -> updateFormField { it.copy(hospitalName = action.name) }
            is AddEditMedicationAction.OnDoctorNameChanged -> updateFormField { it.copy(doctorName = action.name) }
            is AddEditMedicationAction.OnHospitalAddressChanged -> updateFormField { it.copy(hospitalAddress = action.address) }

            // Image Actions
            is AddEditMedicationAction.OnUploadImageClick -> updateFormField { it.copy(imageType = action.imageType) }
            is AddEditMedicationAction.OnImagePicked -> handleImagePicked(action.kmpFile, action.imageType)
            is AddEditMedicationAction.OnDeleteImageClick -> deleteImage(action.imageType)

            // AI Prescription Scan
            is AddEditMedicationAction.OnPrescriptionImagePicked -> analyzePrescription(action.kmpFile)

            // Picker Actions
            is AddEditMedicationAction.OnShowTimePicker -> _state.update { it.copy(timePickerIndexToShow = action.index) }
            AddEditMedicationAction.OnShowStartDatePicker -> _state.update { it.copy(isStartDatePickerVisible = true) }
            AddEditMedicationAction.OnShowEndDatePicker -> _state.update { it.copy(isEndDatePickerVisible = true) }
            AddEditMedicationAction.OnDismissTimePicker -> _state.update { it.copy(timePickerIndexToShow = null) }
            AddEditMedicationAction.OnDismissStartDatePicker -> _state.update { it.copy(isStartDatePickerVisible = false) }
            AddEditMedicationAction.OnDismissEndDatePicker -> _state.update { it.copy(isEndDatePickerVisible = false) }

            // Dropdown Actions
            AddEditMedicationAction.OnShowMedicationTypeDropdown -> _state.update { it.copy(
                activeDropdownMenu = ActiveDropdownMenu.MEDICATION_TYPE
            ) }
            AddEditMedicationAction.OnShowFrequencyDropdown -> _state.update { it.copy(activeDropdownMenu = ActiveDropdownMenu.FREQUENCY) }
            AddEditMedicationAction.OnHideDropdown -> _state.update { it.copy(activeDropdownMenu = ActiveDropdownMenu.NONE) }

            // Main Actions
            AddEditMedicationAction.OnSaveClick -> validateAndRequestPermission()
            is AddEditMedicationAction.OnPermissionResult -> {
                if (action.isGranted) {
                    _state.update { it.copy(
                        shouldShowRationale = false, isSaving = true
                    ) }
                    saveMedicationsAndScheduleReminders()
                } else _state.update { it.copy(shouldShowRationale = true) }
                _state.update { it.copy(isSavePendingPermission = false) }
            }
            AddEditMedicationAction.OnShowRationaleDialog -> {
                _state.update { it.copy(shouldShowRationale = true) }
            }
            AddEditMedicationAction.OnConfirmSaveWithoutNotifications -> {
                _state.update { it.copy(
                    shouldShowRationale = false, isSaving = true
                ) }
                saveMedicationsAndScheduleReminders()
            }
            AddEditMedicationAction.OnDismissSaveWithoutNotifications -> {
                _state.update { it.copy(
                    shouldShowRationale = false,
                    isSaving = false,
                    isSavePendingPermission = false
                ) }
            }
            
            AddEditMedicationAction.OnConfirmLeaveWithoutSaving -> confirmLeave()
            AddEditMedicationAction.OnDismissLeaveWithoutSaving -> dismissConfirmLeaveDialog()
            AddEditMedicationAction.OnGoBack -> attemptToGoBack()
        }
    }

    private fun analyzePrescription(kmpFile: KmpFile) {
        _state.update { it.copy(
            isAnalyzingPrescription = true
        ) }

        viewModelScope.launch {
            val targetLanguage = preferences.aiLanguagePreference.value
            val deviceLanguage = when (targetLanguage) {
                Language.BANGLA -> Language.BANGLA.label
                Language.GERMAN -> Language.GERMAN.label
                else -> Language.ENGLISH.label
            }
            
            with(fileHelper) {
                val bytes = kmpFile.readBytes()
                val fileName ="prescription_${Uuid.random()}.jpg"
                var filePath = ""

                if (bytes != null) {
                    internalImageStorage.saveImage(bytes, fileName)
                        .onSuccess { filePath = it }
                        .onError { dataErrorLocal ->
                            _state.update { it.copy(
                                isAnalyzingPrescription = false
                            ) }
                            eventChannel.send(AddEditMedicationEvent.OnImageSaveError(
                                getString(Res.string.error_failed_save_image)
                            ))
                            return@launch
                        }
                    
                    aiPrescriptionRepository
                        .analyzePrescriptionImage(
                            imageData = bytes,
                            imagePath = filePath,
                            deviceLanguage = deviceLanguage,
                            targetLanguage = targetLanguage
                        )
                        .onSuccess { response ->
                            when (response.analysisResult) {
                                AiImageAnalysisResult.PRESCRIPTION_FOUND -> {
                                    _state.update { it.copy(
                                        isAnalyzingPrescription = false,
                                        medicationForms = response.forms
                                    ) }
                                    eventChannel.send(AddEditMedicationEvent.PrescriptionFound(
                                        getString(Res.string.msg_prescription_detected)
                                    ))
                                }
                                AiImageAnalysisResult.BLURRY_OR_UNCLEAR -> {
                                    internalImageStorage.deleteImage(filePath)
                                    
                                    _state.update { it.copy(
                                        isAnalyzingPrescription = false
                                    ) }
                                    eventChannel.send(AddEditMedicationEvent.PrescriptionBlurryOrUnclear(
                                        getString(Res.string.error_image_blurry)
                                    ))
                                }
                                AiImageAnalysisResult.NOT_A_PRESCRIPTION -> {
                                    internalImageStorage.deleteImage(filePath)
                                    
                                    _state.update { it.copy(
                                        isAnalyzingPrescription = false
                                    ) }
                                    eventChannel.send(AddEditMedicationEvent.NotAPrescription(
                                        getString(Res.string.error_not_a_prescription)
                                    ))
                                }
                            }
                        }
                        .onError { dataErrorRemote ->
                            internalImageStorage.deleteImage(filePath)
                            
                            _state.update { it.copy(
                                isAnalyzingPrescription = false
                            ) }
                            eventChannel.send(AddEditMedicationEvent.OnImageAnalysisError(
                                getString(Res.string.error_analysis_failed)
                            ))
                        }
                } else {
                    _state.update { it.copy(
                        isAnalyzingPrescription = false
                    ) }
                }
            }
        }
    }

    private fun confirmLeave() {
        _state.update { it.copy(
            hasLeftForm = true
        ) }
    }

    private fun dismissConfirmLeaveDialog() {
        _state.update { it.copy(
            isLeavingWithoutSaving = false
        ) }
    }

    private fun attemptToGoBack() {
        val formsChanged = _state.value.medicationForms != _state.value.originalForms
        
        if (formsChanged) {
            _state.update { it.copy(
                isLeavingWithoutSaving = true
            ) }
        } else {
            _state.update { it.copy(
                hasLeftForm = true
            ) }
        }
    }
    
    private fun setInitialFormState(forms: List<MedicationFormState>) {
        val deepCopy = forms.map { it.copy() }
        
        _state.update { it.copy(
            medicationForms = forms,
            originalForms = deepCopy,
            currentMedicationIndex = 0,
            isLeavingWithoutSaving = false,
            hasLeftForm = false
        ) }
    }

    private fun loadMedicationForEdit(id: Int) {
        _state.update { it.copy(
            isLoading = true
        ) }

        viewModelScope.launch {
            val medication = medicationRepository.getMedicationById(id)
            
            medication?.let { med ->
                val loadedForm = MedicationFormState(
                    id = med.medicationId,
                    medicineName = med.medicineName,
                    dosageStrength = med.dosageStrength ?: "",
                    medicationType = MedicationType.entries.find { it.name == med.medicationType },
                    frequency = Frequency.entries.find { it.name == med.frequency },
                    times = med.formattedTimes,
                    startDate = med.formattedStartDate,
                    endDate = med.formattedEndDate,
                    notes = med.notes ?: "",
                    hospitalName = med.hospitalName ?: "",
                    doctorName = med.doctorName ?: "",
                    hospitalAddress = med.hospitalAddress ?: "",
                    prescriptionImagePath = med.prescriptionImagePath,
                    medicationImagePath = med.medicationImagePath
                )
                
                setInitialFormState(listOf(loadedForm))
                
                _state.update { it.copy(
                    isLoading = false
                ) }
            } ?: setInitialFormState(listOf(MedicationFormState()))
            
            _state.update { it.copy(
                isLoading = false
            ) }
        }
    }
    
    private fun deleteImage(imageType: ImageType) {
        when (imageType) {
            ImageType.PRESCRIPTION -> {
                updateFormField { it.copy(
                    prescriptionImagePath = null
                ) }
            }
            ImageType.MEDICATION -> {
                updateFormField { it.copy(
                    medicationImagePath = null
                ) }
            }
        }
    }

    private fun handleImagePicked(
        kmpFile: KmpFile,
        imageType: ImageType,
    ) {
        viewModelScope.launch {
            with(fileHelper) {
                val bytes = kmpFile.readBytes()
                
                val fileName = when (imageType) {
                    ImageType.PRESCRIPTION -> "prescription_${Uuid.random()}.jpg"
                    ImageType.MEDICATION -> "medication_${Uuid.random()}.jpg"
                }
                
                if (bytes != null) {
                    internalImageStorage.saveImage(bytes, fileName)
                        .onSuccess { filePath ->
                            when (imageType) {
                                ImageType.PRESCRIPTION -> {
                                    updateFormField { it.copy(
                                        prescriptionImagePath = filePath
                                    ) }
                                }
                                ImageType.MEDICATION -> {
                                    updateFormField { it.copy(
                                        medicationImagePath = filePath
                                    ) }
                                }
                            }
                        }
                        .onError { dataErrorLocal ->
                            eventChannel.send(AddEditMedicationEvent.OnImageSaveError(
                                getString(Res.string.error_failed_save_image)
                            ))
                        }
                }
            }
        }
    }

    private fun nextMedication() {
        _state.update {
            if (it.currentMedicationIndex < it.medicationForms.size - 1) {
                it.copy(currentMedicationIndex = it.currentMedicationIndex + 1)
            } else it
        }
    }

    private fun previousMedication() {
        _state.update {
            if (it.currentMedicationIndex > 0) {
                it.copy(currentMedicationIndex = it.currentMedicationIndex - 1)
            } else it
        }
    }

    private fun addAnotherMedication() {
        _state.update {
            val newForms = it.medicationForms + MedicationFormState()
            it.copy(
                medicationForms = newForms,
                currentMedicationIndex = newForms.lastIndex
            )
        }
    }

    private fun removeMedicationFormAt(index: Int) {
        _state.update {
            if (it.medicationForms.size > 1) {
                val newForms = it.medicationForms.toMutableList().apply { removeAt(index) }
                val newIndex = if (it.currentMedicationIndex >= newForms.size) newForms.lastIndex else it.currentMedicationIndex
                it.copy(medicationForms = newForms, currentMedicationIndex = newIndex)
            } else it.copy(medicationForms = listOf(MedicationFormState()), currentMedicationIndex = 0)
        }
    }

    private fun handleFrequencyChange(frequency: Frequency) {
        val newTimes = when (frequency) {
            Frequency.ONCE_DAILY,
            Frequency.EVERY_FOUR_HOURS,
            Frequency.EVERY_SIX_HOURS,
            Frequency.WEEKLY,
            Frequency.MONTHLY -> listOf(null)
            Frequency.TWICE_DAILY -> listOf(null, null)
            Frequency.THRICE_DAILY -> listOf(null, null, null)
            Frequency.AS_NEEDED -> emptyList()
        }
        updateFormField { it.copy(frequency = frequency, times = newTimes) }
    }
    
    private fun updateTime(index: Int, time: String?) {
        updateFormField { currentState ->
            val newTimes = currentState.times.toMutableList()
            if (index in newTimes.indices) newTimes[index] = time
            currentState.copy(times = newTimes)
        }
    }

    private fun updateFormField(update: (MedicationFormState) -> MedicationFormState) {
        _state.update {
            val currentForm = it.currentMedicationForm ?: return@update it
            val updatedForm = update(currentForm)
            val newForms = it.medicationForms.toMutableList()
            newForms[it.currentMedicationIndex] = updatedForm
            it.copy(medicationForms = newForms)
        }
    }

    private fun validateAndRequestPermission() {
        var isValid = true

        val validatedForms = _state.value.medicationForms.map { form ->
            val medicineNameVal = Validators.validateMedicineName(form.medicineName)
            val dosageStrengthVal = Validators.validateDosageStrength(form.dosageStrength)
            val formattedMedicationTypeVal = Validators.validateMedicationType(form.medicationType?.name)
            val formattedFrequencyVal = Validators.validateFrequencyType(form.frequency?.name)
            val formattedTimesVal = Validators.validateTimes(form.times)
            val formattedStartDateVal = Validators.validateStartDate(form.startDate)
            val formattedEndDateVal = Validators.validateEndDate(form.endDate)
            val notesVal = Validators.validateNotes(form.notes)
            val hospitalNameVal = Validators.validateHospitalName(form.hospitalName)
            val doctorNameVal = Validators.validateDoctorName(form.doctorName)
            val hospitalAddressVal = Validators.validateHospitalAddress(form.hospitalAddress)
            val prescriptionImageVal = Validators.validateImageUri(form.prescriptionImagePath)
            val medicationImageVal = Validators.validateImageUri(form.medicationImagePath)
            
            val hasError = listOf(
                medicineNameVal, dosageStrengthVal, formattedMedicationTypeVal, formattedFrequencyVal,
                formattedTimesVal, formattedStartDateVal, formattedEndDateVal, notesVal, hospitalNameVal,
                doctorNameVal, hospitalAddressVal, prescriptionImageVal, medicationImageVal
            ).any { it is FieldValidation.Invalid }
            
            if (hasError) isValid = false

            form.copy(
                medicineNameError = (medicineNameVal as? FieldValidation.Invalid)?.reason,
                medicationTypeError = (formattedMedicationTypeVal as? FieldValidation.Invalid)?.reason,
                frequencyError = (formattedFrequencyVal as? FieldValidation.Invalid)?.reason,
                timesError = (formattedTimesVal as? FieldValidation.Invalid)?.reason,
                startDateError = (formattedStartDateVal as? FieldValidation.Invalid)?.reason,
                endDateError = (formattedEndDateVal as? FieldValidation.Invalid)?.reason,
            )
        }
        
        _state.update { it.copy(
            medicationForms = validatedForms
        ) }
        
        if (isValid) {
            viewModelScope.launch {
                val requiresNotification = _state.value.medicationForms
                    .any { formState ->
                        formState.frequency != Frequency.AS_NEEDED &&
                                formState.times.any { time -> time != null }
                    }
                
                if (requiresNotification) {
                    _state.update { it.copy(isSavePendingPermission = true) }
                    eventChannel.send(AddEditMedicationEvent.RequestNotificationPermission)
                } else saveMedicationsAndScheduleReminders()
            }
        }
    }
    
    private fun saveMedicationsAndScheduleReminders() {
        _state.update { it.copy(isSaving = true) }
        
        val forms = _state.value.medicationForms
        val originalForms = _state.value.originalForms
        
        val medications = forms.map { form ->
            Medication(
                medicationId = form.id ?: 0,
                medicineName = form.medicineName.trim(),
                dosageStrength = form.dosageStrength.trim(),
                medicationType = form.medicationType?.name,
                frequency = form.frequency?.name!!,
                formattedTimes = form.times,
                formattedStartDate = form.startDate,
                formattedEndDate = form.endDate,
                notes = form.notes.trim(),
                hospitalName = form.hospitalName.trim(),
                doctorName = form.doctorName.trim(),
                hospitalAddress = form.hospitalAddress.trim(),
                prescriptionImagePath = form.prescriptionImagePath,
                medicationImagePath = form.medicationImagePath,
                isActive = true
            )
        }
        
        viewModelScope.launch {
            medicationRepository
                .saveMedicationsAndScheduleReminders(medications)
                .onSuccess {
                    performFileCleanup(originalForms, forms)
                    
                    _state.update { it.copy(
                        isSaving = false,
                        isSavePendingPermission = false
                    ) }
                    eventChannel.send(AddEditMedicationEvent.OnSaveSuccess)
                }
                .onError { dataErrorLocal ->
                    _state.update { it.copy(
                        isSaving = false,
                        isSavePendingPermission = false
                    ) }
                    eventChannel.send(AddEditMedicationEvent.OnSaveError(
                        getString(Res.string.error_disk_full)
                    ))
                }
        }
    }

    private fun performFileCleanup(
        oldForms: List<MedicationFormState>,
        newForms: List<MedicationFormState>
    ) {
        viewModelScope.launch {
            val oldPaths = oldForms.flatMap {
                listOfNotNull(it.prescriptionImagePath, it.medicationImagePath)
            }.toSet()

            val newPaths = newForms.flatMap {
                listOfNotNull(it.prescriptionImagePath, it.medicationImagePath)
            }.toSet()

            val potentialTrash = oldPaths - newPaths

            potentialTrash.forEach { filePath ->
                val count = medicationRepository.getImageUsageCount(filePath)
                if (count == 0) {
                    internalImageStorage.deleteImage(filePath)
                }
            }
        }
    }
}
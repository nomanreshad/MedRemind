@file:OptIn(ExperimentalTime::class, ExperimentalPermissionsApi::class,
    ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class
)

package com.nomanhassan.medremind.presentation.add_edit_medication

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mohamedrejeb.calf.permissions.ExperimentalPermissionsApi
import com.mohamedrejeb.calf.permissions.Permission
import com.mohamedrejeb.calf.permissions.isGranted
import com.mohamedrejeb.calf.permissions.rememberPermissionState
import com.mohamedrejeb.calf.permissions.shouldShowRationale
import com.mohamedrejeb.calf.picker.FilePickerFileType
import com.mohamedrejeb.calf.picker.FilePickerSelectionMode
import com.mohamedrejeb.calf.picker.rememberFilePickerLauncher
import com.nomanhassan.medremind.app.ui.theme.MedRemindTheme
import com.nomanhassan.medremind.core.enums.Frequency
import com.nomanhassan.medremind.core.enums.ImageType
import com.nomanhassan.medremind.core.enums.MedicationType
import com.nomanhassan.medremind.core.presentation.ObserveAsEvents
import com.nomanhassan.medremind.core.presentation.components.ConfirmationDialog
import com.nomanhassan.medremind.core.presentation.components.ConfirmationDialogConfig
import com.nomanhassan.medremind.core.presentation.components.FloatingToolbarDefaults
import com.nomanhassan.medremind.core.presentation.components.HorizontalFloatingToolbar
import com.nomanhassan.medremind.core.presentation.toUiText
import com.nomanhassan.medremind.presentation.add_edit_medication.components.AddEditMedicationTopAppBar
import com.nomanhassan.medremind.presentation.add_edit_medication.components.AiLoadingDialog
import com.nomanhassan.medremind.presentation.add_edit_medication.components.DatePickerField
import com.nomanhassan.medremind.presentation.add_edit_medication.components.FormDropDown
import com.nomanhassan.medremind.presentation.add_edit_medication.components.FormTextField
import com.nomanhassan.medremind.presentation.add_edit_medication.components.ImageUploadConfig
import com.nomanhassan.medremind.presentation.add_edit_medication.components.ImageUploadSection
import com.nomanhassan.medremind.presentation.add_edit_medication.components.TimePickerDialog
import com.nomanhassan.medremind.presentation.add_edit_medication.components.TimePickerField
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.ai_prescription_scan
import medremind.composeapp.generated.resources.ai_prescription_scan_message_1
import medremind.composeapp.generated.resources.ai_prescription_scan_message_2
import medremind.composeapp.generated.resources.ai_prescription_scan_message_3
import medremind.composeapp.generated.resources.ai_prescription_scan_message_4
import medremind.composeapp.generated.resources.btn_add_another
import medremind.composeapp.generated.resources.btn_cancel
import medremind.composeapp.generated.resources.btn_save_all_medications
import medremind.composeapp.generated.resources.btn_save_medication
import medremind.composeapp.generated.resources.btn_upload_medication_image_description
import medremind.composeapp.generated.resources.btn_upload_prescription_image_description
import medremind.composeapp.generated.resources.cancel
import medremind.composeapp.generated.resources.delete_medication
import medremind.composeapp.generated.resources.delete_prescription
import medremind.composeapp.generated.resources.edit_medication_title
import medremind.composeapp.generated.resources.hint_doctor_name
import medremind.composeapp.generated.resources.hint_dosage
import medremind.composeapp.generated.resources.hint_frequency
import medremind.composeapp.generated.resources.hint_hospital_address
import medremind.composeapp.generated.resources.hint_hospital_name
import medremind.composeapp.generated.resources.hint_medication_type
import medremind.composeapp.generated.resources.hint_medicine_name
import medremind.composeapp.generated.resources.hint_notes
import medremind.composeapp.generated.resources.hint_select
import medremind.composeapp.generated.resources.icon_document_scanner
import medremind.composeapp.generated.resources.icon_filled_add
import medremind.composeapp.generated.resources.icon_outline_rounded_cancel
import medremind.composeapp.generated.resources.icon_save
import medremind.composeapp.generated.resources.label_doctor_name
import medremind.composeapp.generated.resources.label_dosage_strength
import medremind.composeapp.generated.resources.label_end_date
import medremind.composeapp.generated.resources.label_frequency
import medremind.composeapp.generated.resources.label_hospital_address
import medremind.composeapp.generated.resources.label_hospital_name
import medremind.composeapp.generated.resources.label_medication_type
import medremind.composeapp.generated.resources.label_medicine_name
import medremind.composeapp.generated.resources.label_notes
import medremind.composeapp.generated.resources.label_start_date
import medremind.composeapp.generated.resources.label_time
import medremind.composeapp.generated.resources.leave
import medremind.composeapp.generated.resources.leave_form_message
import medremind.composeapp.generated.resources.leave_form_title
import medremind.composeapp.generated.resources.new_medication_title
import medremind.composeapp.generated.resources.reminders_disabled_message
import medremind.composeapp.generated.resources.reminders_disabled_title
import medremind.composeapp.generated.resources.required_fields_info
import medremind.composeapp.generated.resources.save_anyway
import medremind.composeapp.generated.resources.section_title_medicine_info
import medremind.composeapp.generated.resources.section_title_other_info
import medremind.composeapp.generated.resources.section_title_upload_images
import medremind.composeapp.generated.resources.select_end_date
import medremind.composeapp.generated.resources.select_start_date
import medremind.composeapp.generated.resources.this_medication
import medremind.composeapp.generated.resources.upload_images_later_info
import medremind.composeapp.generated.resources.upload_medication
import medremind.composeapp.generated.resources.upload_prescription
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.ExperimentalTime

@Composable
fun AddEditMedicationScreenRoot(
    onGoBack: () -> Unit,
    viewModel: AddEditMedicationViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    val snackbarState = remember { SnackbarHostState() }
    
    val notificationPermissionState = rememberPermissionState(Permission.Notification)
    
    LaunchedEffect(state.hasLeftForm) {
        if (state.hasLeftForm) {
            onGoBack()
        }
    }
    
    LaunchedEffect(state.isSavePendingPermission, notificationPermissionState.status.isGranted) {
        if (state.isSavePendingPermission && notificationPermissionState.status.isGranted) {
            viewModel.onAction(AddEditMedicationAction.OnPermissionResult(true))
        }
    }
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            AddEditMedicationEvent.OnSaveSuccess -> onGoBack()
            is AddEditMedicationEvent.OnSaveError -> {
                snackbarState.showSnackbar(
                    message = event.message,
                    withDismissAction = true
                )
            }
            is AddEditMedicationEvent.OnImageDeleteError -> {
                snackbarState.showSnackbar(
                    message = event.message,
                    withDismissAction = true
                )
            }
            is AddEditMedicationEvent.OnImageSaveError -> {
                snackbarState.showSnackbar(
                    message = event.message,
                    withDismissAction = true
                )
            }
            
            is AddEditMedicationEvent.PrescriptionFound -> {
                snackbarState.showSnackbar(
                    message = event.message,
                    withDismissAction = true
                )
            }
            is AddEditMedicationEvent.PrescriptionBlurryOrUnclear -> {
                snackbarState.showSnackbar(
                    message = event.message,
                    withDismissAction = true
                )
            }
            is AddEditMedicationEvent.NotAPrescription -> {
                snackbarState.showSnackbar(
                    message = event.message,
                    withDismissAction = true
                )
            }
            is AddEditMedicationEvent.OnImageAnalysisError -> {
                snackbarState.showSnackbar(
                    message = event.message,
                    withDismissAction = true
                )
            }
            
            AddEditMedicationEvent.RequestNotificationPermission -> {
                when {
                    notificationPermissionState.status.shouldShowRationale ->
                        viewModel.onAction(AddEditMedicationAction.OnShowRationaleDialog)
                    
                    else -> notificationPermissionState.launchPermissionRequest()
                }
            }
        }
    }

    AddEditMedicationScreen(
        state = state,
        snackbarState = snackbarState,
        onAction = viewModel::onAction
    )
}

@Composable
fun AddEditMedicationScreen(
    state: AddEditMedicationState,
    snackbarState: SnackbarHostState,
    onAction: (AddEditMedicationAction) -> Unit
) {
    BackHandler(!state.isLeavingWithoutSaving) {
        onAction(AddEditMedicationAction.OnGoBack)
    }
    
    val currentForm = state.currentMedicationForm
    val scrollState = rememberScrollState()

    LaunchedEffect(state.currentMedicationIndex) {
        scrollState.animateScrollTo(0)
    }

    val pickerLauncher = rememberFilePickerLauncher(
        type = FilePickerFileType.Image,
        selectionMode = FilePickerSelectionMode.Single,
        onResult = { kmpFiles ->
            kmpFiles.firstOrNull()?.let { kmpFile ->
                currentForm?.let { form ->
                    onAction(AddEditMedicationAction.OnImagePicked(kmpFile, form.imageType))
                }
            }
        }
    )

    val prescriptionPickerLauncher = rememberFilePickerLauncher(
        type = FilePickerFileType.Image,
        selectionMode = FilePickerSelectionMode.Single,
        onResult = { kmpFiles ->
            kmpFiles.firstOrNull()?.let { kmpFile ->
                currentForm?.let {
                    onAction(AddEditMedicationAction.OnPrescriptionImagePicked(kmpFile))
                }
            }
        }
    )
    
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val isEditMode = currentForm?.id != null
    val topBarTitle = when {
        state.isLoading -> ""
        isEditMode -> stringResource(Res.string.edit_medication_title)
        else -> stringResource(Res.string.new_medication_title)
    }

    var expanded by rememberSaveable { mutableStateOf(true) }
    val vibrantColors = FloatingToolbarDefaults.vibrantFloatingToolbarColors()
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            AddEditMedicationTopAppBar(
                title = topBarTitle,
                medicationCount = state.medicationForms.size,
                currentMedicationIndex = state.currentMedicationIndex,
                onBackClicked = { onAction(AddEditMedicationAction.OnGoBack) },
                onPreviousClicked = { onAction(AddEditMedicationAction.OnPreviousMedication) },
                onNextClicked = { onAction(AddEditMedicationAction.OnNextMedication) },
                onRemoveClicked = { onAction(AddEditMedicationAction.OnRemoveMedication(state.currentMedicationIndex)) }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarState)
        },
        floatingActionButton = {
            HorizontalFloatingToolbar(
                expanded = expanded,
                floatingActionButton = {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                        tooltip = {
                            PlainTooltip {
                                Text(
                                    text = if (state.medicationForms.size > 1) {
                                        stringResource(Res.string.btn_save_all_medications)
                                    } else stringResource(Res.string.btn_save_medication),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        },
                        state = rememberTooltipState(),
                    ) {
                        FloatingToolbarDefaults.StandardFloatingActionButton(
                            onClick = {
                                if (!state.isSaving) onAction(AddEditMedicationAction.OnSaveClick)
                            }
                        ) {
                            if (state.isSaving) {
                                CircularProgressIndicator()
                            } else {
                                Icon(
                                    painter = painterResource(Res.drawable.icon_save),
                                    contentDescription = if (state.medicationForms.size > 1) {
                                        stringResource(Res.string.btn_save_all_medications)
                                    } else stringResource(Res.string.btn_save_medication)
                                )
                            }
                        }
                    }
                },
                colors = vibrantColors,
            ) {
                if (!state.isLoading) {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                        tooltip = {
                            PlainTooltip {
                                Text(
                                    text = stringResource(Res.string.btn_cancel),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(
                            onClick = { onAction(AddEditMedicationAction.OnGoBack) },
                            enabled = !state.isSaving,
                            modifier = Modifier.focusProperties { canFocus = expanded },
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.icon_outline_rounded_cancel),
                                contentDescription = stringResource(Res.string.btn_cancel)
                            )
                        }
                    }

                    if (!isEditMode) {
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                            tooltip = {
                                PlainTooltip {
                                    Text(
                                        text = stringResource(Res.string.ai_prescription_scan),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            },
                            state = rememberTooltipState(),
                        ) {
                            IconButton(
                                onClick = { prescriptionPickerLauncher.launch() },
                                enabled = !state.isSaving,
                                modifier = Modifier.focusProperties { canFocus = expanded }
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.icon_document_scanner),
                                    contentDescription = stringResource(Res.string.ai_prescription_scan)
                                )
                            }
                        }

                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                            tooltip = {
                                PlainTooltip {
                                    Text(
                                        text = stringResource(Res.string.btn_add_another),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            },
                            state = rememberTooltipState(),
                        ) {
                            IconButton(
                                onClick = { onAction(AddEditMedicationAction.OnAddAnotherMedication) },
                                enabled = !state.isSaving,
                                modifier = Modifier.focusProperties { canFocus = expanded }
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.icon_filled_add),
                                    contentDescription = stringResource(Res.string.btn_add_another)
                                )
                            }
                        }
                    }
                }
                
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = MaterialTheme.colorScheme.primary
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { focusManager.clearFocus() }
                    )
                },
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp
            )
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (currentForm != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .consumeWindowInsets(WindowInsets.ime)
                        .imePadding()
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(12.dp))
                    // Medication Information
                    Text(
                        text = stringResource(Res.string.required_fields_info),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = stringResource(Res.string.section_title_medicine_info),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    FormTextField(
                        value = currentForm.medicineName,
                        onValueChange = { onAction(AddEditMedicationAction.OnMedicineNameChanged(it)) },
                        label = stringResource(Res.string.label_medicine_name),
                        placeholder = stringResource(Res.string.hint_medicine_name),
                        error = currentForm.medicineNameError,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(FocusDirection.Down)
                            }
                        ),
                        modifier = Modifier
                            .widthIn(max = 500.dp)
                            .fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    FormTextField(
                        value = currentForm.dosageStrength,
                        onValueChange = { onAction(AddEditMedicationAction.OnDosageStrengthChanged(it)) },
                        label = stringResource(Res.string.label_dosage_strength),
                        placeholder = stringResource(Res.string.hint_dosage),
                        error = null,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        ),
                        modifier = Modifier
                            .widthIn(max = 500.dp)
                            .fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    FormDropDown(
                        label = stringResource(Res.string.label_medication_type),
                        isMenuVisible = state.activeDropdownMenu == ActiveDropdownMenu.MEDICATION_TYPE,
                        onToggleMenu = {
                            if (state.activeDropdownMenu == ActiveDropdownMenu.MEDICATION_TYPE) {
                                onAction(AddEditMedicationAction.OnHideDropdown)
                            } else onAction(AddEditMedicationAction.OnShowMedicationTypeDropdown)
                        },
                        selectedValue = currentForm.medicationType,
                        options = MedicationType.entries,
                        onOptionSelected = { onAction(AddEditMedicationAction.OnMedicationTypeChanged(it)) },
                        displayOption = { it.toUiText().asString() },
                        placeholder = stringResource(Res.string.hint_medication_type),
                        error = currentForm.medicationTypeError,
                        modifier = Modifier
                            .widthIn(max = 500.dp)
                            .fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    FormDropDown(
                        label = stringResource(Res.string.label_frequency),
                        isMenuVisible = state.activeDropdownMenu == ActiveDropdownMenu.FREQUENCY,
                        onToggleMenu = {
                            if (state.activeDropdownMenu == ActiveDropdownMenu.FREQUENCY) {
                                onAction(AddEditMedicationAction.OnHideDropdown)
                            } else onAction(AddEditMedicationAction.OnShowFrequencyDropdown)
                        },
                        selectedValue = currentForm.frequency,
                        options = Frequency.entries,
                        onOptionSelected = {
                            onAction(AddEditMedicationAction.OnFrequencyChanged(it))
                        },
                        displayOption = { it.toUiText().asString() },
                        placeholder = stringResource(Res.string.hint_frequency),
                        error = currentForm.frequencyError,
                        modifier = Modifier
                            .widthIn(max = 500.dp)
                            .fillMaxWidth()
                    )

                    // Time Pickers
                    if (currentForm.times.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .widthIn(max = 500.dp)
                                .fillMaxWidth()
                        ) {
                            currentForm.times.forEachIndexed { index, time ->
                                Box(modifier = Modifier.weight(1f)) {
                                    TimePickerField(
                                        label = stringResource(Res.string.label_time, index + 1),
                                        displayText = time ?: "",
                                        placeholder = stringResource(Res.string.hint_select),
                                        onShowDialog = {
                                            onAction(AddEditMedicationAction.OnShowTimePicker(index))
                                        },
                                        error = currentForm.timesError,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    state.timePickerIndexToShow?.let { index ->
                                        TimePickerDialog(
                                            showDialog = state.timePickerIndexToShow,
                                            onDismiss = {
                                                onAction(AddEditMedicationAction.OnDismissTimePicker)
                                            },
                                            onTimeSelected = {
                                                onAction(AddEditMedicationAction.OnTimeSelected(index, it))
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))

                    // Date Pickers
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .widthIn(max = 500.dp)
                            .fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            DatePickerField(
                                label = stringResource(Res.string.label_start_date),
                                displayText = currentForm.startDate,
                                placeholder = stringResource(Res.string.hint_select),
                                dialogTitle = stringResource(Res.string.select_start_date),
                                showDialog = state.isStartDatePickerVisible,
                                onShowDialog = {
                                    onAction(AddEditMedicationAction.OnShowStartDatePicker)
                                },
                                onDismiss = {
                                    onAction(AddEditMedicationAction.OnDismissStartDatePicker)
                                },
                                onDateSelected = {
                                    onAction(AddEditMedicationAction.OnStartDateSelected(it))
                                },
                                error = currentForm.startDateError,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            DatePickerField(
                                label = stringResource(Res.string.label_end_date),
                                displayText = currentForm.endDate ?: "",
                                placeholder = stringResource(Res.string.hint_select),
                                dialogTitle = stringResource(Res.string.select_end_date),
                                showDialog = state.isEndDatePickerVisible,
                                onDateSelected = {
                                    onAction(AddEditMedicationAction.OnEndDateSelected(it))
                                },
                                onShowDialog = {
                                    onAction(AddEditMedicationAction.OnShowEndDatePicker)
                                },
                                onDismiss = {
                                    onAction(AddEditMedicationAction.OnDismissEndDatePicker)
                                },
                                error = currentForm.endDateError,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    
                    FormTextField(
                        value = currentForm.notes,
                        onValueChange = { onAction(AddEditMedicationAction.OnNotesChanged(it)) },
                        label = stringResource(Res.string.label_notes),
                        placeholder = stringResource(Res.string.hint_notes),
                        error = null,
                        maxLines = 4,
                        singleLine = false,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Default
                        ),
                        modifier = Modifier
                            .widthIn(max = 500.dp)
                            .fillMaxWidth()
                    )

                    // Other Information
                    Spacer(Modifier.height(24.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    )
                    Text(
                        text = stringResource(Res.string.section_title_other_info),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(10.dp))
                    FormTextField(
                        value = currentForm.hospitalName,
                        onValueChange = { onAction(AddEditMedicationAction.OnHospitalNameChanged(it)) },
                        label = stringResource(Res.string.label_hospital_name),
                        placeholder = stringResource(Res.string.hint_hospital_name),
                        error = null,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        ),
                        modifier = Modifier
                            .widthIn(max = 500.dp)
                            .fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    FormTextField(
                        value = currentForm.doctorName,
                        onValueChange = { onAction(AddEditMedicationAction.OnDoctorNameChanged(it)) },
                        label = stringResource(Res.string.label_doctor_name),
                        placeholder = stringResource(Res.string.hint_doctor_name),
                        error = null,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        ),
                        modifier = Modifier
                            .widthIn(max = 500.dp)
                            .fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    FormTextField(
                        value = currentForm.hospitalAddress,
                        onValueChange = { onAction(AddEditMedicationAction.OnHospitalAddressChanged(it)) },
                        label = stringResource(Res.string.label_hospital_address),
                        placeholder = stringResource(Res.string.hint_hospital_address),
                        error = null,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        ),
                        modifier = Modifier
                            .widthIn(max = 500.dp)
                            .fillMaxWidth()
                    )

                    // Image Upload
                    Spacer(Modifier.height(24.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    )
                    Text(
                        text = stringResource(Res.string.section_title_upload_images),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = stringResource(Res.string.upload_images_later_info),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(20.dp))

                    val uploadConfigs = listOf(
                        ImageUploadConfig(
                            imagePath = currentForm.prescriptionImagePath,
                            label = stringResource(Res.string.upload_prescription),
                            deleteLabel = stringResource(Res.string.delete_prescription),
                            contentDescription = stringResource(Res.string.btn_upload_prescription_image_description),
                            onUpload = {
                                onAction(AddEditMedicationAction.OnUploadImageClick(ImageType.PRESCRIPTION))
                                pickerLauncher.launch()
                            },
                            onDelete = {
                                onAction(AddEditMedicationAction.OnDeleteImageClick(ImageType.PRESCRIPTION))
                            }
                        ),
                        ImageUploadConfig(
                            imagePath = currentForm.medicationImagePath,
                            label = stringResource(Res.string.upload_medication),
                            deleteLabel = stringResource(Res.string.delete_medication),
                            contentDescription = stringResource(Res.string.btn_upload_medication_image_description),
                            onUpload = {
                                onAction(AddEditMedicationAction.OnUploadImageClick(ImageType.MEDICATION))
                                pickerLauncher.launch()
                            },
                            onDelete = {
                                onAction(AddEditMedicationAction.OnDeleteImageClick(ImageType.MEDICATION))
                            }
                        )
                    )
                    ImageUploadSection(uploadConfigs)

                    Spacer(Modifier.height(100.dp))
                }
            }
        }
    }
    
    if (state.isLeavingWithoutSaving) {
        ConfirmationDialog(
            config = ConfirmationDialogConfig(
                title = stringResource(Res.string.leave_form_title),
                message = stringResource(Res.string.leave_form_message),
                confirmButtonText = stringResource(Res.string.leave),
                cancelButtonText = stringResource(Res.string.cancel),
                confirmButtonColor = MaterialTheme.colorScheme.secondary
            ),
            onConfirm = {
                onAction(AddEditMedicationAction.OnConfirmLeaveWithoutSaving)
            },
            onDismiss = {
                onAction(AddEditMedicationAction.OnDismissLeaveWithoutSaving)
            }
        )
    }
    
    if (state.shouldShowRationale) {
        val itemPhrase = pluralStringResource(Res.plurals.this_medication, state.medicationForms.size)
        
        ConfirmationDialog(
            config = ConfirmationDialogConfig(
                title = stringResource(Res.string.reminders_disabled_title),
                message = stringResource(Res.string.reminders_disabled_message, itemPhrase),
                confirmButtonText = stringResource(Res.string.save_anyway),
                cancelButtonText = stringResource(Res.string.cancel),
                confirmButtonColor = MaterialTheme.colorScheme.secondary
            ),
            onConfirm = {
                onAction(AddEditMedicationAction.OnConfirmSaveWithoutNotifications)
            },
            onDismiss = {
                onAction(AddEditMedicationAction.OnDismissSaveWithoutNotifications)
            }
        )
    }

    if (state.isAnalyzingPrescription) {
        val analyzingTexts = listOf(
            stringResource(Res.string.ai_prescription_scan_message_1),
            stringResource(Res.string.ai_prescription_scan_message_2),
            stringResource(Res.string.ai_prescription_scan_message_3),
            stringResource(Res.string.ai_prescription_scan_message_4)
        )
        AiLoadingDialog(analyzingTexts)
    }
}

@Preview(showBackground = true)
@Preview(
    name = "997dp x 393dp",
    showBackground = true,
    widthDp = 997,
    heightDp = 393
)
@Preview(
    name = "600dp x 960dp",
    showBackground = true,
    widthDp = 600,
    heightDp = 960
)
@Preview(
    name = "1280dp x 800dp",
    showBackground = true,
    widthDp = 1280,
    heightDp = 800
)
@Composable
fun AddEditMedicationScreenPreview() {
    MedRemindTheme {
        val state = AddEditMedicationState()

        AddEditMedicationScreen(
            state = state,
            snackbarState = SnackbarHostState(),
            onAction = {}
        )
    }
}
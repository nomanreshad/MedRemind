@file:OptIn(ExperimentalMaterial3Api::class)

package com.nomanhassan.medremind.presentation.medication_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nomanhassan.medremind.core.presentation.UiText
import com.nomanhassan.medremind.core.presentation.components.ConfirmationDialog
import com.nomanhassan.medremind.core.presentation.components.ConfirmationDialogConfig
import com.nomanhassan.medremind.core.presentation.components.FloatingToolbarDefaults
import com.nomanhassan.medremind.core.presentation.components.HorizontalFloatingToolbar
import com.nomanhassan.medremind.domain.model.Medication
import com.nomanhassan.medremind.presentation.medication_detail.components.ImageTabContent
import com.nomanhassan.medremind.presentation.medication_detail.components.InfoTabContent
import com.nomanhassan.medremind.presentation.medication_list.MedicationItem
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.btn_back_description
import medremind.composeapp.generated.resources.btn_cancel
import medremind.composeapp.generated.resources.btn_delete
import medremind.composeapp.generated.resources.btn_delete_medication_description
import medremind.composeapp.generated.resources.btn_edit_medication_description
import medremind.composeapp.generated.resources.delete_medication_dialog_message
import medremind.composeapp.generated.resources.delete_medication_dialog_title
import medremind.composeapp.generated.resources.details_tab_image
import medremind.composeapp.generated.resources.details_tab_info
import medremind.composeapp.generated.resources.icon_outline_edit
import medremind.composeapp.generated.resources.icon_outline_rounded_delete
import medremind.composeapp.generated.resources.icon_rounded_arrow_go_back
import medremind.composeapp.generated.resources.image_tab_medication
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MedicationDetailScreenRoot(
    viewModel: MedicationDetailViewModel = koinViewModel(),
    onClickEditMedication: (Int?) -> Unit,
    onBackClicked: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    LaunchedEffect(state.isMedicationDeletedSuccessfully) {
        if (state.isMedicationDeletedSuccessfully) {
            onBackClicked()
        }
    }
    
    MedicationDetailScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is MedicationDetailAction.OnEditMedicationClick -> onClickEditMedication(action.id)
                MedicationDetailAction.OnClickGoBack -> onBackClicked()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
private fun MedicationDetailScreen(
    state: MedicationDetailState,
    onAction: (MedicationDetailAction) -> Unit
) {
    val pagerState = rememberPagerState { 2 }

    LaunchedEffect(state.selectedTabIndex) {
        pagerState.animateScrollToPage(state.selectedTabIndex)
    }
    
    LaunchedEffect(pagerState.currentPage) {
        onAction(MedicationDetailAction.OnTabSelected(pagerState.currentPage))
    }

    var expanded by rememberSaveable { mutableStateOf(true) }
    val vibrantColors = FloatingToolbarDefaults.standardFloatingToolbarColors(
        toolbarContainerColor = MaterialTheme.colorScheme.primary,
        toolbarContentColor = MaterialTheme.colorScheme.onPrimary,
    )
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "${state.medicationItem?.medication?.medicineName ?: ""} ${state.medicationItem?.medication?.dosageStrength ?: ""}",
                        color = MaterialTheme.colorScheme.onPrimary,
                        maxLines = 1,
                        modifier = Modifier
                            .basicMarquee(iterations = Int.MAX_VALUE)
                    )
                },
                navigationIcon = {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                        tooltip = {
                            PlainTooltip {
                                Text(
                                    text = stringResource(Res.string.btn_back_description),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(
                            onClick = { onAction(MedicationDetailAction.OnClickGoBack) }
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.icon_rounded_arrow_go_back),
                                contentDescription = stringResource(Res.string.btn_back_description),
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.primary
                        )
                    )
                )
            )
        },
        floatingActionButton = {
            HorizontalFloatingToolbar(
                expanded = true,
                expandedShadowElevation = 2.dp,
                colors = vibrantColors,
            ) {
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                    tooltip = {
                        PlainTooltip {
                            Text(
                                text = stringResource(Res.string.btn_delete_medication_description),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    },
                    state = rememberTooltipState(),
                ) {
                    IconButton(
                        onClick = {
                            onAction(MedicationDetailAction.OnDeleteMedicationClick)
                        }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.icon_outline_rounded_delete),
                            contentDescription = stringResource(Res.string.btn_delete_medication_description),
                            tint = MaterialTheme.colorScheme.onError
                        )
                    }
                }
                
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                    tooltip = {
                        PlainTooltip {
                            Text(
                                text = stringResource(Res.string.btn_edit_medication_description),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    },
                    state = rememberTooltipState(),
                ) {
                    IconButton(
                        onClick = {
                            onAction(MedicationDetailAction.OnEditMedicationClick(state.medicationItem?.medication?.medicationId!!))
                        }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.icon_outline_edit),
                            contentDescription = stringResource(Res.string.btn_edit_medication_description),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        containerColor = MaterialTheme.colorScheme.primary
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PrimaryTabRow(
                    selectedTabIndex = state.selectedTabIndex,
                    modifier = Modifier
                        .widthIn(700.dp)
                        .fillMaxWidth(),
                ) {
                    Tab(
                        selected = state.selectedTabIndex == 0,
                        onClick = {
                            onAction(MedicationDetailAction.OnTabSelected(0))
                        },
                        modifier = Modifier.weight(1f),
                        text = {
                            Text(
                                text = stringResource(Res.string.details_tab_info),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                    
                    Tab(
                        selected = state.selectedTabIndex == 1,
                        onClick = {
                            onAction(MedicationDetailAction.OnTabSelected(1))
                        },
                        modifier = Modifier.weight(1f),
                        text = {
                            Text(
                                text = stringResource(Res.string.details_tab_image),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { pageIndex ->
                    when (pageIndex) {
                        0 -> {
                            if (state.isLoading) {
//                                Box(
//                                    modifier = Modifier.fillMaxSize(),
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    CircularProgressIndicator()
//                                }
                            } else if (state.errorMessage != null) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = state.errorMessage.asString()
                                    )
                                }
                            } else {
                                state.medicationItem?.let { medicationItem ->
                                    InfoTabContent(medicationItem)
                                }
                            }
                        }
                        
                        1 -> {
                            if (state.isLoading) {
//                                Box(
//                                    modifier = Modifier.fillMaxSize(),
//                                    contentAlignment = Alignment.Center
//                                ) {
//                                    CircularProgressIndicator()
//                                }
                            } else if (state.errorMessage != null) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = state.errorMessage.asString()
                                    )
                                }
                            } else {
                                state.medicationItem?.medication?.let { medication ->
                                    ImageTabContent(
                                        prescriptionImagePath = medication.prescriptionImagePath,
                                        medicationImagePath = medication.medicationImagePath,
                                        selectedImageType = state.selectedImageType,
                                        onTypeSelected = {
                                            onAction(MedicationDetailAction.OnImageTypeSelected(it))
                                        },
                                        offset = state.imageOffset,
                                        scale = state.imageScale,
                                        onImageTransformChanged = { offset, scale, containerSize ->
                                            onAction(MedicationDetailAction.OnImageTransformChanged(offset, scale, containerSize))
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (state.showDeleteMedicationDialog) {
        ConfirmationDialog(
            config = ConfirmationDialogConfig(
                title = stringResource(Res.string.delete_medication_dialog_title, state.medicationItem?.medication?.medicineName ?: Res.string.image_tab_medication),
                message = pluralStringResource(Res.plurals.delete_medication_dialog_message, 1),
                confirmButtonText = stringResource(Res.string.btn_delete),
                cancelButtonText = stringResource(Res.string.btn_cancel),
            ),
            onConfirm = {
                onAction(MedicationDetailAction.ConfirmDeleteMedication)
            },
            onDismiss = {
                onAction(MedicationDetailAction.DismissDeleteMedicationDialog)
            }
        )
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
private fun MedicationDetailScreenPrev() {
    MaterialTheme {
        MedicationDetailScreen(
            state = MedicationDetailState(
                medicationItem = MedicationItem(
                    medication = Medication(
                        medicationId = 1,
                        medicineName = "Aspirin",
                        dosageStrength = "500 mg",
                        medicationType = "Tablet",
                        frequency = "Thrice Daily",
                        formattedTimes = listOf("9:00 AM", "2:00 PM", "9:00 PM"),
                        formattedStartDate = "2023-08-01",
                        formattedEndDate = "2023-09-30",
                        notes = null,
                        hospitalName = "ABC Hospital",
                        doctorName = "Dr. Smith",
                        hospitalAddress = null,
                        prescriptionImagePath = null,
                        medicationImagePath = null,
                        isActive = true
                    ),
                    formattedFrequency = UiText.DynamicString("Thrice Daily")
                ),
                selectedTabIndex = 1
            ),
            onAction = {}
        )
    }
}
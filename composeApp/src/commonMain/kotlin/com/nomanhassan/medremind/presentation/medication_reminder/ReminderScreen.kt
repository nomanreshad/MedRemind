@file:OptIn(ExperimentalMaterial3Api::class)

package com.nomanhassan.medremind.presentation.medication_reminder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.nomanhassan.medremind.app.ui.theme.MedRemindTheme
import com.nomanhassan.medremind.core.presentation.components.Header
import com.nomanhassan.medremind.core.util.toLocalizedDigits
import com.nomanhassan.medremind.domain.model.Medication
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.btn_back_to_home_description
import medremind.composeapp.generated.resources.carbonimage
import medremind.composeapp.generated.resources.failed_to_load
import medremind.composeapp.generated.resources.icon_filled_gallery_upload
import medremind.composeapp.generated.resources.icon_outline_rounded_notifications_active
import medremind.composeapp.generated.resources.icon_reminder_bell_description
import medremind.composeapp.generated.resources.image_tab_medication_description
import medremind.composeapp.generated.resources.no_photo_available
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ReminderScreenRoot(
    onGoBack: () -> Unit,
    viewModel: ReminderViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isAcknowledged) {
        if (state.isAcknowledged) onGoBack()
    }

    ReminderScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun ReminderScreen(
    state: ReminderState,
    onAction: (ReminderAction) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.icon_outline_rounded_notifications_active),
                            contentDescription = stringResource(Res.string.icon_reminder_bell_description),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(state.reminderTime.toLocalizedDigits())
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
        }
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
            Box(Modifier.fillMaxSize()) {
                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    state.errorMessage != null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = state.errorMessage.asString(),
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
                        ) {
                            items(
                                items = state.dueMedications,
                                key = { it.medicationId }
                            ) { medication ->
                                MedicationReminderCard(
                                    medication = medication,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = { onAction(ReminderAction.OnAcknowledgeReminder) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.BottomEnd),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = stringResource(Res.string.btn_back_to_home_description),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MedicationReminderCard(
    medication: Medication,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .widthIn(max = 500.dp)
            .fillMaxWidth()
    ) {
        Header(
            label = "${medication.medicineName} ${medication.dosageStrength ?: ""}",
            textStyle = MaterialTheme.typography.titleLarge
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 4.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!medication.medicationImagePath.isNullOrBlank()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalPlatformContext.current)
                                .data(medication.medicationImagePath)
                                .crossfade(true)
                                .build(),
                            contentDescription = stringResource(Res.string.image_tab_medication_description),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            loading = {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            error = {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.icon_filled_gallery_upload),
                                        contentDescription = stringResource(Res.string.failed_to_load),
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(30.dp)
                                    )
                                    Text(
                                        text = stringResource(Res.string.failed_to_load),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.carbonimage),
                            contentDescription = stringResource(Res.string.no_photo_available),
                            modifier = Modifier.size(48.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(Res.string.no_photo_available),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReminderScreenPreview() {
    MedRemindTheme {
        val state = ReminderState(
            isLoading = false,
            reminderTime = "8:00 AM",
            dueMedications = listOf(
                Medication(
                    medicationId = 1,
                    medicineName = "Aspirin",
                    dosageStrength = "100mg",
                    medicationType = "",
                    frequency = "",
                    formattedTimes = listOf(),
                    formattedStartDate = "",
                    formattedEndDate = "",
                    notes = "",
                    hospitalName = "",
                    doctorName = "",
                    hospitalAddress = "",
                    prescriptionImagePath = "",
                    medicationImagePath = "",
                    isActive = true,
                ),
                Medication(
                    medicationId = 2,
                    medicineName = "Paracetamol",
                    dosageStrength = "500mg",
                    medicationType = "",
                    frequency = "",
                    formattedTimes = listOf(),
                    formattedStartDate = "",
                    formattedEndDate = "",
                    notes = "",
                    hospitalName = "",
                    doctorName = "",
                    hospitalAddress = "",
                    prescriptionImagePath = "",
                    medicationImagePath = "",
                    isActive = true,
                ),
                Medication(
                    medicationId = 3,
                    medicineName = "Paracetamol",
                    dosageStrength = "500mg",
                    medicationType = "",
                    frequency = "",
                    formattedTimes = listOf(),
                    formattedStartDate = "",
                    formattedEndDate = "",
                    notes = "",
                    hospitalName = "",
                    doctorName = "",
                    hospitalAddress = "",
                    prescriptionImagePath = "",
                    medicationImagePath = "",
                    isActive = true,
                ),
                Medication(
                    medicationId = 4,
                    medicineName = "Paracetamol",
                    dosageStrength = "500mg",
                    medicationType = "",
                    frequency = "",
                    formattedTimes = listOf(),
                    formattedStartDate = "",
                    formattedEndDate = "",
                    notes = "",
                    hospitalName = "",
                    doctorName = "",
                    hospitalAddress = "",
                    prescriptionImagePath = "",
                    medicationImagePath = "",
                    isActive = true,
                ),
            )
        )
        ReminderScreen(
            state = state,
            onAction = {}
        )
    }
}
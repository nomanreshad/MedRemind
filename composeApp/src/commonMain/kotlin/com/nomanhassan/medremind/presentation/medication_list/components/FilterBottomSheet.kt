@file:OptIn(ExperimentalMaterial3Api::class)

package com.nomanhassan.medremind.presentation.medication_list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nomanhassan.medremind.app.ui.theme.MedRemindTheme
import com.nomanhassan.medremind.core.enums.TimeSlot
import com.nomanhassan.medremind.core.presentation.toUiText
import medremind.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.stringResource
import medremind.composeapp.generated.resources.filter_frequency_and_type
import medremind.composeapp.generated.resources.filter_medications
import medremind.composeapp.generated.resources.filter_time_of_day
import medremind.composeapp.generated.resources.filter_view
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FilterBottomSheet(
    selectedTimeSlot: TimeSlot,
    onDismiss: () -> Unit,
    onSelect: (TimeSlot) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        sheetMaxWidth = 500.dp,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(Res.string.filter_medications),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(24.dp))

            FilterSection(
                title = stringResource(Res.string.filter_view),
                options = listOf(TimeSlot.ALL),
                selectedSlot = selectedTimeSlot,
                onSelect = onSelect
            )
            Spacer(modifier = Modifier.height(24.dp))

            FilterSection(
                title = stringResource(Res.string.filter_time_of_day),
                options = listOf(
                    TimeSlot.MORNING,
                    TimeSlot.NOON_AFTERNOON,
                    TimeSlot.EVENING,
                    TimeSlot.NIGHT
                ),
                selectedSlot = selectedTimeSlot,
                onSelect = onSelect
            )
            Spacer(modifier = Modifier.height(24.dp))

            FilterSection(
                title = stringResource(Res.string.filter_frequency_and_type),
                options = listOf(
                    TimeSlot.AS_NEEDED,
                    TimeSlot.WEEKLY,
                    TimeSlot.MONTHLY,
                    TimeSlot.EVERY_4_HOURS,
                    TimeSlot.EVERY_6_HOURS
                ),
                selectedSlot = selectedTimeSlot,
                onSelect = onSelect
            )
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    options: List<TimeSlot>,
    selectedSlot: TimeSlot,
    onSelect: (TimeSlot) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { slot ->
                FilterChip(
                    selected = slot == selectedSlot,
                    onClick = { onSelect(slot) },
                    label = {
                        Text(
                            text = slot.toUiText().asString(),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun FilterBottomSheetPrev() {
    MedRemindTheme {
        FilterBottomSheet(
            selectedTimeSlot = TimeSlot.ALL,
            onDismiss = {},
            onSelect = {}
        )
    }
}
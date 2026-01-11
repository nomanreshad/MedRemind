package com.nomanhassan.medremind.presentation.add_edit_medication.components

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.btn_back_description
import medremind.composeapp.generated.resources.btn_cancel
import medremind.composeapp.generated.resources.btn_delete_medication_description
import medremind.composeapp.generated.resources.btn_next_medication_description
import medremind.composeapp.generated.resources.btn_previous_medication_description
import medremind.composeapp.generated.resources.icon_outline_rounded_delete
import medremind.composeapp.generated.resources.icon_rounded_arrow_go_back
import medremind.composeapp.generated.resources.icon_rounded_go_backward
import medremind.composeapp.generated.resources.icon_rounded_go_forward
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMedicationTopAppBar(
    title: String,
    medicationCount: Int,
    currentMedicationIndex: Int,
    onBackClicked: () -> Unit,
    onPreviousClicked: () -> Unit,
    onNextClicked: () -> Unit,
    onRemoveClicked: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimary
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

                IconButton(onClick = onBackClicked) {
                    Icon(
                        painter = painterResource(Res.drawable.icon_rounded_arrow_go_back),
                        contentDescription = stringResource(Res.string.btn_back_description),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        actions = {
            if (medicationCount > 1) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onPreviousClicked,
                        enabled = currentMedicationIndex > 0,
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.icon_rounded_go_backward),
                            contentDescription = stringResource(Res.string.btn_previous_medication_description),
                            tint = if (currentMedicationIndex > 0) {
                                MaterialTheme.colorScheme.onPrimary
                            } else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Text(
                        text = "${currentMedicationIndex + 1}/$medicationCount",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    IconButton(
                        onClick = onNextClicked,
                        enabled = currentMedicationIndex < medicationCount - 1
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.icon_rounded_go_forward),
                            contentDescription = stringResource(Res.string.btn_next_medication_description),
                            tint = if (currentMedicationIndex < medicationCount - 1) {
                                MaterialTheme.colorScheme.onPrimary
                            } else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    
                    IconButton(onClick = onRemoveClicked) {
                        Icon(
                            painter = painterResource(Res.drawable.icon_outline_rounded_delete),
                            contentDescription = stringResource(Res.string.btn_delete_medication_description),
                            tint = MaterialTheme.colorScheme.errorContainer
                        )
                    }
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
}
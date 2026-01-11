@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)

package com.nomanhassan.medremind.presentation.add_edit_medication.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.unit.dp
import com.nomanhassan.medremind.core.presentation.UiText
import com.nomanhassan.medremind.core.util.DateTimeFormatterUtil
import com.nomanhassan.medremind.core.util.toLocalizedDigits
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.btn_cancel
import medremind.composeapp.generated.resources.btn_dismiss
import medremind.composeapp.generated.resources.btn_ok
import org.jetbrains.compose.resources.stringResource
import kotlin.time.ExperimentalTime

@Composable
fun TimePickerField(
    label: String,
    displayText: String,
    placeholder: String,
    onShowDialog: () -> Unit,
    error: UiText?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = displayText,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text(text = placeholder) },
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(true) {
                    awaitEachGesture {
                        // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                        // in the Initial pass to observe events before the text field consumes them
                        // in the Main pass.
                        awaitFirstDown(pass = PointerEventPass.Initial)
                        val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        if (upEvent != null) {
                            onShowDialog()
                        }
                    }
                },
            isError = error != null,
            supportingText = {
                error?.let {
                    Text(
                        text = it.asString(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        )
    }
}

@Composable
fun TimePickerDialog(
    showDialog: Int?,
    onDismiss: () -> Unit,
    onTimeSelected: (Long) -> Unit
) {
    val currentTime = DateTimeFormatterUtil.getCurrentTime()
    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.hour,
        initialMinute = currentTime.minute,
        is24Hour = false
    )
    
    showDialog?.let {
        TimePickerDialog(
            onDismissRequest = onDismiss,
            title = {},
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedEpochMillis = DateTimeFormatterUtil.getEpochMillisForTimeToday(
                            hour = timePickerState.hour,
                            minute = timePickerState.minute
                        )
                        onTimeSelected(selectedEpochMillis)
                    }
                ) {
                    Text(text = stringResource(Res.string.btn_ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onDismiss() }
                ) {
                    Text(text = stringResource(Res.string.btn_cancel))
                }
            }
        ) {
            TimePicker(
                state = timePickerState
            )
        }
    }
}

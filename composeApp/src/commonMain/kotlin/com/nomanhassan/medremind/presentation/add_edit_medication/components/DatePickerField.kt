package com.nomanhassan.medremind.presentation.add_edit_medication.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.nomanhassan.medremind.core.presentation.UiText
import com.nomanhassan.medremind.core.util.DateTimeFormatterUtil
import com.nomanhassan.medremind.core.util.toLocalizedDigits
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.btn_cancel
import medremind.composeapp.generated.resources.btn_ok
import medremind.composeapp.generated.resources.selected_date
import org.jetbrains.compose.resources.stringResource

@Composable
fun DatePickerField(
    label: String,
    displayText: String,
    placeholder: String,
    dialogTitle: String,
    showDialog: Boolean,
    onShowDialog: () -> Unit,
    onDismiss: () -> Unit,
    onDateSelected: (Long) -> Unit,
    error: UiText?,
    modifier: Modifier = Modifier
) {
    val datePickerState = rememberDatePickerState()
    val selectedDateMillis = datePickerState.selectedDateMillis
        ?: DateTimeFormatterUtil.getCurrentDateMidnightMillis()

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

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    onDateSelected(selectedDateMillis)
                    onDismiss()
                }) {
                    Text(text = stringResource(Res.string.btn_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(Res.string.btn_cancel))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        text = dialogTitle,
                        style = MaterialTheme.typography.labelLarge,
                        color = DatePickerDefaults.colors().titleContentColor,
                        modifier = Modifier
                            .padding(PaddingValues(start = 24.dp, end = 12.dp, top = 16.dp))
                    )
                },
                headline = {
                    Text(
                        text = datePickerState.selectedDateMillis?.let {
                            DateTimeFormatterUtil.formatDate(it)
                        } ?: stringResource(Res.string.selected_date),
                        style = MaterialTheme.typography.headlineLarge,
                        color = DatePickerDefaults.colors().titleContentColor,
                        modifier = Modifier
                            .padding(PaddingValues(start = 24.dp, end = 12.dp, bottom = 12.dp))
                    )
                },
                showModeToggle = false
            )
        }
    }
}
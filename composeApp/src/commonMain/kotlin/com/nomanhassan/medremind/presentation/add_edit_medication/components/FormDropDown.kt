@file:OptIn(ExperimentalMaterial3Api::class)

package com.nomanhassan.medremind.presentation.add_edit_medication.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.nomanhassan.medremind.core.presentation.UiText
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.icon_filled_rounded_arrow_drop_down
import medremind.composeapp.generated.resources.icon_rounded_check
import medremind.composeapp.generated.resources.icon_toggle_dropdown_menu_description
import medremind.composeapp.generated.resources.selected
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun <T> FormDropDown(
    label: String,
    isMenuVisible: Boolean,
    onToggleMenu: () -> Unit,
    selectedValue: T?,
    options: List<T>,
    onOptionSelected: (T) -> Unit,
    displayOption: @Composable (T) -> String,
    placeholder: String,
    error: UiText?,
    modifier: Modifier = Modifier
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isMenuVisible) 180f else 0f,
        label = "dropdownIconRotation"
    )

    ExposedDropdownMenuBox(
        expanded = isMenuVisible,
        onExpandedChange = { onToggleMenu() },
        modifier = modifier
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            OutlinedTextField(
                value = selectedValue?.let { displayOption(it) } ?: "",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(text = placeholder) },
                trailingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.icon_filled_rounded_arrow_drop_down),
                        contentDescription = stringResource(Res.string.icon_toggle_dropdown_menu_description),
                        modifier = Modifier
                            .graphicsLayer { rotationZ = rotationAngle }
                    )
                },
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                    .fillMaxWidth()
                    .clickable { onToggleMenu() },
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
            
            ExposedDropdownMenu(
                expanded = isMenuVisible,
                onDismissRequest = { onToggleMenu() },
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.width(IntrinsicSize.Max)
            ) {
                options.forEach { option ->
                    val isSelected = (option == selectedValue)
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = displayOption(option),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(end = 24.dp)
                            )
                        },
                        onClick = {
                            onOptionSelected(option)
                            onToggleMenu()
                        },
                        trailingIcon = {
                            if (isSelected) {
                                Icon(
                                    painter = painterResource(Res.drawable.icon_rounded_check),
                                    contentDescription = stringResource(Res.string.selected),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

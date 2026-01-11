package com.nomanhassan.medremind.presentation.medication_detail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.btn_add_medication_description
import medremind.composeapp.generated.resources.btn_delete_medication_description
import medremind.composeapp.generated.resources.btn_edit_medication_description
import medremind.composeapp.generated.resources.icon_outline_edit
import medremind.composeapp.generated.resources.icon_outline_more_vert
import medremind.composeapp.generated.resources.icon_outline_rounded_delete
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun MedicationOptionsMenu(
    isMenuVisible: Boolean,
    onToggleOptionsMenu: () -> Unit,
    onClickEditMedication: () -> Unit,
    onClickDeleteMedication: () -> Unit
) {
    Box {
        IconButton(onClick = onToggleOptionsMenu) {
            Icon(
                painter = painterResource(Res.drawable.icon_outline_more_vert),
                contentDescription = stringResource(Res.string.btn_add_medication_description),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        DropdownMenu(
            expanded = isMenuVisible,
            onDismissRequest = onToggleOptionsMenu
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(Res.string.btn_edit_medication_description),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                onClick = {
                    onToggleOptionsMenu()
                    onClickEditMedication()
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.icon_outline_edit),
                        contentDescription = stringResource(Res.string.btn_edit_medication_description),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(Res.string.btn_delete_medication_description),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                onClick = {
                    onToggleOptionsMenu()
                    onClickDeleteMedication()
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.icon_outline_rounded_delete),
                        contentDescription = stringResource(Res.string.btn_delete_medication_description),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            )
        }
    }
}
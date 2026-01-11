package com.nomanhassan.medremind.presentation.medication_list.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.btn_add_medication_description
import medremind.composeapp.generated.resources.btn_enter_medication_details_description
import medremind.composeapp.generated.resources.btn_upload_prescription_image_description
import medremind.composeapp.generated.resources.enter_details
import medremind.composeapp.generated.resources.icon_filled_add
import medremind.composeapp.generated.resources.icon_filled_edit
import medremind.composeapp.generated.resources.icon_filled_gallery_upload
import medremind.composeapp.generated.resources.upload_prescription
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AddMedicationMenu(
    isMenuVisible: Boolean,
    onToggleAddMedicationMenu: () -> Unit,
    onClickUploadPrescription: () -> Unit,
    onClickEnterMedicationDetails: () -> Unit
) {
    Box {
        IconButton(onClick = onToggleAddMedicationMenu) {
            Icon(
                painter = painterResource(Res.drawable.icon_filled_add),
                contentDescription = stringResource(Res.string.btn_add_medication_description),
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(28.dp)
            )
        }

        DropdownMenu(
            expanded = isMenuVisible,
            onDismissRequest = onToggleAddMedicationMenu
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(Res.string.upload_prescription),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                onClick = {
                    onToggleAddMedicationMenu()
                    onClickUploadPrescription()
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.icon_filled_gallery_upload),
                        contentDescription = stringResource(Res.string.btn_upload_prescription_image_description),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(Res.string.enter_details),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                onClick = {
                    onToggleAddMedicationMenu()
                    onClickEnterMedicationDetails()
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.icon_filled_edit),
                        contentDescription = stringResource(Res.string.btn_enter_medication_details_description),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            )
        }
    }
}
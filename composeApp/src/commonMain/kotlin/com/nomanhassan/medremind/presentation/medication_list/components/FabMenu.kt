package com.nomanhassan.medremind.presentation.medication_list.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.btn_enter_medication_details_description
import medremind.composeapp.generated.resources.btn_take_prescription_photo_description
import medremind.composeapp.generated.resources.btn_upload_prescription_image_description
import medremind.composeapp.generated.resources.enter_details
import medremind.composeapp.generated.resources.fab_close_medication_menu_description
import medremind.composeapp.generated.resources.fab_expand_medication_menu_description
import medremind.composeapp.generated.resources.icon_close
import medremind.composeapp.generated.resources.icon_filled_add
import medremind.composeapp.generated.resources.icon_filled_camera
import medremind.composeapp.generated.resources.icon_filled_edit
import medremind.composeapp.generated.resources.icon_filled_gallery_upload
import medremind.composeapp.generated.resources.take_prescription_photo
import medremind.composeapp.generated.resources.upload_prescription
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.noman.medremind.core.components.FloatingActionButtonMenu
import org.noman.medremind.core.components.FloatingActionButtonMenuItem
import org.noman.medremind.core.components.ToggleFloatingActionButton
import org.noman.medremind.core.components.ToggleFloatingActionButtonDefaults.animateIcon

@Composable
fun FabMenu(
    isFabMenuExpanded: Boolean,
    onToggleFabMenu: (Boolean) -> Unit,
    onClickTakePrescriptionPhoto: () -> Unit,
    onClickUploadPrescription: () -> Unit,
    onClickEnterMedicationDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButtonMenu(
        expanded = isFabMenuExpanded,
        button = {
            ToggleFloatingActionButton(
                checked = isFabMenuExpanded,
                onCheckedChange = onToggleFabMenu
            ) {
                Icon(
                    painter = if (checkedProgress > 0.5f) {
                        painterResource(Res.drawable.icon_close) 
                    } else painterResource(Res.drawable.icon_filled_add),
                    contentDescription = if (checkedProgress > 0.5f) {
                        stringResource(Res.string.fab_close_medication_menu_description)
                    } else stringResource(Res.string.fab_expand_medication_menu_description),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.animateIcon(checkedProgress = { checkedProgress })
                )
            }
        },
        modifier = modifier
    ) {
        FloatingActionButtonMenuItem(
            onClick = {
                onClickTakePrescriptionPhoto()
                onToggleFabMenu(false)
            },
            text = {
                Text(
                    text = stringResource(Res.string.take_prescription_photo),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.icon_filled_camera),
                    contentDescription = stringResource(Res.string.btn_take_prescription_photo_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
        
        FloatingActionButtonMenuItem(
            onClick = {
                onClickUploadPrescription()
                onToggleFabMenu(false)
            },
            text = {
                Text(
                    text = stringResource(Res.string.upload_prescription),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.icon_filled_gallery_upload),
                    contentDescription = stringResource(Res.string.btn_upload_prescription_image_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
        
        FloatingActionButtonMenuItem(
            onClick = {
                onClickEnterMedicationDetails()
                onToggleFabMenu(false)
            },
            text = {
                Text(
                    text = stringResource(Res.string.enter_details),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.icon_filled_edit),
                    contentDescription = stringResource(Res.string.btn_enter_medication_details_description),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    }
}
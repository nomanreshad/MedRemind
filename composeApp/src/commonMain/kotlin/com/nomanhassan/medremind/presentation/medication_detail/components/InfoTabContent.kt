package com.nomanhassan.medremind.presentation.medication_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nomanhassan.medremind.core.util.toLocalizedDigits
import com.nomanhassan.medremind.core.util.toLocalizedDateString
import com.nomanhassan.medremind.presentation.medication_list.MedicationItem
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.details_label_doctor_name
import medremind.composeapp.generated.resources.details_label_dosage
import medremind.composeapp.generated.resources.details_label_end_date
import medremind.composeapp.generated.resources.details_label_frequency
import medremind.composeapp.generated.resources.details_label_hospital_address
import medremind.composeapp.generated.resources.details_label_hospital_name
import medremind.composeapp.generated.resources.details_label_name
import medremind.composeapp.generated.resources.details_label_start_date
import medremind.composeapp.generated.resources.details_label_type
import medremind.composeapp.generated.resources.details_value_not_set
import medremind.composeapp.generated.resources.label_main_details
import medremind.composeapp.generated.resources.section_title_other_info
import org.jetbrains.compose.resources.stringResource

@Composable
fun InfoTabContent(
    medicationItem: MedicationItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 20.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        val notSetText = stringResource(Res.string.details_value_not_set)
        
        DetailInfoCard(
            label = stringResource(Res.string.label_main_details),
            items = listOf(
                DetailItem(
                    label = stringResource(Res.string.details_label_name),
                    value = medicationItem.medication.medicineName
                ),
                DetailItem(
                    label = stringResource(Res.string.details_label_dosage),
                    value = medicationItem.medication.dosageStrength.takeIf { !it.isNullOrBlank() } ?: notSetText
                ),
                DetailItem(
                    label = stringResource(Res.string.details_label_frequency),
                    value = if (medicationItem.medication.formattedTimes.isNotEmpty()) {
                        "${medicationItem.formattedFrequency.asString()} (${medicationItem.medication.formattedTimes.joinToString(", ").toLocalizedDigits()})"
                    } else medicationItem.formattedFrequency.asString()
                ),
                DetailItem(
                    label = stringResource(Res.string.details_label_start_date),
                    value = medicationItem.medication.formattedStartDate.toLocalizedDateString()
                ),
                DetailItem(
                    label = stringResource(Res.string.details_label_end_date),
                    value = medicationItem.medication.formattedEndDate?.toLocalizedDateString() ?: notSetText
                ),
                DetailItem(
                    label = stringResource(Res.string.details_label_type),
                    value = medicationItem.formattedType?.asString() ?: notSetText
                )
            )
        )
        
        NotesCard(
            notes = medicationItem.medication.notes.takeIf { !it.isNullOrBlank() } ?: notSetText
        )
        
        DetailInfoCard(
            label = stringResource(Res.string.section_title_other_info),
            items = listOf(
                DetailItem(
                    label = stringResource(Res.string.details_label_hospital_name),
                    value = medicationItem.medication.hospitalName?.takeIf { it.isNotBlank() } ?: notSetText
                ),
                DetailItem(
                    label = stringResource(Res.string.details_label_doctor_name),
                    value = medicationItem.medication.doctorName?.takeIf { it.isNotBlank() } ?: notSetText
                ),
                DetailItem(
                    label = stringResource(Res.string.details_label_hospital_address),
                    value = medicationItem.medication.hospitalAddress?.takeIf { it.isNotBlank() } ?: notSetText
                )
            )
        )

        Spacer(Modifier.height(50.dp))
    }
}
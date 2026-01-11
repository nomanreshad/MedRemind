@file:OptIn(ExperimentalTime::class)

package com.nomanhassan.medremind.presentation.medication_list.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nomanhassan.medremind.core.enums.Frequency
import com.nomanhassan.medremind.core.presentation.UiText
import com.nomanhassan.medremind.core.util.toLocalizedDigits
import com.nomanhassan.medremind.domain.model.Medication
import com.nomanhassan.medremind.domain.settings.Language
import com.nomanhassan.medremind.domain.settings.LocalizedLanguage
import com.nomanhassan.medremind.presentation.medication_list.MedicationItem
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.icon_rounded_check
import medremind.composeapp.generated.resources.next_dose_after
import medremind.composeapp.generated.resources.next_dose_in
import medremind.composeapp.generated.resources.less_then_a_minute
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.ExperimentalTime

@Composable
fun MedicationListItem(
    medicationItem: MedicationItem,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onReminderToggled: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(durationMillis = 300)
    )
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    Card(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .widthIn(max = 500.dp)
            .fillMaxWidth()
            .alpha(if (medicationItem.medication.isActive) 1f else 0.7f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                append(medicationItem.medication.medicineName)
                            }

                            append(" ")

                            if (!medicationItem.medication.dosageStrength.isNullOrBlank()) {
                                withStyle(
                                    style = SpanStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                ) {
                                    append(medicationItem.medication.dosageStrength)
                                }
                            }
                        },
                        maxLines = 1,
                        modifier = Modifier
                            .basicMarquee(iterations = Int.MAX_VALUE)
                    )
                    Spacer(Modifier.height(8.dp))

                    MedicationTimeRow(
                        formattedTimes = medicationItem.medication.formattedTimes,
                        nextDoseTime = medicationItem.nextDoseTime,
                        frequency = Frequency.fromName(medicationItem.medication.frequency),
                        isActive = medicationItem.medication.isActive
                    )                                                                   
                    Spacer(Modifier.height(8.dp))

                    MedicationDoseInfo(medicationItem)
                }

                Spacer(Modifier.width(16.dp))

                if (isSelectionMode) {
                    CircularCheckbox(
                        checked = isSelected,
                        onCheckedChange = onClick
                    )
                } else {
                    Switch(
                        checked = medicationItem.medication.isActive,
                        onCheckedChange = onReminderToggled
                    )
                }
            }
        }
    }
}

@Composable
private fun MedicationTimeRow(
    formattedTimes: List<String?>,
    nextDoseTime: String?,
    frequency: Frequency,
    isActive: Boolean
) {
    if (formattedTimes.isNotEmpty()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            formattedTimes.forEach { time ->
                val isNextDose = (time == nextDoseTime || frequency.isInterval())

                val timeParts = time?.split(" ")
                val timeValue = timeParts?.getOrNull(0) ?: ""
                val amPmValue = timeParts?.getOrNull(1) ?: ""

                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontSize = 18.sp,
                                fontWeight = if (isNextDose && isActive) FontWeight.Bold else FontWeight.Normal,
                                color = if (isNextDose && isActive) {
                                    MaterialTheme.colorScheme.primary
                                } else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            append(timeValue.toLocalizedDigits())
                        }

                        append(" ")

                        withStyle(
                            style = SpanStyle(
                                fontSize = 12.sp,
                                color = if (isNextDose && isActive) {
                                    MaterialTheme.colorScheme.primary
                                } else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            append(amPmValue)
                        }
                    },
                    modifier = if (isNextDose && isActive) {
                        Modifier
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    } else {
                        Modifier
                    }
                )
            }
        }
    }
}

@Composable
private fun MedicationDoseInfo(medicationItem: MedicationItem) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = medicationItem.formattedFrequency.asString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        medicationItem.nextDoseIn?.let { nextDoseText ->
            if (medicationItem.medication.isActive) {
                Text(
                    text = "|",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .basicMarquee(iterations = Int.MAX_VALUE)
                ) {
                    Text(
                        text = stringResource(Res.string.next_dose_in),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )

                    AnimatedContent(
                        targetState = nextDoseText,
                        transitionSpec = {
                            (slideInVertically(
                                animationSpec = tween(
                                    durationMillis = 300,
                                    easing = FastOutSlowInEasing
                                )
                            ) { height -> height } + fadeIn(animationSpec = tween(300)))
                                .togetherWith(
                                    slideOutVertically(
                                        animationSpec = tween(
                                            durationMillis = 300,
                                            easing = FastOutSlowInEasing
                                        )
                                    ) { height -> -height } + fadeOut(animationSpec = tween(300))
                                )
                                .using(SizeTransform(clip = false))
                        },
                        label = "nextDoseInAnimation"
                    ) { targetText ->
                        Text(
                            text = targetText.asString().toLocalizedDigits(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }

                    if (LocalizedLanguage.current == Language.BANGLA &&
                        medicationItem.nextDoseIn.asString() != stringResource(Res.string.less_then_a_minute)) {
                        Text(
                            text = stringResource(Res.string.next_dose_after),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CircularCheckbox(
    checked: Boolean,
    onCheckedChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = tween(durationMillis = 200)
    )
    val borderColor by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        animationSpec = tween(durationMillis = 200)
    )
    val checkmarkColor = MaterialTheme.colorScheme.onPrimary

    Box(
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(2.dp, borderColor, CircleShape)
            .clickable { onCheckedChange() },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = checked,
            enter = scaleIn(animationSpec = tween(200)) + fadeIn(),
            exit = scaleOut(animationSpec = tween(200)) + fadeOut()
        ) {
            Icon(
                painter = painterResource(Res.drawable.icon_rounded_check),
                contentDescription = null,
                tint = checkmarkColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview
@Composable
private fun MedicationListItemPreview() {
    val medication = Medication(
        medicineName = "Aspirin",
        dosageStrength = "500 mg",
        medicationType = "Tablet",
        frequency = "Twice Daily",
        formattedTimes = listOf("8:00 AM", "6:00 PM"),
        formattedStartDate = "23 Oct, 2023",
        formattedEndDate = null,
        notes = null,
        hospitalName = null,
        doctorName = null,
        hospitalAddress = null,
        prescriptionImagePath = null,
        medicationImagePath = null,
        isActive = true
    )
    val medicationItem = MedicationItem(
        medication = medication,
        formattedFrequency = UiText.DynamicString("Twice Daily"),
        nextDoseTime = "6:00 PM",
        nextDoseIn = UiText.DynamicString("less then a minute")
    )
    MedicationListItem(
        medicationItem = medicationItem,
        isSelected = false,
        isSelectionMode = false,
        onClick = {},
        onLongClick = {},
        onReminderToggled = {},
        modifier = Modifier
            .widthIn(max = 700.dp)
            .fillMaxWidth()
    )
}

@Preview
@Composable
private fun MedicationListItemPreview2() {
    val medication = Medication(
        medicationId = 1,
        medicineName = "Aspirin",
        dosageStrength = "500 mg",
        medicationType = "Tablet",
        frequency = "Thrice Daily",
        formattedTimes = listOf("8:00 AM", "2:00 PM", "9:00 PM"),
        formattedStartDate = "23 Oct, 2023",
        formattedEndDate = null,
        notes = null,
        hospitalName = null,
        doctorName = null,
        hospitalAddress = null,
        prescriptionImagePath = null,
        medicationImagePath = null,
        isActive = true
    )
    val medicationItem = MedicationItem(
        medication = medication,
        formattedFrequency = UiText.DynamicString("Thrice Daily"),
        nextDoseTime = "2:00 PM",
        nextDoseIn = UiText.DynamicString("2h 15m")
    )
    MedicationListItem(
        medicationItem = medicationItem,
        isSelected = false,
        isSelectionMode = false,
        onClick = {},
        onLongClick = {},
        onReminderToggled = {},
        modifier = Modifier
            .widthIn(max = 700.dp)
            .fillMaxWidth()
    )
}

@Preview
@Composable
private fun MedicationListItemPreview3() {
    val medication = Medication(
        medicationId = 1,
        medicineName = "Aspirin",
        dosageStrength = "500 mg",
        medicationType = "Tablet",
        frequency = "Every 4 hours",
        formattedTimes = listOf("8:00 AM"),
        formattedStartDate = "23 Oct, 2023",
        formattedEndDate = null,
        notes = null,
        hospitalName = null,
        doctorName = null,
        hospitalAddress = null,
        prescriptionImagePath = null,
        medicationImagePath = null,
        isActive = true
    )
    val medicationItem = MedicationItem(
        medication = medication,
        formattedFrequency = UiText.DynamicString("Every 4 hours"),
        nextDoseTime = "12:00 PM",
        nextDoseIn = UiText.DynamicString("2h 15m")
    )
    MedicationListItem(
        medicationItem = medicationItem,
        isSelected = false,
        isSelectionMode = false,
        onClick = {},
        onLongClick = {},
        onReminderToggled = {},
        modifier = Modifier
            .widthIn(max = 700.dp)
            .fillMaxWidth()
    )
}

@Preview
@Composable
private fun MedicationListItemPreview4() {
    val medication = Medication(
        medicationId = 1,
        medicineName = "Aspirin",
        dosageStrength = "500 mg",
        medicationType = "Tablet",
        frequency = "Weekly",
        formattedTimes = listOf("8:00 AM"),
        formattedStartDate = "23 Oct, 2023",
        formattedEndDate = null,
        notes = null,
        hospitalName = null,
        doctorName = null,
        hospitalAddress = null,
        prescriptionImagePath = null,
        medicationImagePath = null,
        isActive = true
    )
    val medicationItem = MedicationItem(
        medication = medication,
        formattedFrequency = UiText.DynamicString("Weekly"),
        nextDoseTime = "8:00 AM",
        nextDoseIn = UiText.DynamicString("2h 15m")
    )
    MedicationListItem(
        medicationItem = medicationItem,
        isSelected = false,
        isSelectionMode = false,
        onClick = {},
        onLongClick = {},
        onReminderToggled = {},
        modifier = Modifier
            .widthIn(max = 700.dp)
            .fillMaxWidth()
    )
}

@Preview
@Composable
private fun MedicationListItemPreview5() {
    val medication = Medication(
        medicationId = 1,
        medicineName = "Aspirin",
        dosageStrength = "500 mg",
        medicationType = "Tablet",
        frequency = "Every 6 Hours",
        formattedTimes = emptyList(),
        formattedStartDate = "23 Oct, 2023",
        formattedEndDate = null,
        notes = null,
        hospitalName = null,
        doctorName = null,
        hospitalAddress = null,
        prescriptionImagePath = null,
        medicationImagePath = null,
        isActive = true
    )
    val medicationItem = MedicationItem(
        medication = medication,
        formattedFrequency = UiText.DynamicString("Every 6 Hours"),
        nextDoseTime = "2:00 PM",
        nextDoseIn = UiText.DynamicString("2h 15m")
    )
    MedicationListItem(
        medicationItem = medicationItem,
        isSelected = false,
        isSelectionMode = false,
        onClick = {},
        onLongClick = {},
        onReminderToggled = {},
        modifier = Modifier
            .widthIn(max = 700.dp)
            .fillMaxWidth()
    )
}

@Preview
@Composable
private fun MedicationListItemPreview6() {
    val medication = Medication(
        medicationId = 1,
        medicineName = "Aspirin",
        dosageStrength = "500 mg",
        medicationType = "Tablet",
        frequency = "Every 6 Hours",
        formattedTimes = emptyList(),
        formattedStartDate = "23 Oct, 2023",
        formattedEndDate = null,
        notes = null,
        hospitalName = null,
        doctorName = null,
        hospitalAddress = null,
        prescriptionImagePath = null,
        medicationImagePath = null,
        isActive = true
    )
    val medicationItem = MedicationItem(
        medication = medication,
        formattedFrequency = UiText.DynamicString("Every 6 Hours"),
        nextDoseTime = "2:00 PM",
        nextDoseIn = UiText.DynamicString("2h 15m")
    )
    MedicationListItem(
        medicationItem = medicationItem,
        isSelected = true,
        isSelectionMode = true,
        onClick = {},
        onLongClick = {},
        onReminderToggled = {},
        modifier = Modifier
            .widthIn(max = 700.dp)
            .fillMaxWidth()
    )
}
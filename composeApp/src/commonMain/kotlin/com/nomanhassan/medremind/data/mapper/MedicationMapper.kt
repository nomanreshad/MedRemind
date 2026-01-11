package com.nomanhassan.medremind.data.mapper

import com.nomanhassan.medremind.core.enums.Frequency
import com.nomanhassan.medremind.core.enums.MedicationType
import com.nomanhassan.medremind.core.util.DateTimeFormatterUtil
import com.nomanhassan.medremind.data.local.database.MedicationEntity
import com.nomanhassan.medremind.domain.model.Medication

fun MedicationEntity.toMedication() = Medication(
    medicationId = medicationId,
    medicineName = medicineName,
    dosageStrength = dosageStrength,
    medicationType = medicationType?.name,
    frequency = frequency.name,
    formattedTimes = timesEpochMillis.map { timesEpoch ->
        timesEpoch?.let { millis ->
             DateTimeFormatterUtil.formatTime(millis)
        }
    },
    formattedStartDate = DateTimeFormatterUtil.formatDate(startDate),
    formattedEndDate = endDate?.let { DateTimeFormatterUtil.formatDate(endDate) },
    notes = notes,
    hospitalName = hospitalName,
    doctorName = doctorName,
    hospitalAddress = hospitalAddress,
    prescriptionImagePath = prescriptionImagePath,
    medicationImagePath = medicationImagePath,
    isActive = isActive
)

fun Medication.toMedicationEntity(): MedicationEntity {
    val startEpochDate = DateTimeFormatterUtil.parseDateToEpochMillis(formattedStartDate)
    val endEpochDate = formattedEndDate?.let { DateTimeFormatterUtil.parseDateToEpochMillis(it) }
    val epochTimes = formattedTimes.map { times ->
        times?.let { time ->
            DateTimeFormatterUtil.parseTimeToEpochMillis(timeStr = time, baseDateEpochMillis = startEpochDate)
        }
    }
    
    return MedicationEntity(
        medicationId = medicationId,
        medicineName = medicineName,
        dosageStrength = dosageStrength,
        medicationType = MedicationType.fromName(medicationType),
        frequency = Frequency.fromName(frequency),
        timesEpochMillis = epochTimes,
        startDate = startEpochDate,
        endDate = endEpochDate,
        notes = notes,
        hospitalName = hospitalName,
        doctorName = doctorName,
        hospitalAddress = hospitalAddress,
        prescriptionImagePath = prescriptionImagePath,
        medicationImagePath = medicationImagePath,
        isActive = isActive
    )
}
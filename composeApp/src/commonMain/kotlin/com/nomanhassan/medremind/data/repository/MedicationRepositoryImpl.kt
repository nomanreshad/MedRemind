@file:OptIn(ExperimentalTime::class)

package com.nomanhassan.medremind.data.repository

import com.nomanhassan.medremind.core.domain.DataError
import com.nomanhassan.medremind.core.domain.EmptyResult
import com.nomanhassan.medremind.core.domain.Result
import com.nomanhassan.medremind.core.enums.Frequency
import com.nomanhassan.medremind.core.enums.TimeSlot
import com.nomanhassan.medremind.core.notifications.ReminderScheduler
import com.nomanhassan.medremind.core.util.NextOccurrenceCalculator
import com.nomanhassan.medremind.data.local.database.MedicationDao
import com.nomanhassan.medremind.data.local.database.MedicationEntity
import com.nomanhassan.medremind.data.mapper.toMedicationEntity
import com.nomanhassan.medremind.data.mapper.toMedication
import com.nomanhassan.medremind.domain.model.Medication
import com.nomanhassan.medremind.domain.repository.MedicationRepository
import com.nomanhassan.medremind.core.util.DateTimeFormatterUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.notification_body_take
import medremind.composeapp.generated.resources.notification_title_reminder
import org.jetbrains.compose.resources.getString
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class MedicationRepositoryImpl(
    private val medicationDao: MedicationDao,
    private val reminderScheduler: ReminderScheduler
): MedicationRepository {
    
    private val schedulingMutex = Mutex()

    override suspend fun saveMedicationsAndScheduleReminders(medications: List<Medication>): EmptyResult<DataError.Local> {
        return try {
            medicationDao.upsertMedicationsSafely(medications.map { it.toMedicationEntity() })
            scheduleGlobalAlarms()
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun deleteMedicationById(id: Int) {
        medicationDao.deleteMedicationById(id)
        scheduleGlobalAlarms()
    }

    override suspend fun deleteMedication(medication: Medication) {
        medicationDao.deleteMedication(medication.toMedicationEntity())
        scheduleGlobalAlarms()
    }

    override suspend fun deleteMedications(medications: List<Medication>) {
        medicationDao.deleteMedicationsSafely(medications.map { it.toMedicationEntity() })
        scheduleGlobalAlarms()
    }

    override suspend fun deleteAllMedications() { 
        medicationDao.deleteAllMedications()
        scheduleGlobalAlarms()
    }

    override fun getAllMedications(): Flow<List<Medication>> {
        return medicationDao.getAllMedications().map { entities -> 
            entities.map { it.toMedication() }
        }
    }

    override fun getActiveMedications(): Flow<List<Medication>> {
        return medicationDao.getActiveMedications().map { entities -> 
            entities.map { it.toMedication() }
        }
    }

    override suspend fun getMedicationById(id: Int): Medication? {
        return medicationDao.getMedicationById(id)?.toMedication()
    }

    override fun searchMedications(query: String): Flow<List<Medication>> {
        return medicationDao.searchMedications(query).map { entities ->
            entities.map { it.toMedication() }
        }
    }

    override fun getMedicationsByTimeSlot(slot: TimeSlot): Flow<List<Medication>> {
        return medicationDao.getActiveMedications().map { entities ->
            entities.map { it.toMedication() }
                .filter { med ->
                    if (slot == TimeSlot.ALL) return@filter true
                    val frequencyMatches = getTimeSlot(med.frequency) == slot
                    val timeMatches = med.formattedTimes.any { time ->
                        time?.let { getTimeSlot(it) == slot } == true
                    }
                    
                    frequencyMatches || timeMatches
                }
        }
    }

    override suspend fun getMedicationsByIds(ids: List<Int>): List<Medication> {
        return medicationDao.getMedicationsByIds(ids).map { it.toMedication() }
    }

    override suspend fun toggleActive(id: Int, isActive: Boolean) {
        medicationDao.toggleActive(id, isActive)
        scheduleGlobalAlarms()
    }

    override suspend fun getImageUsageCount(path: String): Int {
        return medicationDao.countImageUsage(path)
    }

    private fun getTimeSlot(time: String): TimeSlot {
        // 1. Check for Frequency-based slots first (Exact matches)
        val frequencySlot = when (time) {
            Frequency.EVERY_FOUR_HOURS.name -> TimeSlot.EVERY_4_HOURS
            Frequency.EVERY_SIX_HOURS.name -> TimeSlot.EVERY_6_HOURS
            Frequency.WEEKLY.name -> TimeSlot.WEEKLY
            Frequency.MONTHLY.name -> TimeSlot.MONTHLY
            Frequency.AS_NEEDED.name -> TimeSlot.AS_NEEDED
            else -> null
        }
        if (frequencySlot != null) return frequencySlot
        
        // 2. Fallback to Time-based logic for formats like "8:40 AM", "1:00 PM", etc.
        val match = Regex("^(\\d{1,2}):(\\d{2})\\s?(AM|PM)$").matchEntire(time)
            ?: return TimeSlot.ALL

        val (hourStr, _, suffix) = match.destructured
        val hour12 = hourStr.toIntOrNull() ?: return TimeSlot.ALL
        
        val hour24 = when {
            suffix == "AM" && hour12 == 12 -> 0 // 12 AM -> 00:00
            suffix == "PM" && hour12 != 12 -> hour12 + 12 // 1 PM -> 13:00, etc.
            else -> hour12
        }
        
        return when (hour24) {
            in 5..11 -> TimeSlot.MORNING
            in 12..17 -> TimeSlot.NOON_AFTERNOON
            in 18..20 -> TimeSlot.EVENING
            in 21..23, in 0..4 -> TimeSlot.NIGHT
            else -> TimeSlot.ALL
        }
    }

    override suspend fun scheduleGlobalAlarms() {
        schedulingMutex.withLock {
            try {
                val activeMeds = medicationDao.getActiveMedications().first()

                // 2. Calculate the NEXT single trigger for each
                val timeToMedsMap = mutableMapOf<Long, MutableList<MedicationEntity>>()

                activeMeds.forEach { med ->
                    val nextTime = NextOccurrenceCalculator.calculateNextSingleTrigger(
                        entity = med,
                        afterTime = Clock.System.now().toEpochMilliseconds()
                    )
                    nextTime?.let { time ->
                        timeToMedsMap.getOrPut(time) { mutableListOf() }.add(med)
                    }
                }

                // 3. Schedule Alarms (One per Time Slot)
                timeToMedsMap.forEach { (time, meds) ->
                    val names = meds.joinToString(", ") { it.medicineName }

                    val titleText = getString(Res.string.notification_title_reminder, DateTimeFormatterUtil.formatTime(time))
                    val bodyText = getString(Res.string.notification_body_take, names)

                    reminderScheduler.scheduleReminder(
                        id = time,
                        title = titleText,
                        body = bodyText,
                        triggerAtMillis = time
                    )
                }
            } catch (e: Exception) {
                println("Scheduling Error: ${e.message}")
            }
        }
    }

    override suspend fun getDueMedicationsByTime(triggerTime: Long): List<Medication>? {
        return try {
            val activeMeds = medicationDao.getActiveMedications().first()

            val dueMeds = activeMeds.filter { med ->
                NextOccurrenceCalculator.isDueAt(med, triggerTime)
            }

            return if (dueMeds.isEmpty()) null else dueMeds.map { it.toMedication() }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
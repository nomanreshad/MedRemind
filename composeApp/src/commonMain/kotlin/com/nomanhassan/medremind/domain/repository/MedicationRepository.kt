package com.nomanhassan.medremind.domain.repository

import com.nomanhassan.medremind.core.domain.DataError
import com.nomanhassan.medremind.core.domain.EmptyResult
import com.nomanhassan.medremind.core.enums.TimeSlot
import com.nomanhassan.medremind.domain.model.Medication
import kotlinx.coroutines.flow.Flow

interface MedicationRepository {
    
    suspend fun saveMedicationsAndScheduleReminders(medications: List<Medication>): EmptyResult<DataError.Local>

    suspend fun deleteMedicationById(id: Int)
    
    suspend fun deleteMedication(medication: Medication)
    
    suspend fun deleteMedications(medications: List<Medication>)
    
    suspend fun deleteAllMedications()

    fun getAllMedications(): Flow<List<Medication>>
    
    fun getActiveMedications(): Flow<List<Medication>>

    suspend fun getMedicationById(id: Int): Medication?
    
    fun searchMedications(query: String): Flow<List<Medication>>

    fun getMedicationsByTimeSlot(slot: TimeSlot): Flow<List<Medication>>

    suspend fun getMedicationsByIds(ids: List<Int>): List<Medication>
    
    suspend fun toggleActive(id: Int, isActive: Boolean)

    suspend fun getImageUsageCount(path: String): Int

    suspend fun scheduleGlobalAlarms()

    suspend fun getDueMedicationsByTime(triggerTime: Long): List<Medication>?
}
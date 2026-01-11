package com.nomanhassan.medremind.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MedicationDao {
    
    @Upsert
    protected abstract suspend fun upsertMedications(medications: List<MedicationEntity>)
    
    @Transaction
    open suspend fun upsertMedicationsSafely(medications: List<MedicationEntity>) {
        return upsertMedications(medications)
    }

    @Query(
        """
        DELETE FROM medications
        WHERE medicationId = :id
    """
    )
    abstract suspend fun deleteMedicationById(id: Int)
    
    @Delete
    abstract suspend fun deleteMedication(medication: MedicationEntity)
    
    @Delete
    protected abstract suspend fun deleteMedications(medications: List<MedicationEntity>)
    
    @Delete
    open suspend fun deleteMedicationsSafely(medications: List<MedicationEntity>) {
        deleteMedications(medications)
    }
    
    @Query("DELETE FROM medications")
    abstract suspend fun deleteAllMedications()

    // Get all medications in descending order
    @Query(
        """
        SELECT * FROM medications
        ORDER BY medicationId DESC
    """
    )
    abstract fun getAllMedications(): Flow<List<MedicationEntity>>

    // Get active medications only
    @Query(
        """
        SELECT * FROM medications
        WHERE isActive = 1
        ORDER BY medicationId DESC
    """
    )
    abstract fun getActiveMedications(): Flow<List<MedicationEntity>>

    // Get medication by ID
    @Query(
        """
        SELECT * FROM medications
        WHERE medicationId = :id
        LIMIT 1
    """
    )
    abstract suspend fun getMedicationById(id: Int): MedicationEntity?

    // Search medications by name
    @Query("""
        SELECT * FROM medications
        WHERE medicineName LIKE '%' || :query || '%' COLLATE NOCASE
        ORDER BY medicineName ASC
    """)
    abstract fun searchMedications(query: String): Flow<List<MedicationEntity>>

    // Fetch medications by a list of IDs
    @Query(
        """
        SELECT * FROM medications
        WHERE medicationId IN (:ids)
    """
    )
    abstract suspend fun getMedicationsByIds(ids: List<Int>): List<MedicationEntity>
    
    @Query("""
        SELECT * FROM medications
        WHERE timesEpochMillis LIKE '%' || :time || '%'
    """)
    abstract fun getMedicationsByTime(time: String): Flow<List<MedicationEntity>>

    @Query("""
        SELECT * FROM medications
        WHERE isActive = 1 AND timesEpochMillis > :currentTimeMillis
        ORDER BY timesEpochMillis ASC
    """)
    abstract suspend fun getAllFutureReminders(currentTimeMillis: Long): List<MedicationEntity>
    
    // update isActive
    @Query(
        """
        UPDATE medications
        SET isActive = :isActive
        WHERE medicationId = :id
    """
    )
    abstract suspend fun toggleActive(id: Int, isActive: Boolean)
    
    @Query(
        """
        SELECT COUNT(*) FROM medications
        WHERE prescriptionImagePath = :path OR medicationImagePath = :path
    """
    )
    abstract suspend fun countImageUsage(path: String): Int
}
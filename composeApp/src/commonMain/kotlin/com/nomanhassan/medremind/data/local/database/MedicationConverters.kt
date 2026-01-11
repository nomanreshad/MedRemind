package com.nomanhassan.medremind.data.local.database

import androidx.room.TypeConverter
import com.nomanhassan.medremind.core.enums.Frequency
import com.nomanhassan.medremind.core.enums.MedicationType
import kotlinx.serialization.json.Json

object MedicationConverters {
    
    @TypeConverter
    fun fromMedicationType(value: MedicationType?): String? = value?.name

    @TypeConverter
    fun toMedicationType(value: String?): MedicationType? = value?.let { MedicationType.valueOf(it) }
    
    @TypeConverter
    fun fromFrequency(value: Frequency): String = value.name

    @TypeConverter
    fun toFrequency(value: String): Frequency = Frequency.valueOf(value)

    @TypeConverter
    fun fromLongList(value: List<Long?>): String = Json.encodeToString(value)

    @TypeConverter
    fun toLongList(value: String): List<Long?> =
        if (value.isBlank()) emptyList() else Json.decodeFromString(value)

    @TypeConverter
    fun fromIntList(value: List<Int>): String = Json.encodeToString(value)

    @TypeConverter
    fun toIntList(value: String): List<Int> =
        if (value.isBlank()) emptyList() else Json.decodeFromString(value)
}
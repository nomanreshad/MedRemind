@file:OptIn(ExperimentalTime::class)

package com.nomanhassan.medremind.core.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object DateTimeFormatterUtil {
    
    private val zone = TimeZone.currentSystemDefault()

    private val monthMap = Month.entries.associateBy { month ->
        month.name
            .lowercase()
            .replaceFirstChar { it.uppercase() }
            .take(3)
    }

    fun getCurrentTime(): LocalTime {
        return Clock.System.now()
            .toLocalDateTime(zone)
            .time
    }

    fun getCurrentDateMidnightMillis(): Long {
        val now = Clock.System.now()
            .toLocalDateTime(zone)
        val midnightToday = LocalDateTime(
            now.date, LocalTime(0, 0)
        )
        return midnightToday.toInstant(zone).toEpochMilliseconds()
    }

    fun getEpochMillisForTimeToday(hour: Int, minute: Int): Long {
        val now = Clock.System.now().toLocalDateTime(zone).date
        val selectedLocalTime = LocalTime(hour = hour, minute = minute)
        val selectedDateTime = LocalDateTime(now, selectedLocalTime)
        return selectedDateTime.toInstant(zone).toEpochMilliseconds()
    }
    
    fun formatTime(epochMillis: Long): String {
        val time = Instant
            .fromEpochMilliseconds(epochMillis)
            .toLocalDateTime(zone)
        
        val hour = (time.hour % 12).let {
            if (it == 0) 12 else it
        }
        val minute = time.minute.toString().padStart(2, '0')
        val suffix = if (time.hour < 12) "AM" else "PM"
        
        return "$hour:$minute $suffix" // 8:40 AM, 1:00 PM
    }

    fun formatDate(epochMillis: Long): String {
        val date = Instant
            .fromEpochMilliseconds(epochMillis)
            .toLocalDateTime(zone)
        
        val month = date.month.name
            .lowercase()
            .replaceFirstChar { it.uppercase() }
            .take(3)
        
        return "$month ${date.day}, ${date.year}" // Oct 20, 2025
    }
    
    fun parseTimeToEpochMillis(timeStr: String, baseDateEpochMillis: Long): Long {
        // Expecting format like "8:40 AM", "1:00 PM", etc.
        val match = Regex("^(\\d{1,2}):(\\d{2})\\s?(AM|PM)$").matchEntire(timeStr)
            ?: error("Invalid time format: $timeStr")
        
        val (hourStr, minuteStr, suffix) = match.destructured
        var hour = hourStr.toInt()
        val minute = minuteStr.toInt()
        
        if (suffix == "PM" && hour < 12) hour += 12
        if (suffix == "AM" && hour == 12) hour = 0
        
        val baseDate = Instant
            .fromEpochMilliseconds(baseDateEpochMillis)
            .toLocalDateTime(zone)
        
        val dateTime = LocalDateTime(
            year = baseDate.year,
            month = baseDate.month,
            day = baseDate.day,
            hour = hour,
            minute = minute
        )
        
        return dateTime.toInstant(zone).toEpochMilliseconds()
    }
    
    fun parseDateToEpochMillis(dateStr: String): Long {
        // Expecting format like "Oct 20, 2025"
        val match = Regex("^([A-Za-z]{3})\\s(\\d{1,2}),\\s(\\d{4})$").matchEntire(dateStr)
            ?: error("Invalid date format: $dateStr")
        
        val (monthAbbr, dayStr, yearStr) = match.destructured
        val month = monthMap[monthAbbr] ?: error("Invalid month abbreviation: $monthAbbr")
        val day = dayStr.toInt()
        val year = yearStr.toInt()

        val dateTime = LocalDateTime(
            year = year,
            month = month,
            day = day,
            hour = 0,
            minute = 0
        )
        
        return dateTime.toInstant(zone).toEpochMilliseconds()
    }
}
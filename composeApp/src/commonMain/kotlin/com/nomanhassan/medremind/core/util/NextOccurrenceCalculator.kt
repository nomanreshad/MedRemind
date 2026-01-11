@file:OptIn(ExperimentalTime::class)

package com.nomanhassan.medremind.core.util

import com.nomanhassan.medremind.core.enums.Frequency
import com.nomanhassan.medremind.data.local.database.MedicationEntity
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object NextOccurrenceCalculator {

    /**
     * Calculates the exact timestamp (in epoch milliseconds) for the next single medication
     * reminder that should occur after a given time.
     *
     * This function considers the medication's start date, end date (if any), frequency,
     * and specific times of day. It handles two main types of schedules:
     * 1.  **Interval-based:** (e.g., "Every 4 hours"). It calculates the next occurrence based
     *     on an anchor time (the first dose time) and the specified interval.
     * 2.  **Time-of-day based:** (e.g., "Daily at 8 AM and 8 PM", "Weekly on Mondays at 9 AM").
     *     It iterates day by day from the `afterTime`, checking against the defined times and
     *     frequency rules (daily, weekly on the same day-of-week as the start date, or monthly
     *     on the same day-of-month as the start date).
     *
     * The calculation ensures the returned time is always:
     * - Greater than `afterTime`.
     * - On or after the medication's `startDate`.
     * - On or before the medication's `endDate` (if one is set).
     *
     * @param entity The [MedicationEntity] containing the schedule details.
     * @param afterTime The epoch millisecond timestamp after which to find the next occurrence.
     *                  This is typically the current time.
     * @return The calculated epoch millisecond timestamp for the next reminder, or `null` if
     *         no future occurrence can be found (e.g., the medication schedule has ended or
     *         the schedule is invalid).
     */
    fun calculateNextSingleTrigger(
        entity: MedicationEntity,
        afterTime: Long
    ): Long? {
        val timeZone = TimeZone.currentSystemDefault()

        // The effective start is either the medication's start date 
        // OR the time we are checking from, whichever is later.
        val startTimeBoundary = maxOf(entity.startDate, afterTime)

        // 1. Handle Interval Frequencies (Every X Hours)
        if (entity.frequency.isInterval()) {
            val intervalHours = when (entity.frequency) {
                Frequency.EVERY_FOUR_HOURS -> 4
                Frequency.EVERY_SIX_HOURS -> 6
                else -> return null
            }
            val intervalMillis = intervalHours * 60 * 60 * 1000L

            // Anchor point: The first time set by user, or the start date
            val anchorTime = entity.timesEpochMillis.filterNotNull().firstOrNull() ?: entity.startDate

            // Find the first occurrence of (anchor + N * interval) that is > afterTime
            val nextTime = if (startTimeBoundary <= anchorTime) {
                anchorTime // If we haven't reached the anchor yet, anchor is the next one
            } else {
                val timeSinceAnchor = startTimeBoundary - anchorTime
                val completedPeriods = (timeSinceAnchor / intervalMillis)
                anchorTime + ((completedPeriods + 1) * intervalMillis)
            }

            // Check if it exceeds the medication end date
            return if (entity.endDate == null || nextTime <= entity.endDate) nextTime else null
        }

        // 2. Handle Daily/Weekly/Monthly Frequencies
        // Get the specific times of day defined for this medication (e.g., 8:00 AM)
        val definedTimes = entity.timesEpochMillis
            .filterNotNull()
            .map {
                Instant.fromEpochMilliseconds(it).toLocalDateTime(timeZone).time
            }
            .sorted()

        if (definedTimes.isEmpty()) return null

        val afterInstant = Instant.fromEpochMilliseconds(startTimeBoundary)
        var searchDate = afterInstant.toLocalDateTime(timeZone).date
        val searchLimitDate = searchDate.plus(366, DateTimeUnit.DAY) // Search up to a year

        while (searchDate <= searchLimitDate) {
            // Check if searchDate exceeds Medication End Date
            if (entity.endDate != null &&
                searchDate.atStartOfDayIn(timeZone).toEpochMilliseconds() > entity.endDate) return null

            // Check every defined time for this day
            for (time in definedTimes) {
                val candidateInstant = searchDate.atTime(time).toInstant(timeZone)
                val candidateMillis = candidateInstant.toEpochMilliseconds()

                // Logic: Must be after 'afterTime' AND on/after 'startDate'
                if (candidateMillis > afterTime && candidateMillis >= entity.startDate) {
                    val matchesFrequency = when (entity.frequency) {
                        Frequency.WEEKLY -> {
                            val startDayOfWeek = Instant.fromEpochMilliseconds(entity.startDate)
                                .toLocalDateTime(timeZone).dayOfWeek
                            searchDate.dayOfWeek == startDayOfWeek
                        }
                        Frequency.MONTHLY -> {
                            val startDayOfMonth = Instant.fromEpochMilliseconds(entity.startDate)
                                .toLocalDateTime(timeZone).day
                            searchDate.day == startDayOfMonth
                        }
                        // ONCE, TWICE, THRICE match every day
                        else -> true
                    }

                    if (matchesFrequency) {
                        // Final check against end date
                        if (entity.endDate == null || candidateMillis <= entity.endDate) {
                            return candidateMillis
                        }
                    }
                }
            }
            // Move to next day
            searchDate = searchDate.plus(1, DateTimeUnit.DAY)
        }
        return null
    }

    /**
     * Checks if a medication is considered "due" at a specific timestamp, allowing for a
     * small margin of error.
     *
     * This function is used to validate if an alarm or notification firing at `targetTime`
     * corresponds to a legitimate scheduled dose for the given `entity`. It handles both
     * time-of-day and interval-based schedules.
     *
     * **For time-of-day schedules (Daily, Weekly, Monthly):**
     * It verifies that the `targetTime` matches one of the scheduled times of day (hour and minute)
     * and also satisfies the frequency rule (e.g., same day of the week for weekly schedules).
     *
     * **For interval-based schedules (Every X hours):**
     * It calculates if the `targetTime` falls on a valid interval point, starting from an
     * initial anchor time. A generous leniency window (e.g., +/- 10 minutes) is used to account
     * for potential delays in alarm delivery by the Android OS, ensuring the reminder is still
     * considered valid even if it fires a few minutes late.
     *
     * @param entity The [MedicationEntity] containing the schedule details.
     * @param targetTime The epoch millisecond timestamp to check. This is typically the time an
     *                   alarm is triggered.
     * @return `true` if the medication is due at the `targetTime` (within the allowed tolerance),
     *         `false` otherwise.
     */
    fun isDueAt(entity: MedicationEntity, targetTime: Long): Boolean {
        // 1. Basic Bounds Check
        // If the alarm is firing before the medication even started, ignore it.
        if (targetTime < (entity.startDate - 60_000)) return false
        if (entity.endDate != null && targetTime > (entity.endDate + 60_000)) return false

        val timeZone = TimeZone.currentSystemDefault()
        val targetLocal = Instant.fromEpochMilliseconds(targetTime).toLocalDateTime(timeZone)

        // A. Daily / Weekly / Monthly Logic
        // We check if the current HOUR and MINUTE match any of the times defined by the user.
        val definedTimes = entity.timesEpochMillis.filterNotNull()
            .map { Instant.fromEpochMilliseconds(it).toLocalDateTime(timeZone) }

        if (definedTimes.isNotEmpty() && !entity.frequency.isInterval()) {
            val timeMatch = definedTimes.any {
                it.hour == targetLocal.hour && it.minute == targetLocal.minute
            }

            if (timeMatch) {
                return when (entity.frequency) {
                    Frequency.WEEKLY -> {
                        val startDay = Instant.fromEpochMilliseconds(entity.startDate).toLocalDateTime(timeZone).dayOfWeek
                        targetLocal.dayOfWeek == startDay
                    }
                    Frequency.MONTHLY -> {
                        val startDay = Instant.fromEpochMilliseconds(entity.startDate).toLocalDateTime(timeZone).day
                        targetLocal.day == startDay
                    }
                    else -> true // Daily match
                }
            }
        }

        // B. Interval Logic (Every 4/6 Hours) - THIS IS THE FIX FOR "CLOSED" APPS
        if (entity.frequency.isInterval()) {
            val intervalHours = if (entity.frequency == Frequency.EVERY_FOUR_HOURS) 4 else 6
            val intervalMillis = intervalHours * 60 * 60 * 1000L

            // Anchor is the time the user picked (e.g., 08:00 AM)
            val anchorTime = entity.timesEpochMillis.filterNotNull().firstOrNull() ?: entity.startDate

            // Calculate the difference in time
            val diff = abs(targetTime - anchorTime)
            val remainder = diff % intervalMillis

            // 10 MINUTE LENIENCY (600,000ms)
            // This ensures that even if Android wakes up the app 5 minutes late 
            // because it was "Closed", the notification still shows.
            return remainder < 600_000 || remainder > (intervalMillis - 600_000)
        }

        return false
    }
}
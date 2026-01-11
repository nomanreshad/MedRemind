@file:OptIn(ExperimentalTime::class)

package com.nomanhassan.medremind.core.util

import com.nomanhassan.medremind.core.enums.Frequency
import com.nomanhassan.medremind.data.local.database.MedicationEntity
import com.nomanhassan.medremind.core.enums.MedicationType
import kotlinx.datetime.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.ExperimentalTime

class NextOccurrenceCalculatorTest {

    private val timeZone = TimeZone.currentSystemDefault()

    @Test
    fun `when current time is exactly a dose time it should return the next future dose`() {
        val doseTime = createDateTime(2025, 10, 20, 8, 0)
        val med = createMedication(
            frequency = Frequency.ONCE_DAILY,
            startDate = doseTime,
            times = listOf(doseTime)
        )

        // "Now" is exactly 8:00 AM. We should NOT get 8:00 AM back.
        // We should get 8:00 AM the NEXT day.
        val next = NextOccurrenceCalculator.calculateNextSingleTrigger(med, doseTime)

        val expected = createDateTime(2025, 10, 21, 8, 0)
        
        assertEquals(expected, next, "If 'afterTime' is the dose time, it must return the next occurrence")
    }

    @Test
    fun `twice daily frequency should pick the second dose if the first one has passed`() {
        val morning = createDateTime(2025, 10, 20, 8, 0)
        val evening = createDateTime(2025, 10, 20, 20, 0)
        val med = createMedication(
            frequency = Frequency.TWICE_DAILY,
            startDate = morning,
            times = listOf(morning, evening)
        )

        // It is 10:00 AM (Past morning dose, before evening)
        val now = createDateTime(2025, 10, 20, 10, 0)
        val next = NextOccurrenceCalculator.calculateNextSingleTrigger(med, now)

        assertEquals(evening, next, "Should skip the morning dose and return the evening dose")
    }

    @Test
    fun `thrice daily frequency should jump to the next morning if all doses today have passed`() {
        val d1 = createDateTime(2025, 10, 20, 8, 0)
        val d2 = createDateTime(2025, 10, 20, 14, 0)
        val d3 = createDateTime(2025, 10, 20, 20, 0)
        val med = createMedication(
            frequency = Frequency.THRICE_DAILY,
            startDate = d1,
            times = listOf(d1, d2, d3)
        )

        // It is 11:00 PM (All doses for Oct 20 have passed)
        val now = createDateTime(2025, 10, 20, 23, 0)
        val next = NextOccurrenceCalculator.calculateNextSingleTrigger(med, now)

        val expected = createDateTime(2025, 10, 21, 8, 0)
        
        assertEquals(expected, next, "Should return the first dose of the next day")
    }

    @Test
    fun `monthly frequency starting on the 31st should skip months with only 30 days`() {
        // This tests a potential bug in basic logic
        val startJan31 = createDateTime(2025, 1, 31, 9, 0)
        val med = createMedication(
            frequency = Frequency.MONTHLY,
            startDate = startJan31,
            times = listOf(startJan31)
        )

        // It is Feb 1st. Next dose shouldn't be in Feb (no Feb 31st).
        // It should jump to March 31st.
        val now = createDateTime(2025, 2, 1, 9, 0)
        val next = NextOccurrenceCalculator.calculateNextSingleTrigger(med, now)

        val expected = createDateTime(2025, 3, 31, 9, 0)
        
        assertEquals(expected, next, "Should skip months that don't have the required day of the month")
    }

    @Test
    fun `every six hour frequency should cross over into the next day correctly`() {
        val startAt = createDateTime(2025, 10, 20, 22, 0) // 10:00 PM
        val med = createMedication(
            frequency = Frequency.EVERY_SIX_HOURS,
            startDate = startAt,
            times = listOf(startAt)
        )

        // It is 11:00 PM. Next dose is 22:00 + 6 hours = 4:00 AM next day.
        val now = createDateTime(2025, 10, 20, 23, 0)
        val next = NextOccurrenceCalculator.calculateNextSingleTrigger(med, now)

        val expected = createDateTime(2025, 10, 21, 4, 0)
        
        assertEquals(expected, next)
    }

    @Test
    fun `every four hour frequency should align to the correct time slot based on start date`() {
        val startAt = createDateTime(2025, 10, 20, 8, 0) // Monday 8:00 AM
        val med = createMedication(
            frequency = Frequency.EVERY_FOUR_HOURS,
            startDate = startAt,
            times = listOf(startAt)
        )

        // If it's 9:30 AM, the next dose should be 12:00 PM (8:00 + 4 hours)
        val now = createDateTime(2025, 10, 20, 9, 30)
        val next = NextOccurrenceCalculator.calculateNextSingleTrigger(med, now)

        val expected = createDateTime(2025, 10, 20, 12, 0)
        
        assertEquals(expected, next, "The interval should anchor to the original start time")
    }

    @Test
    fun `weekly reminders should skip to the same day next week if current time is past today's dose`() {
        val startMonday = createDateTime(2025, 10, 20, 9, 0) // Monday, Oct 20
        val med = createMedication(
            frequency = Frequency.WEEKLY,
            startDate = startMonday,
            times = listOf(startMonday)
        )

        // If it is currently Tuesday, Oct 21 (The Monday dose is passed)
        val now = createDateTime(2025, 10, 21, 10, 0)
        val next = NextOccurrenceCalculator.calculateNextSingleTrigger(med, now)

        // Expected is the following Monday, Oct 27 at 9:00 AM
        val expected = createDateTime(2025, 10, 27, 9, 0)
        
        assertEquals(expected, next, "Weekly frequency must match the starting Day of Week")
    }

    @Test
    fun `monthly reminders should skip to the same day of the month in the following month`() {
        val startOct15 = createDateTime(2025, 10, 15, 9, 0)
        val med = createMedication(
            frequency = Frequency.MONTHLY,
            startDate = startOct15,
            times = listOf(startOct15)
        )

        // If it is Oct 16
        val now = createDateTime(2025, 10, 16, 10, 0)
        val next = NextOccurrenceCalculator.calculateNextSingleTrigger(med, now)

        // Expected is Nov 15 at 9:00 AM
        val expected = createDateTime(2025, 11, 15, 9, 0)
        
        assertEquals(expected, next, "Monthly frequency must match the starting Day of Month")
    }

    @Test
    fun `calculator should return null if the next occurrence exceeds the medication end date`() {
        val start = createDateTime(2025, 10, 20, 8, 0)
        val end = createDateTime(2025, 10, 20, 20, 0) // Ends at 8 PM today
        val med = createMedication(
            frequency = Frequency.EVERY_FOUR_HOURS,
            startDate = start,
            endDate = end,
            times = listOf(start)
        )

        // If it's 9 PM, the next calculated dose (12 AM) is after the end date
        val now = createDateTime(2025, 10, 20, 21, 0)
        val next = NextOccurrenceCalculator.calculateNextSingleTrigger(med, now)

        assertNull(next, "Should return null when no more doses are allowed before end date")
    }

    @Test
    fun `medication scheduled for the future should not trigger until the start date is reached`() {
        val futureStart = createDateTime(2025, 12, 1, 8, 0) // Starts Dec 1st
        val med = createMedication(
            frequency = Frequency.ONCE_DAILY,
            startDate = futureStart,
            times = listOf(futureStart)
        )

        // If today is Nov 1st
        val now = createDateTime(2025, 11, 1, 10, 0)
        val next = NextOccurrenceCalculator.calculateNextSingleTrigger(med, now)

        assertEquals(futureStart, next, "The very first trigger should be the start date itself")
    }

    @Test
    fun `monthly reminders starting on Feb 29th should skip non-leap years correctly`() {
        val startFeb29 = createDateTime(2024, 2, 29, 9, 0) // Leap Year
        val med = createMedication(
            frequency = Frequency.MONTHLY,
            startDate = startFeb29,
            times = listOf(startFeb29)
        )

        // Current time is Feb 1st, 2025 (Not a leap year)
        val now = createDateTime(2025, 2, 1, 9, 0)
        val next = NextOccurrenceCalculator.calculateNextSingleTrigger(med, now)

        // Expected: March 29th, 2025 (Since Feb 29 doesn't exist)
        val expected = createDateTime(2025, 3, 29, 9, 0)
        
        assertEquals(expected, next, "Should skip February in non-leap years")
    }

    @Test
    fun `interval frequency should calculate correctly even if the medication started years ago`() {
        val startTwoYearsAgo = createDateTime(2023, 1, 1, 8, 0) // 8:00 AM
        val med = createMedication(
            frequency = Frequency.EVERY_FOUR_HOURS,
            startDate = startTwoYearsAgo,
            times = listOf(startTwoYearsAgo)
        )

        // It is now mid-day today (Oct 20, 2025)
        val now = createDateTime(2025, 10, 20, 10, 30)
        val next = NextOccurrenceCalculator.calculateNextSingleTrigger(med, now)

        // 8:00 AM + (N * 4 hours) should land on 12:00 PM today
        val expected = createDateTime(2025, 10, 20, 12, 0)
        
        assertEquals(expected, next, "Should fast-forward interval logic accurately over long periods")
    }

    @Test
    fun `reminder should still trigger if the dose time is exactly equal to the end date`() {
        val doseTime = createDateTime(2025, 10, 20, 20, 0) // 8:00 PM
        val med = createMedication(
            frequency = Frequency.ONCE_DAILY,
            startDate = createDateTime(2025, 10, 20, 8, 0),
            endDate = doseTime, // Ends exactly at 8:00 PM
            times = listOf(doseTime)
        )

        val now = createDateTime(2025, 10, 20, 19, 0) // It's 7:00 PM
        val next = NextOccurrenceCalculator.calculateNextSingleTrigger(med, now)

        assertEquals(doseTime, next, "The end date should be inclusive of the dose time")
    }

    @Test
    fun `every four hours frequency should preserve minutes from the original anchor time`() {
        val startAt = createDateTime(2025, 10, 20, 10, 17) // 10:17 AM
        val med = createMedication(
            frequency = Frequency.EVERY_FOUR_HOURS,
            startDate = startAt,
            times = listOf(startAt)
        )

        val now = createDateTime(2025, 10, 20, 11, 0)
        val next = NextOccurrenceCalculator.calculateNextSingleTrigger(med, now)

        val expected = createDateTime(2025, 10, 20, 14, 17) // 2:17 PM
        
        assertEquals(expected, next, "Intervals must preserve the minute-offset of the start time")
    }

    @Test
    fun `calculator should successfully fast-forward and find a dose within the search window for old medications`() {
        val start = createDateTime(2020, 1, 1, 8, 0) // Started 5 years ago
        val med = createMedication(
            frequency = Frequency.MONTHLY,
            startDate = start,
            times = listOf(start)
        )

        // Current time is Jan 1st, 2025. 
        // The next dose should be Feb 1st, 2025.
        val now = createDateTime(2025, 1, 1, 9, 0)
        val next = NextOccurrenceCalculator.calculateNextSingleTrigger(med, now)

        assertNotNull(next, "The calculator failed to 'fast-forward' from 2020 to find the next valid dose in 2025")
    }

    @Test
    fun `monthly frequency with multiple doses per day should pick the next available time slot`() {
        val startAt = createDateTime(2025, 10, 1, 8, 0)
        val noon = createDateTime(2025, 10, 1, 12, 0)
        val evening = createDateTime(2025, 10, 1, 20, 0)

        val med = createMedication(
            frequency = Frequency.MONTHLY,
            startDate = startAt,
            times = listOf(startAt, noon, evening)
        )

        // It is Oct 1st at 10:00 AM. 
        // The 8:00 AM dose is passed, next should be 12:00 PM today.
        val now = createDateTime(2025, 10, 1, 10, 0)
        val next = NextOccurrenceCalculator.calculateNextSingleTrigger(med, now)

        assertEquals(
            noon,
            next,
            "Monthly logic should be able to iterate through multiple time slots in a single day"
        )
    }

    @Test
    fun `calculator should handle null time entries gracefully without crashing`() {
        val startAt = createDateTime(2025, 10, 20, 8, 0)
        val med = createMedication(
            frequency = Frequency.ONCE_DAILY,
            startDate = startAt,
            times = listOf(null) // Simulating corrupted or empty time data
        )

        val now = createDateTime(2025, 10, 20, 7, 0)
        val next = NextOccurrenceCalculator.calculateNextSingleTrigger(med, now)

        assertNull(next, "The calculator should return null instead of crashing if time data is missing")
    }

    // --- Helpers ---

    private fun createDateTime(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {
        return LocalDateTime(year, month, day, hour, minute)
            .toInstant(timeZone)
            .toEpochMilliseconds()
    }

    private fun createMedication(
        frequency: Frequency,
        startDate: Long,
        endDate: Long? = null,
        times: List<Long?> = emptyList()
    ) = MedicationEntity(
        medicationId = 1,
        medicineName = "Test Med",
        dosageStrength = "500mg",
        medicationType = MedicationType.TABLET,
        frequency = frequency,
        timesEpochMillis = times,
        startDate = startDate,
        endDate = endDate,
        notes = null,
        hospitalName = null,
        doctorName = null,
        hospitalAddress = null,
        prescriptionImagePath = null,
        medicationImagePath = null,
        isActive = true
    )
}
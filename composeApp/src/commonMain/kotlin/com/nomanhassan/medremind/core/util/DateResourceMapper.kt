package com.nomanhassan.medremind.core.util

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Month
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.day_friday
import medremind.composeapp.generated.resources.day_monday
import medremind.composeapp.generated.resources.day_saturday
import medremind.composeapp.generated.resources.day_sunday
import medremind.composeapp.generated.resources.day_thursday
import medremind.composeapp.generated.resources.day_tuesday
import medremind.composeapp.generated.resources.day_wednesday
import medremind.composeapp.generated.resources.month_april
import medremind.composeapp.generated.resources.month_august
import medremind.composeapp.generated.resources.month_december
import medremind.composeapp.generated.resources.month_february
import medremind.composeapp.generated.resources.month_january
import medremind.composeapp.generated.resources.month_july
import medremind.composeapp.generated.resources.month_june
import medremind.composeapp.generated.resources.month_march
import medremind.composeapp.generated.resources.month_may
import medremind.composeapp.generated.resources.month_november
import medremind.composeapp.generated.resources.month_october
import medremind.composeapp.generated.resources.month_september
import org.jetbrains.compose.resources.StringResource

object DateResourceMapper {
    fun mapDayOfWeek(day: DayOfWeek): StringResource = when (day) {
        DayOfWeek.SATURDAY -> Res.string.day_saturday
        DayOfWeek.SUNDAY -> Res.string.day_sunday
        DayOfWeek.MONDAY -> Res.string.day_monday
        DayOfWeek.TUESDAY -> Res.string.day_tuesday
        DayOfWeek.WEDNESDAY -> Res.string.day_wednesday
        DayOfWeek.THURSDAY -> Res.string.day_thursday
        DayOfWeek.FRIDAY -> Res.string.day_friday
    }

    fun mapMonth(month: Month): StringResource = when (month) {
        Month.JANUARY -> Res.string.month_january
        Month.FEBRUARY -> Res.string.month_february
        Month.MARCH -> Res.string.month_march
        Month.APRIL -> Res.string.month_april
        Month.MAY -> Res.string.month_may
        Month.JUNE -> Res.string.month_june
        Month.JULY -> Res.string.month_july
        Month.AUGUST -> Res.string.month_august
        Month.SEPTEMBER -> Res.string.month_september
        Month.OCTOBER -> Res.string.month_october
        Month.NOVEMBER -> Res.string.month_november
        Month.DECEMBER -> Res.string.month_december
    }
}
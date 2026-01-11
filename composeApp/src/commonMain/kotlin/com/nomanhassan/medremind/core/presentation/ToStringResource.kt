package com.nomanhassan.medremind.core.presentation

import com.nomanhassan.medremind.core.domain.DataError
import com.nomanhassan.medremind.core.enums.Frequency
import com.nomanhassan.medremind.core.enums.MedicationType
import com.nomanhassan.medremind.core.enums.TimeSlot
import kotlinx.datetime.Month
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.error_disk_full
import medremind.composeapp.generated.resources.error_no_internet
import medremind.composeapp.generated.resources.error_request_timeout
import medremind.composeapp.generated.resources.error_serialization
import medremind.composeapp.generated.resources.error_too_many_requests
import medremind.composeapp.generated.resources.error_unknown
import medremind.composeapp.generated.resources.freq_as_needed
import medremind.composeapp.generated.resources.freq_every_four_hours
import medremind.composeapp.generated.resources.freq_every_six_hours
import medremind.composeapp.generated.resources.freq_monthly
import medremind.composeapp.generated.resources.freq_once_daily
import medremind.composeapp.generated.resources.freq_thrice_daily
import medremind.composeapp.generated.resources.freq_twice_daily
import medremind.composeapp.generated.resources.freq_weekly
import medremind.composeapp.generated.resources.med_type_capsule
import medremind.composeapp.generated.resources.med_type_cream
import medremind.composeapp.generated.resources.med_type_drops
import medremind.composeapp.generated.resources.med_type_gel
import medremind.composeapp.generated.resources.med_type_inhaler
import medremind.composeapp.generated.resources.med_type_injection
import medremind.composeapp.generated.resources.med_type_ointment
import medremind.composeapp.generated.resources.med_type_other
import medremind.composeapp.generated.resources.med_type_patch
import medremind.composeapp.generated.resources.med_type_powder
import medremind.composeapp.generated.resources.med_type_spray
import medremind.composeapp.generated.resources.med_type_syrup
import medremind.composeapp.generated.resources.med_type_tablet
import medremind.composeapp.generated.resources.month_apr_short
import medremind.composeapp.generated.resources.month_aug_short
import medremind.composeapp.generated.resources.month_dec_short
import medremind.composeapp.generated.resources.month_feb_short
import medremind.composeapp.generated.resources.month_jan_short
import medremind.composeapp.generated.resources.month_jul_short
import medremind.composeapp.generated.resources.month_jun_short
import medremind.composeapp.generated.resources.month_mar_short
import medremind.composeapp.generated.resources.month_may_short
import medremind.composeapp.generated.resources.month_nov_short
import medremind.composeapp.generated.resources.month_oct_short
import medremind.composeapp.generated.resources.month_sep_short
import medremind.composeapp.generated.resources.timeslot_all
import medremind.composeapp.generated.resources.timeslot_evening
import medremind.composeapp.generated.resources.timeslot_morning
import medremind.composeapp.generated.resources.timeslot_night
import medremind.composeapp.generated.resources.timeslot_noon_afternoon

fun DataError.toUiText(): UiText {
    val stringRes = when (this) {
        DataError.Local.DISK_FULL -> Res.string.error_disk_full
        DataError.Local.UNKNOWN -> Res.string.error_unknown
        DataError.Remote.REQUEST_TIMEOUT -> Res.string.error_request_timeout
        DataError.Remote.TOO_MANY_REQUESTS -> Res.string.error_too_many_requests
        DataError.Remote.NO_INTERNET -> Res.string.error_no_internet
        DataError.Remote.SERVER -> Res.string.error_unknown
        DataError.Remote.SERIALIZATION -> Res.string.error_serialization
        DataError.Remote.UNKNOWN -> Res.string.error_unknown
    }
    return UiText.StringResourceId(stringRes)
}

fun Frequency.toUiText(): UiText {
    val stringRes = when (this) {
        Frequency.ONCE_DAILY -> Res.string.freq_once_daily
        Frequency.TWICE_DAILY -> Res.string.freq_twice_daily
        Frequency.THRICE_DAILY -> Res.string.freq_thrice_daily
        Frequency.EVERY_FOUR_HOURS -> Res.string.freq_every_four_hours
        Frequency.EVERY_SIX_HOURS -> Res.string.freq_every_six_hours
        Frequency.WEEKLY -> Res.string.freq_weekly
        Frequency.MONTHLY -> Res.string.freq_monthly
        Frequency.AS_NEEDED -> Res.string.freq_as_needed
    }
    return UiText.StringResourceId(stringRes)
}

fun MedicationType.toUiText(): UiText {
    val stringRes = when (this) {
        MedicationType.TABLET -> Res.string.med_type_tablet
        MedicationType.CAPSULE -> Res.string.med_type_capsule
        MedicationType.SYRUP -> Res.string.med_type_syrup
        MedicationType.INJECTION -> Res.string.med_type_injection
        MedicationType.DROPS -> Res.string.med_type_drops
        MedicationType.INHALER -> Res.string.med_type_inhaler
        MedicationType.POWDER -> Res.string.med_type_powder
        MedicationType.SPRAY -> Res.string.med_type_spray
        MedicationType.CREAM -> Res.string.med_type_cream
        MedicationType.GEL -> Res.string.med_type_gel
        MedicationType.OINTMENT -> Res.string.med_type_ointment
        MedicationType.PATCH -> Res.string.med_type_patch
        MedicationType.OTHER -> Res.string.med_type_other
    }
    return UiText.StringResourceId(stringRes)
}

fun TimeSlot.toUiText(): UiText {
    val stringRes = when (this) {
        TimeSlot.ALL -> Res.string.timeslot_all
        TimeSlot.MORNING -> Res.string.timeslot_morning
        TimeSlot.NOON_AFTERNOON -> Res.string.timeslot_noon_afternoon
        TimeSlot.EVENING -> Res.string.timeslot_evening
        TimeSlot.NIGHT -> Res.string.timeslot_night
        TimeSlot.EVERY_4_HOURS -> Res.string.freq_every_four_hours
        TimeSlot.EVERY_6_HOURS -> Res.string.freq_every_six_hours
        TimeSlot.WEEKLY -> Res.string.freq_weekly
        TimeSlot.MONTHLY -> Res.string.freq_monthly
        TimeSlot.AS_NEEDED -> Res.string.freq_as_needed
    }
    return UiText.StringResourceId(stringRes)
}

fun Month.toUiText(): UiText {
    val stringRes =  when (this) {
        Month.JANUARY -> Res.string.month_jan_short
        Month.FEBRUARY -> Res.string.month_feb_short
        Month.MARCH -> Res.string.month_mar_short
        Month.APRIL -> Res.string.month_apr_short
        Month.MAY -> Res.string.month_may_short
        Month.JUNE -> Res.string.month_jun_short
        Month.JULY -> Res.string.month_jul_short
        Month.AUGUST -> Res.string.month_aug_short
        Month.SEPTEMBER -> Res.string.month_sep_short
        Month.OCTOBER -> Res.string.month_oct_short
        Month.NOVEMBER -> Res.string.month_nov_short
        Month.DECEMBER -> Res.string.month_dec_short
    }
    return UiText.StringResourceId(stringRes)
}
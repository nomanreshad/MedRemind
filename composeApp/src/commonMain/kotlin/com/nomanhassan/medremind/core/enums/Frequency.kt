package com.nomanhassan.medremind.core.enums

enum class Frequency {
    ONCE_DAILY,
    TWICE_DAILY,
    THRICE_DAILY,
    EVERY_FOUR_HOURS,
    EVERY_SIX_HOURS,
    WEEKLY,
    MONTHLY,
    AS_NEEDED;

    companion object {
        fun fromName(name: String): Frequency {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) }
                ?: error("Invalid frequency string: $name")
        }
    }
    
    fun isDaily() = this in listOf(ONCE_DAILY, TWICE_DAILY, THRICE_DAILY)
    
    fun isInterval() = this in listOf(EVERY_FOUR_HOURS, EVERY_SIX_HOURS)
}
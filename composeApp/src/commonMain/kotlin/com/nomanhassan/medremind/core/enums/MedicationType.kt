package com.nomanhassan.medremind.core.enums

enum class MedicationType() {
    TABLET,
    CAPSULE,
    SYRUP,
    INJECTION,
    DROPS,
    INHALER,
    POWDER,
    SPRAY,
    CREAM,
    GEL,
    OINTMENT,
    PATCH,
    OTHER;

    companion object {
        fun fromName(name: String?): MedicationType? {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) }
        }
    }
}
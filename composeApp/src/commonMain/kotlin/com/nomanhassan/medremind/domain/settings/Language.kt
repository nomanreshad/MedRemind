package com.nomanhassan.medremind.domain.settings

import androidx.compose.runtime.staticCompositionLocalOf

enum class Language(
    val code: String,
    val label: String
) {
    // Dynamic Options for AI
    AUTO("auto", "Match Prescription"),
    DEVICE("device", "Device Language"),
    
    // Standard Languages
    ENGLISH("en", "English"),
    BANGLA("bn", "Bangla"),
    GERMAN("de", "German")
}

val LocalizedLanguage = staticCompositionLocalOf { Language.ENGLISH }
package com.nomanhassan.medremind.core.enums

enum class AiImageAnalysisResult {
    PRESCRIPTION_FOUND,
    BLURRY_OR_UNCLEAR,
    NOT_A_PRESCRIPTION;
    
    companion object {
        fun fromName(name: String): AiImageAnalysisResult? {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) }
        }
    }
}
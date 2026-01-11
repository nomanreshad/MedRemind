package com.nomanhassan.medremind.presentation.medication_settings

import com.nomanhassan.medremind.domain.settings.ContrastMode
import com.nomanhassan.medremind.domain.settings.Language
import com.nomanhassan.medremind.domain.settings.ThemePreference

sealed interface MedicationSettingsAction {
    data object OnClickGoBack: MedicationSettingsAction
    data class OnThemeChange(val theme: ThemePreference): MedicationSettingsAction
    data class OnContrastChange(val contrast: ContrastMode): MedicationSettingsAction
    data class OnLanguageChange(val language: Language): MedicationSettingsAction
    data class OnAiLanguageChange(val aiLanguage: Language): MedicationSettingsAction
}
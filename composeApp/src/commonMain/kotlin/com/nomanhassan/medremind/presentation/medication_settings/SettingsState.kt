package com.nomanhassan.medremind.presentation.medication_settings

import com.nomanhassan.medremind.domain.settings.ContrastMode
import com.nomanhassan.medremind.domain.settings.Language
import com.nomanhassan.medremind.domain.settings.ThemePreference

data class SettingsState(
    val isLoading: Boolean = true,
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val contrastMode: ContrastMode = ContrastMode.NORMAL,
    val language: Language = Language.ENGLISH,
    val aiLanguage: Language = Language.AUTO,
)
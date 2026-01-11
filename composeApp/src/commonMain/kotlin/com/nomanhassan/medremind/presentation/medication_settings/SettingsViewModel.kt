package com.nomanhassan.medremind.presentation.medication_settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nomanhassan.medremind.data.local.settings.AppearancePreferences
import com.nomanhassan.medremind.domain.settings.ContrastMode
import com.nomanhassan.medremind.domain.settings.Language
import com.nomanhassan.medremind.domain.settings.Localization
import com.nomanhassan.medremind.domain.settings.ThemePreference
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferences: AppearancePreferences,
    private val localization: Localization
) : ViewModel() {

    val state = combine(
        preferences.themePreference,
        preferences.contrastMode,
        preferences.languagePreference,
        preferences.aiLanguagePreference
    ) { theme, contrast, language, aiLanguage ->
        SettingsState(
            isLoading = false,
            themePreference = theme,
            contrastMode = contrast,
            language = language,
            aiLanguage = aiLanguage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsState()
    )

    fun onAction(action: MedicationSettingsAction) {
        when (action) {
            is MedicationSettingsAction.OnThemeChange -> setThemePreference(action.theme)
            is MedicationSettingsAction.OnContrastChange -> setContrastMode(action.contrast)
            is MedicationSettingsAction.OnLanguageChange -> setLanguage(action.language)
            is MedicationSettingsAction.OnAiLanguageChange -> setAiLanguage(action.aiLanguage)
            else -> Unit
        }
    }

    private fun setThemePreference(preference: ThemePreference) {
        viewModelScope.launch {
            preferences.setThemePreference(preference)
        }
    }

    private fun setContrastMode(mode: ContrastMode) {
        viewModelScope.launch {
            preferences.setContrastMode(mode)
        }
    }

    private fun setLanguage(language: Language) {
        viewModelScope.launch {
            preferences.setLanguagePreference(language)
            localization.applyLanguage(language.code)
        }
    }

    private fun setAiLanguage(language: Language) {
        viewModelScope.launch {
            preferences.setAiLanguagePreference(language)
        }
    }
}
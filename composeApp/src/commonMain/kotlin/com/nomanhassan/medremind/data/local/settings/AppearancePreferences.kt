package com.nomanhassan.medremind.data.local.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nomanhassan.medremind.domain.settings.ContrastMode
import com.nomanhassan.medremind.domain.settings.Language
import com.nomanhassan.medremind.domain.settings.ThemePreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AppearancePreferences(
    private val dataStore: DataStore<Preferences>,
    applicationScope: CoroutineScope
) {
    private val themeKey = stringPreferencesKey("theme_pref")
    private val contrastKey = stringPreferencesKey("contrast_mode_pref")
    private val languageKey = stringPreferencesKey("language_pref")
    private val aiLanguageKey = stringPreferencesKey("ai_language_pref")

    val themePreference = dataStore.data
        .map { prefs ->
            ThemePreference.valueOf(
                prefs[themeKey] ?: ThemePreference.SYSTEM.name
            )
        }
        .distinctUntilChanged()
        .stateIn(
            scope = applicationScope,
            started = SharingStarted.Eagerly,
            initialValue = ThemePreference.SYSTEM
        )

    val contrastMode = dataStore.data
        .map { prefs ->
            ContrastMode.valueOf(
                prefs[contrastKey] ?: ContrastMode.NORMAL.name
            )
        }
        .distinctUntilChanged()
        .stateIn(
            scope = applicationScope,
            started = SharingStarted.Eagerly,
            initialValue = ContrastMode.NORMAL
        )

    val languagePreference = dataStore.data
        .map { prefs ->
            Language.valueOf(
                prefs[languageKey] ?: Language.ENGLISH.name
            )
        }
        .distinctUntilChanged()
        .stateIn(
            scope = applicationScope,
            started = SharingStarted.Eagerly,
            initialValue = Language.ENGLISH
        )

    val aiLanguagePreference = dataStore.data
        .map { prefs ->
            Language.valueOf(
                prefs[aiLanguageKey] ?: Language.AUTO.name
            )
        }
        .distinctUntilChanged()
        .stateIn(
            scope = applicationScope,
            started = SharingStarted.Eagerly,
            initialValue = Language.AUTO
        )

    suspend fun setThemePreference(pref: ThemePreference) {
        dataStore.edit {
            it[themeKey] = pref.name
        }
    }

    suspend fun setContrastMode(mode: ContrastMode) {
        dataStore.edit {
            it[contrastKey] = mode.name
        }
    }

    suspend fun setLanguagePreference(lang: Language) {
        dataStore.edit {
            it[languageKey] = lang.name
        }
    }

    suspend fun setAiLanguagePreference(lang: Language) {
        dataStore.edit {
            it[aiLanguageKey] = lang.name
        }
    }
}
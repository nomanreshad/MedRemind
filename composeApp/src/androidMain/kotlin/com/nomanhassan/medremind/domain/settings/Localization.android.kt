package com.nomanhassan.medremind.domain.settings

import android.content.Context
import android.os.LocaleList
import java.util.Locale

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class Localization(
    private val context: Context
) {
    actual fun applyLanguage(languageCode: String) {
        val locale = Locale.Builder()
            .setLanguage(languageCode)
            .build()
        Locale.setDefault(locale)
        
        val config = context.resources.configuration
        config.setLocales(LocaleList(locale))
    }
}
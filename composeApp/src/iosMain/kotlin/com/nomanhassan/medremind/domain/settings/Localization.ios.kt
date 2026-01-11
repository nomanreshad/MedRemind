package com.nomanhassan.medremind.domain.settings

import platform.Foundation.NSUserDefaults

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class Localization {
    actual fun applyLanguage(languageCode: String) {
        NSUserDefaults.standardUserDefaults.setObject(
            arrayListOf(languageCode),
            "AppleLanguages"
        )
    }
}
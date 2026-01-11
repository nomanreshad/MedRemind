package com.nomanhassan.medremind.domain.settings

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class Localization {
    fun applyLanguage(languageCode: String)
}
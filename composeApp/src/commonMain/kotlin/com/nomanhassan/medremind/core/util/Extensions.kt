package com.nomanhassan.medremind.core.util

import androidx.compose.runtime.Composable
import com.nomanhassan.medremind.core.presentation.toUiText
import com.nomanhassan.medremind.domain.settings.Language
import com.nomanhassan.medremind.domain.settings.LocalizedLanguage
import kotlinx.datetime.Month

private fun String.toBanglaDigits(): String {
    val englishDigits = listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
    val banglaDigits = listOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
    
    var result = this
    englishDigits.forEachIndexed { index, char ->
        result = result.replace(char, banglaDigits[index])
    }
    
    return result
}

private fun Int.toBanglaDigits(): String = this.toString().toBanglaDigits()

@Composable
fun String.toLocalizedDigits(): String {
    return if (LocalizedLanguage.current == Language.BANGLA) {
        this.toBanglaDigits()
    } else this
}

@Composable
fun Int.toLocalizedDigits(): String {
    return if (LocalizedLanguage.current == Language.BANGLA) {
        this.toBanglaDigits()
    } else this.toString()
}

@Composable
fun String.toLocalizedDateString(): String {
    if (this.isBlank()) return this

    // Regex to find the first 3 letters (Month Abbreviation) at the start of the string
    val monthRegex = Regex("^([A-Za-z]{3})")
    val match = monthRegex.find(this) ?: return this.toLocalizedDigits()

    val monthAbbr = match.value

    // Map abbreviation to Month enum using your existing logic
    val monthEnum = Month.entries.find { month ->
        month.name
            .lowercase()
            .replaceFirstChar { it.uppercase() }
            .take(3) == monthAbbr
    }

    return if (monthEnum != null) {
        val localizedMonth = monthEnum.toUiText().asString()
        this.replaceFirst(monthAbbr, localizedMonth).toLocalizedDigits()
    } else this.toLocalizedDigits()
}
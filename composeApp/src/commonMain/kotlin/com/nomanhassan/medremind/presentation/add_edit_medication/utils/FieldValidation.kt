package com.nomanhassan.medremind.presentation.add_edit_medication.utils

import com.nomanhassan.medremind.core.presentation.UiText

sealed interface FieldValidation {
    object Valid: FieldValidation
    data class Invalid(val reason: UiText): FieldValidation
}
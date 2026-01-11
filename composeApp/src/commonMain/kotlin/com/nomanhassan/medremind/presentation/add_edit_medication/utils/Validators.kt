package com.nomanhassan.medremind.presentation.add_edit_medication.utils

import com.nomanhassan.medremind.core.presentation.UiText
import medremind.composeapp.generated.resources.Res
import medremind.composeapp.generated.resources.error_frequency_required
import medremind.composeapp.generated.resources.error_medication_type_required
import medremind.composeapp.generated.resources.error_medicine_name_required
import medremind.composeapp.generated.resources.error_required
import medremind.composeapp.generated.resources.error_start_date_required
import medremind.composeapp.generated.resources.error_too_short

object Validators {
    
    fun validateMedicineName(name: String): FieldValidation {
        val trimmed = name.trim()
        if (trimmed.isBlank()) {
            return FieldValidation.Invalid(
                UiText.StringResourceId(Res.string.error_medicine_name_required)
            )
        }
        if (trimmed.length <= 1) {
            return FieldValidation.Invalid(
                UiText.StringResourceId(Res.string.error_too_short)
            )
        }
        
        return FieldValidation.Valid
    }
    
    fun validateDosageStrength(strength: String): FieldValidation {
        if (strength.isBlank()) return FieldValidation.Valid
        
        return FieldValidation.Valid
    }
    
    fun validateMedicationType(formattedType: String?): FieldValidation {
        if (formattedType.isNullOrBlank()) return FieldValidation.Invalid(
            UiText.StringResourceId(Res.string.error_medication_type_required)
        )
        
        return FieldValidation.Valid
    }
    
    fun validateFrequencyType(formattedType: String?): FieldValidation {
        if (formattedType.isNullOrBlank()) return FieldValidation.Invalid(
            UiText.StringResourceId(Res.string.error_frequency_required)
        )
        
        return FieldValidation.Valid
    }
    
    fun validateTimes(times: List<String?>): FieldValidation {
        return if (times.isNotEmpty() && times.any { it.isNullOrBlank() }) {
            FieldValidation.Invalid(
                UiText.StringResourceId(Res.string.error_required)
            )
        } else if (times.isEmpty()) {
            FieldValidation.Valid
        } else FieldValidation.Valid
    }
    
    fun validateStartDate(date: String): FieldValidation {
        if (date.isBlank()) return FieldValidation.Invalid(
            UiText.StringResourceId(Res.string.error_start_date_required)
        )
        
        return FieldValidation.Valid
    }
    
    fun validateEndDate(date: String?): FieldValidation {
        if (date.isNullOrBlank()) return FieldValidation.Valid
        
        return FieldValidation.Valid
    }

    fun validateNotes(content: String): FieldValidation {
        if (content.isBlank()) return FieldValidation.Valid
        
        return FieldValidation.Valid
    }

    fun validateHospitalName(name: String): FieldValidation {
        if (name.isBlank()) return FieldValidation.Valid
        
        return FieldValidation.Valid
    }

    fun validateDoctorName(name: String): FieldValidation {
        if (name.isBlank()) return FieldValidation.Valid
        
        return FieldValidation.Valid
    }

    fun validateHospitalAddress(name: String): FieldValidation {
        if (name.isBlank()) return FieldValidation.Valid
        
        return FieldValidation.Valid
    }

    fun validateImageUri(uri: String?): FieldValidation {
        if (uri.isNullOrBlank()) return FieldValidation.Valid
        
        return FieldValidation.Valid
    }
}
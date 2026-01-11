package com.nomanhassan.medremind.domain.model

import com.nomanhassan.medremind.core.enums.AiImageAnalysisResult
import com.nomanhassan.medremind.presentation.add_edit_medication.MedicationFormState

data class ImageExtractionResult(
    val analysisResult: AiImageAnalysisResult,
    val forms: List<MedicationFormState>
)

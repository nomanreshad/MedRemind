package com.nomanhassan.medremind.domain.repository

import com.nomanhassan.medremind.core.domain.DataError
import com.nomanhassan.medremind.core.domain.Result
import com.nomanhassan.medremind.domain.model.ImageExtractionResult
import com.nomanhassan.medremind.domain.settings.Language

interface AiPrescriptionRepository {
    suspend fun analyzePrescriptionImage(
        imageData: ByteArray,
        imagePath: String?,
        deviceLanguage: String,
        targetLanguage: Language
    ): Result<ImageExtractionResult, DataError.Remote>
}
package com.nomanhassan.medremind.data.network

import com.nomanhassan.medremind.core.domain.DataError
import com.nomanhassan.medremind.core.domain.Result
import com.nomanhassan.medremind.data.dto.AiPrescriptionResponse
import com.nomanhassan.medremind.domain.settings.Language

interface ImageDataExtractor {
    suspend fun extractPrescriptionData(
        prescriptionImage: ByteArray,
        deviceLanguage: String,
        targetLanguage: Language
    ): Result<AiPrescriptionResponse, DataError.Remote>
}
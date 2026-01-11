package com.nomanhassan.medremind.data.repository

import com.nomanhassan.medremind.core.domain.DataError
import com.nomanhassan.medremind.core.domain.Result
import com.nomanhassan.medremind.data.mapper.toExtractionResult
import com.nomanhassan.medremind.data.network.ImageDataExtractor
import com.nomanhassan.medremind.domain.model.ImageExtractionResult
import com.nomanhassan.medremind.domain.repository.AiPrescriptionRepository
import com.nomanhassan.medremind.domain.settings.Language

class AiPrescriptionRepositoryImpl(
    private val extractor: ImageDataExtractor
): AiPrescriptionRepository {

    override suspend fun analyzePrescriptionImage(
        imageData: ByteArray,
        imagePath: String?,
        deviceLanguage: String,
        targetLanguage: Language
    ): Result<ImageExtractionResult, DataError.Remote> {
        val apiResult = extractor.extractPrescriptionData(
            prescriptionImage = imageData,
            deviceLanguage = deviceLanguage,
            targetLanguage = targetLanguage
        )
        
        return when (apiResult) {
            is Result.Success -> {
                val extractionResult = apiResult.data.toExtractionResult(imagePath)
                Result.Success(extractionResult)
            }
            is Result.Error -> {
                Result.Error(apiResult.error)
            }
        }
    }
}
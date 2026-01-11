package com.nomanhassan.medremind.data.network

import com.nomanhassan.medremind.core.domain.DataError
import com.nomanhassan.medremind.core.domain.Result
import com.nomanhassan.medremind.core.enums.AiImageAnalysisResult
import com.nomanhassan.medremind.core.enums.Frequency
import com.nomanhassan.medremind.core.enums.MedicationType
import com.nomanhassan.medremind.data.dto.AiPrescriptionResponse
import com.nomanhassan.medremind.domain.settings.Language
import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import dev.shreyaspatil.ai.client.generativeai.type.PlatformImage
import dev.shreyaspatil.ai.client.generativeai.type.content
import kotlinx.serialization.json.Json

class AIPrescriptionDataExtractor(
    private val modelName: String,
    private val apiKey: String
): ImageDataExtractor {
    private val model = GenerativeModel(
        modelName = modelName,
        apiKey = apiKey
    )

    private val json = Json { ignoreUnknownKeys = true }
    
    override suspend fun extractPrescriptionData(
        prescriptionImage: ByteArray,
        deviceLanguage: String,
        targetLanguage: Language
    ): Result<AiPrescriptionResponse, DataError.Remote> {
        val validTypes = MedicationType.entries.joinToString(", ") { it.name }
        val validFrequencies = Frequency.entries.joinToString(", ") { it.name }
        val validAnalysisResults = AiImageAnalysisResult.entries.joinToString(", ") { it.name }
        
        val languageInstruction = when (targetLanguage) {
            Language.AUTO -> "Extract and respond using the natural language found in the prescription image."
            Language.DEVICE -> "Translate the human-readable content into $deviceLanguage."
            else -> "Translate the human-readable content into ${targetLanguage.label}."
        }
        
        val prompt = """
            You are a highly accurate medical data extraction AI. Your task is to analyze the provided image.
            First, determine if the image is a clear, legible prescription.
            Follow the language instruction: $languageInstruction
            
            FIELDS THAT MUST NEVER BE TRANSLATED (Use original English Enums):
            - 'imageAnalysisResult': Must be exactly one of [$validAnalysisResults]
            - 'medicationType': Must be exactly one of [$validTypes]
            - 'frequency': Must be exactly one of [$validFrequencies]
            
            FIELDS TO BE TRANSLATED/EXTRACTED:
            - 'medicineName', 'dosageStrength', 'notes', 'doctorName', 'hospitalName', 'hospitalAddress'.
            - Strategy: $languageInstruction
            
            Then, extract data into this strict JSON structure:
            {
              "imageAnalysisResult": "...", // Strictly one of: $validAnalysisResults
              "doctorName": "...",
              "hospitalName": "...",
              "hospitalAddress": "...",
              "medications": [
                {
                  "medicineName": "...", // e.g., "Paracetamol"
                  "dosageStrength": "...", // e.g., "500 mg" or "10 ml"
                  "medicationType": "...", // Strictly one of: $validTypes
                  "frequency": "...",      // Strictly one of: $validFrequencies
                  "notes": "..." // e.g., "After food" or "Take with warm water"
                }
              ]
            }
            
            Rules:
            1. The first field, 'imageAnalysisResult', MUST be one of the provided values: $validAnalysisResults.
            2. If 'imageAnalysisResult' is "NOT_A_PRESCRIPTION" or "BLURRY_OR_UNCLEAR", then set all other fields ('doctorName', 'hospitalName', 'medications', etc.) to empty strings or empty arrays [].
            3. If 'imageAnalysisResult' is "PRESCRIPTION_FOUND", proceed with detailed extraction based on the other fields.
            4. For 'medicationType' and 'frequency', you MUST choose the best fit from the provided lists ($validTypes) and ($validFrequencies). If no clear match is found, use an empty string "".
            5. For 'doctorName', 'hospitalName', and 'hospitalAddress', if the information is not present, leave the field as an empty string "".
            6. Do not translate the medicine names if they are proprietary brand names unless a translation is standard.
            7. Return ONLY raw JSON. Do not include markdown formatting, backticks, or the word "json".
        """.trimIndent()
        
        return try {
            val response = model.generateContent(
                content {
                    image(PlatformImage(prescriptionImage))
                    text(prompt)
                }
            )
            
            val cleanJson = response.text
                ?.replace("```Json", "")
                ?.replace("```json", "")
                ?.replace("```", "")
                ?.trim()
            
            val result = cleanJson?.let {
                json.decodeFromString<AiPrescriptionResponse>(it)
            } ?: return Result.Error(DataError.Remote.SERIALIZATION)
            
            Result.Success(result)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }
}
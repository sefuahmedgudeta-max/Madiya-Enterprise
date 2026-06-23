package com.example.data

import com.example.BuildConfig

object ForecastingService {
    suspend fun getFuelForecast(data: String): String {
        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = "Ragaa gurgurtaa boba'aa kanatti aanu tilmaami: $data")))),
            systemInstruction = Content(parts = listOf(Part(text = "Ati gorsaa raabsa boba'aa ti. Tilmaama gabaa boba'aa gabaabaatti fi sirriitti kenni.")), role = "system")
        )
        return try {
            val response = RetrofitClient.service.generateContent(BuildConfig.GEMINI_API_KEY, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "Tilmaama hin qabu."
        } catch (e: Exception) {
            "Garuu tilmaamuun hin danda'amne: ${e.message}"
        }
    }
}

package com.example.trello.data.repository

import com.example.trello.BuildConfig
import com.example.trello.domain.repository.AiRepository
import com.google.ai.client.generativeai.GenerativeModel
import javax.inject.Inject

class AiRepositoryImpl @Inject constructor() : AiRepository {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-flash-latest",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    override suspend fun getTaskBreakdown(taskDescription: String): Result<String> {
        return try {
            val prompt = "You are an expert project manager. Break down the following task into 3 to 5 actionable sub-tasks. Return ONLY a bulleted list. Task: $taskDescription"
            val response = generativeModel.generateContent(prompt)
            if (response.text.isNullOrBlank()) {
                Result.failure(Exception("Empty response from AI"))
            } else {
                Result.success(response.text!!)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getGeneralAssistantResponse(prompt: String): Result<String> {
        return try {
            val systemPrompt = "You are a helpful assistant in a project management app. The user asks: '$prompt'. Provide a concise, helpful response."
            val response = generativeModel.generateContent(systemPrompt)
            if (response.text.isNullOrBlank()) {
                Result.failure(Exception("Empty response from AI"))
            } else {
                Result.success(response.text!!)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

package com.example.trello.domain.repository

interface AiRepository {
    suspend fun getTaskBreakdown(taskDescription: String): Result<String>
    suspend fun getGeneralAssistantResponse(prompt: String): Result<String>
}

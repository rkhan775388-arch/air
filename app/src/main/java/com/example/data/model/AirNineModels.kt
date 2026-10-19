package com.example.data.model

enum class MessageRole {
    USER,
    ASSISTANT,
    SYSTEM
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val role: MessageRole,
    val content: String,
    val modelName: String = "Gemini 2.5 Flash",
    val timestamp: Long = System.currentTimeMillis(),
    val citations: List<String> = emptyList(),
    val isSpeaking: Boolean = false,
    val isError: Boolean = false
)

data class AiModelInfo(
    val id: String,
    val displayName: String,
    val provider: String = "Google DeepMind",
    val latencyTier: String = "Ultra Fast",
    val contextWindow: String = "1M tokens",
    val badge: String = "Default",
    val isAvailable: Boolean = true
)

data class ProjectSpace(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val systemPrompt: String = "",
    val notes: String = "",
    val documentName: String? = null,
    val documentContent: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

data class TaskFlowItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val category: String = "General",
    val isCompleted: Boolean = false,
    val description: String = ""
)

data class ArenaResult(
    val prompt: String,
    val modelAId: String,
    val modelAName: String,
    val modelAResponse: String,
    val modelALatencyMs: Long,
    val modelBId: String,
    val modelBName: String,
    val modelBResponse: String,
    val modelBLatencyMs: Long,
    val selectedWinner: String? = null
)

data class TrustAnalysis(
    val claim: String,
    val trustScore: Int, // 0 - 100
    val verdict: String,
    val verifiedClaims: List<String>,
    val unsupportedClaims: List<String>,
    val uncertaintyScore: String,
    val sources: List<String>,
    val disclaimer: String = "AI output can contain mistakes. Always review critical information."
)

enum class AppLanguage(val code: String, val displayName: String, val speechLocale: String) {
    ENGLISH("en", "English", "en-US"),
    HINDI("hi", "हिन्दी (Hindi)", "hi-IN")
}

enum class VoiceState {
    IDLE,
    LISTENING,
    PROCESSING,
    SPEAKING,
    ERROR
}

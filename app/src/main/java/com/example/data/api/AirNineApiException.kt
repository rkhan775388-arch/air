package com.example.data.api

sealed class AirNineApiException(
    message: String,
    val userFriendlyMessage: String
) : Exception(message) {

    class MissingApiKeyException(
        userMessage: String = "API key not configured. Please add your GEMINI_API_KEY in the AI Studio Secrets panel."
    ) : AirNineApiException("API Key Missing", userMessage)

    class InvalidCredentialsException(
        userMessage: String = "Invalid or unauthorized API key. Please check your GEMINI_API_KEY in the AI Studio Secrets panel."
    ) : AirNineApiException("Invalid API Key", userMessage)

    class QuotaExceededException(
        userMessage: String = "API rate limit or quota exceeded. Please wait a moment before retrying."
    ) : AirNineApiException("Quota Exceeded", userMessage)

    class ModelUnavailableException(
        userMessage: String = "The selected model is unavailable. Please select another model in the Model Hub."
    ) : AirNineApiException("Model Unavailable", userMessage)

    class NetworkUnavailableException(
        userMessage: String = "Network connection unavailable. Please check your internet connection."
    ) : AirNineApiException("Network Unavailable", userMessage)

    class RequestTimeoutException(
        userMessage: String = "The request timed out. Please try again."
    ) : AirNineApiException("Request Timeout", userMessage)

    class ServerException(
        userMessage: String = "Gemini server encountered an error. Please retry shortly."
    ) : AirNineApiException("Server Error", userMessage)

    class EmptyResponseException(
        userMessage: String = "The model returned an empty response. Please retry."
    ) : AirNineApiException("Empty Response", userMessage)

    class UnknownApiException(
        technicalDetail: String,
        userMessage: String = "AI request could not be completed ($technicalDetail)."
    ) : AirNineApiException("Unknown API Error: $technicalDetail", userMessage)
}
